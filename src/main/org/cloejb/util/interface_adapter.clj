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
     :doc "Defines a class that has the ability to annotate an interface."
     :private true}
  org.cloejb.util.interface_adapter
  (:import (clojure.asm Opcodes))
  (:gen-class
     :extends clojure.asm.ClassAdapter
     :init init
     :state state
     :main false
     :constructors {[clojure.asm.ClassVisitor String] [clojure.asm.ClassVisitor]}
     :exposes {cv {:get get-visitor :set set-visitor}})
  )

(defn- -init
  "Initialises the interface adapter"
  [class-writer type]
  [ [class-writer] {:annotation-present (atom false) :interface-type type}])

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

(defmacro #^{:private true}
  get-state
  "Returns the state variable associated with the given keyword."
  [kw]
  `(~kw (.state ~'this)))

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
  (when (and (visible? (= description (get-state :interface-type))))
    (swap! (get-state :annotation-present) (constantly true)))
  (.visitAnnotation (get-visitor) description visible?))

(defmacro #^{:private true}
  add-annotation
  "Adds the requested annotation to the interface"
  []
  `(when (compare-and-set! (get-state :annotation-present) false true)
     (let [class-visitor# (get-visitor)
           annotation-visitor# (.visitAnnotation class-visitor# (get-state :interface-type) true)]
       (when (not (nil? annotation-visitor#))
         (.visitEnd annotation-visitor#)))))

(defn -visitInnerClass
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this name outer inner access]
  (do
    (add-annotation)
    (.visitMethod (get-visitor) name outer inner access)))

(defn -visitField
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this access name desc sig value]
  (do
    (add-annotation)
    (.visitMethod (get-visitor) access name desc sig value)))

(defn -visitMethod
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this access name desc sig exceptions]
  (do
    (add-annotation)
    (.visitMethod (get-visitor) access name desc sig exceptions)))

(defn -visitEnd
  "Hooks into the add-annotation macro to ensure the requested annotation has
  been added."
  [this]
  (do
    (add-annotation)
    (.visitEnd (get-visitor))))
