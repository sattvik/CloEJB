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
     :doc "Provides utilities for generating annotated EJB classes."
     :private true}
  org.cloejb.util.class-generator
  (:import
     (clojure.asm ClassReader ClassWriter)
     (org.cloejb.util annotation_class_adapter)))

(def
  #^{:doc "Reference to private clojure.core/generate-class function"
     :private true }
  generate-class
  (ns-resolve 'clojure.core 'generate-class))

(defn- gen-ejb-class
  "Generates an EJB class with the requested annotations."
  [class-annotations options-map]
  (when *compile-files*
    (let [[cname bytecode] (generate-class options-map)
          class-reader (new ClassReader bytecode)
          class-writer (new ClassWriter class-reader ClassWriter/COMPUTE_MAXS)
          class-adapter (new org.cloejb.util.annotation_class_adapter class-writer class-annotations)]
      (.accept class-reader class-adapter 0)
      (let [new-bytecode (.toByteArray class-writer)]
        (clojure.lang.Compiler/writeClassFile cname new-bytecode)))))
