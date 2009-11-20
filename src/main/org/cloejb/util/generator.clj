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
     :doc "Provides utilities for generating annotated classes and interfaces"
     :private true}
  org.cloejb.util.generator
  (:import
     (clojure.asm ClassReader ClassWriter)
     (org.cloejb.util annotation_class_adapter)))

(def
  #^{:doc "A map of type, i.e. class or interface, to a function that will
          generate said type."
     :private true}
  generator
  {:interface (ns-resolve 'clojure.core 'generate-interface)
   :class (ns-resolve 'clojure.core 'generate-class)})

(defn- gen-annotated
  "Generates an annotated class or interface with the given annotations."
  [type annotations & options]
  (when *compile-files*
    (let [options-map (apply hash-map options)
          [cname bytecode] ((generator type) options-map)
          class-reader (new ClassReader bytecode)
          class-writer (new ClassWriter class-reader 0)
          class-adapter (new org.cloejb.util.annotation_class_adapter class-writer annotations)]
      (.accept class-reader class-adapter 0)
      (let [new-bytecode (.toByteArray class-writer)]
        (clojure.lang.Compiler/writeClassFile cname new-bytecode)))))
