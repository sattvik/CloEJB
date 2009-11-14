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
     :doc "Provides utilities for generating EJB business interfaces."
     :private true}
  org.cloejb.util.interface-generator
  (:import
     (clojure.asm ClassReader ClassWriter)
     (org.cloejb.util interface_adapter)))

(def
  #^{:doc "Reference to private clojure.core/generate-interface function"
     :private true }
  generate-interface
  (ns-resolve 'clojure.core 'generate-interface))

(defn- gen-ejb-interface
  "Generates an EJB business interface of the given type."
  [type & options]
  (when *compile-files*
    (let [options-map (apply hash-map options)
          [cname bytecode] (generate-interface options-map)
          class-reader (new ClassReader bytecode)
          class-writer (new ClassWriter class-reader 0)
          class-adapter (new org.cloejb.util.interface_adapter class-writer type)]
      (.accept class-reader class-adapter 0)
      (let [new-bytecode (.toByteArray class-writer)]
        (clojure.lang.Compiler/writeClassFile cname new-bytecode)))))
