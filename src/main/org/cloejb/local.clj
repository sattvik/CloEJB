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
     :doc "Provides a macro to generate local business interfaces."}
  org.cloejb.local
  (:import javax.ejb.Local)
  (:require org.cloejb.util.generator))

(defmacro gen-local-interface
  "Generates a local interface.  This macro takes the same arguments as
  Clojure’s gen-interface macro.  The compiled interface will contain a
  @javax.ejb.Local annotation."
  [& options]
  (let [gen-ejb-interface (ns-resolve 'org.cloejb.util.generator 'gen-annotated)]
    (apply gen-ejb-interface :interface { Local nil } options)))
