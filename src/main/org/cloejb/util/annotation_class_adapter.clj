; Copyright © 2009 Sattvik Software & Technology Resources, Ltd. Co.
; All rights reserved.
;
; This program and the accompanying materials are made available under the
; terms of the Eclipse Public License v1.0 which accompanies this distribution,
; and is available at http://opensource.org/licenses/eclipse-1.0.php.
;
; By using this software in any fashion, you are agreeing to be bound by the
; terms of this license.  You must not remove this notice, or any other, from
; this software.

(ns
  #^{:author "Daniel Solano Gómez"
     :doc "Defines an ASM class adapter that has the ability to add
          annotations."
     :private true}
  org.cloejb.util.annotation-class-adapter
  (:import
     (clojure.asm Opcodes Type)
     (clojure.lang IPersistentMap IPersistentVector))
  (:gen-class
     :extends clojure.asm.ClassAdapter
     :init init
     :state state
     :main false
     :constructors {[clojure.asm.ClassVisitor clojure.lang.IPersistentMap] [clojure.asm.ClassVisitor]}
     :exposes {cv {:get get-visitor}}))

(defstruct
  #^{:private true
     :doc "The structure of an annotation:
          :class     Class of the annotation
          :present   Whether or not the annotation is present"}
  annotation :class :present :value)


(defn- to-struct [type]
  (struct annotation (Type/getDescriptor (key type)) (atom false) (val type)))

(defn- -init
  "Initialises the interface adapter"
  [class-writer annotations]
  [ [class-writer] (map to-struct annotations)])

(defn- require-version
  "Upgrades a version opcode to a given minimal version."
  [version min-version]
  (if (< (bit-and version 0xff) min-version)
    min-version
    version))

(defn- require-version-5
  "Updates a version opcode to at least version 1.5"
  [version]
  (require-version version Opcodes/V1_5))

(defn- not-present?
  "Returns whether or not a given annotation is not present."
  [annotation]
  (not @(:present annotation)))

(defmulti visit-annotation
  "Performs the creation of an annotation and its contents."
  {:private true}
  (fn [visitor name value ] [(if name :named :unnamed) (class value)]))

(defmethod visit-annotation [:named String]
  [visitor name value]
  (.visit visitor name value))

(defmethod visit-annotation [:named Class]
  [visitor name value]
  (.visit visitor name (Type/getType value)))

(defmethod visit-annotation [:unnamed nil] [_ _ _]
  nil)

(defmethod visit-annotation [:unnamed IPersistentMap]
  [visitor _ value-map]
  (dorun (map #(visit-annotation visitor %1 %2)
           (keys value-map)
           (vals value-map))))

(defmethod visit-annotation [:unnamed IPersistentVector]
  [visitor _ value-vector]
  (let [array-visitor (.visitArray visitor "default")]
    (dorun (map #(visit-annotation array-visitor "" %) value-vector))
    (.visitEnd array-visitor)))

(defn- add-annotation
  "Adds the requested annotation to the class using the given visitor."
  [annotation class-visitor]
  (let [annotation-visitor (.visitAnnotation class-visitor (:class annotation) true)]
    (when annotation-visitor
      (visit-annotation annotation-visitor nil (:value annotation))
      (.visitEnd annotation-visitor)))
  (swap! (:present annotation) (constantly true)))

(defn- add-annotations
  "Adds any missing annotations to the class using the given visitor."
  [annotations class-visitor]
  (dorun (map #(add-annotation %1 class-visitor) (filter not-present? annotations))))

(defn- find-annotation
  [annotations desired-type]
  (filter (fn [a] (= (:class a) desired-type)) annotations))

(defmacro #^{:private true}
  get-visitor
  "Returns the superclass’s ClassVisitor instance."
  []
  `(.get-visitor ~'this))

(defn -visit
  "Ensures that the class’s versio is at least 1.5"
  [this version access name signature super-name interfaces]
  (let [newversion (require-version-5 version)]
    (.visit (.get-visitor this) newversion access name signature super-name interfaces)))

(defn -visitAnnotation
  "Checks to see if the desired annotation has been added."
  [this description visible?]
  (let [annotations (.state this)
        annotation (find-annotation annotations description)]
    (when annotation
      (swap! (:present annotation) (constantly true))))
  (.visitAnnotation (get-visitor) description visible?))

(defn -visitInnerClass
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this name outer inner access]
  (let [annotations (.state this)
        visitor (get-visitor)]
    (add-annotations annotations visitor)
    (.visitMethod visitor name outer inner access)))

(defn -visitField
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this access name desc sig value]
  (let [annotations (.state this)
        visitor (get-visitor)]
    (add-annotations annotations visitor)
    (.visitField visitor access name desc sig value)))

(defn -visitMethod
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this access name desc sig exceptions]
  (let [annotations (.state this)
        visitor (get-visitor)]
    (add-annotations annotations visitor)
    (.visitMethod visitor access name desc sig exceptions)))

(defn -visitEnd
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this]
  (let [annotations (.state this)
        visitor (get-visitor)]
    (add-annotations annotations visitor)
    (.visitEnd visitor)))
