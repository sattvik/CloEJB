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
     :doc "Defines methods for generating a stateless session bean."}
  org.cloejb.stateless
  (:import [javax.ejb Local Remote Stateless])
  (:require org.cloejb.util.generator))

(defmacro gen-stateless-ejb
  "Generates a stateless session bean with the given properties.  This macro
  takes the same arguments as Clojure’s gen-class macro.  In addition, it
  recognizes the following optional arguments:

  :ejb-name <name>

  The name of the stateless session bean.  Equivalent to specifying the ‘name’
  argument to the @Stateless annotation or the <ejb-name> element in an XML
  deployment descriptor.  By default, an EJB’s name is the unqualified name of
  the bean class.  It must be unique among the names of enterprise beans in an
  ejb-jar file.

  :description <description>

  A description  of the stateless session bean.  Equivalent to specifying the
  ‘description’ argument to the @Stateless annotation or the <description>
  element in an XML deployment descriptor.  Defaults to an empty string.

  :mapped-name <mapped-name>

  A product-specific name to which to map the session bean.  Using a mapped
  name is not portable and not recommended for production use.  Defaults to an
  empty string.

  :local [ <interfaces> ]

  Declares the local business interfaces for the bean.  These must be fully
  qualified class names.  By default, the @Local annotation is not added to the
  bean.

  :remote [ <interfaces> ]

  Declares the remote business interfaces for the bean.  These must be fully
  qualified class names.  By default, the @Remote annotation is not added to
  the bean."
  [& options]
  (let [gen-ejb-class (ns-resolve 'org.cloejb.util.generator 'gen-annotated)
        {:keys [ejb-name
                description
                mapped-name
                local
                remote]
         :or {ejb-name ""
              description ""
              mapped-name ""}} (apply hash-map options)
        stateless-data {"name" (str ejb-name)
                        "description" (str description)
                        "mappedName" (str mapped-name)}
        annotations (merge {Stateless stateless-data}
                           (when local {Local (vec (doall (map (fn [x] (Class/forName (str x))) local)))})
                           (when remote {Remote (vec (doall (map (fn [x] (Class/forName (str x))) remote)))}))]
    (apply gen-ejb-class :class annotations options)))
