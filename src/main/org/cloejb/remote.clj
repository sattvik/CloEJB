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
     :doc "Provides a macro to generate remote business interfaces."}
  org.cloejb.remote)

(def #^{:private true
        :doc "The secret name of the javax.ejb.Remote annotation"}
  javax_ejb_Remote "Ljavax/ejb/Remote;")

(defmacro gen-remote-interface
  "Generates a remote interface.  This macro takes the same arguments as
  Clojure’s gen-interface macro.  The compiled will interface contain a
  @javax.ejb.Remote annotation."
  [& options]
  (let [gen-ejb-interface (ns-resolve 'org.cloejb.util.interface-generator 'gen-ejb-interface)]
    (apply gen-ejb-interface javax_ejb_Remote options)))
