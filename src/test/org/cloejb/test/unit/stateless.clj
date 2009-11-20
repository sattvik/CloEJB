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
     :doc "Tests the org.cloejb.ejb namespace by creating EJBs and uses
          reflection to test if they are correctly built."}
  org.cloejb.test.unit.stateless
  (:import [javax.ejb Local Remote Stateless])
  (:use
     org.cloejb.stateless
     org.cloejb.test.unit.util.reflective-class-tester
     clojure.contrib.test-is))

; Generate simple interfaces for testing
(gen-interface
  :name org.cloejb.test.unit.stateless.local
  :methods [[sayHello [] String]])

(gen-interface
  :name org.cloejb.test.unit.stateless.remote
  :methods [[sayHello [] String]])

(defn- check-stateless-annotation
  "Tests the values of a @Stateless annotation."
  [class name description mapped-name]
  (let [annotation (.getAnnotation class Stateless)]
    (is annotation)
    (is (= name (.name annotation)))
    (is (= description (.description annotation)))
    (is (= mapped-name (.mappedName annotation)))))

(defn- check-local-annotation
  "Tests the values of a @Local annotation on stateless EJB."
  [class local-interfaces]
  (let [annotation (.getAnnotation class Local)]
    (is annotation)
    (= local-interfaces (.value annotation))))

(defn- check-remote-annotation
  "Tests the values of a @Remote annotation on stateless EJB."
  [class remote-interfaces]
  (let [annotation (.getAnnotation class Remote)]
    (is annotation)
    (= remote-interfaces (.value annotation))))

; Check simple stateless session EJB
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.simple
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-simple
  :class org.cloejb.test.unit.stateless.simple
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-simple
  "Ensure that the annotation was added and has the default values."
  (check-stateless-annotation org.cloejb.test.unit.stateless.simple "" "" ""))


; Check a stateless session EJB with a name
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.with_name
  :ejb-name with-name
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-with-name
  :class org.cloejb.test.unit.stateless.with_name
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-with-name
  "Ensure that the annotation was added with the name"
  (check-stateless-annotation org.cloejb.test.unit.stateless.with_name "with-name" "" ""))


; Check a stateless session EJB with a description
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.with_desc
  :description with-desc
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-with-desc
  :class org.cloejb.test.unit.stateless.with_desc
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-with-desc
  "Ensure that the annotation was added with the description"
  (check-stateless-annotation org.cloejb.test.unit.stateless.with_desc "" "with-desc" ""))


; Check a stateless session EJB with a mapped name
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.with_mapped_name
  :mapped-name with-mapped-name
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-with-desc
  :class org.cloejb.test.unit.stateless.with_mapped_name
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-with-mapped-name
  "Ensure that the annotation was added with the mapped name"
  (check-stateless-annotation org.cloejb.test.unit.stateless.with_mapped_name "" "" "with-mapped-name"))


; Check a stateless session EJB that declares an empty list of local interfaces
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.with_empty_local
  :local []
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-with-empty-local
  :class org.cloejb.test.unit.stateless.with_empty_local
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-with-empty-local
  "Ensure that the annotation was added with default values"
  (check-stateless-annotation org.cloejb.test.unit.stateless.with_empty_local "" "" ""))

(deftest test-local-with-empty-local
  "Ensure that the @Local annotation was added with an empty list"
  (check-local-annotation org.cloejb.test.unit.stateless.with_empty_local []))


; Check a stateless session EJB that declares a list of local interfaces
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.with_local
  :local [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-with-local
  :class org.cloejb.test.unit.stateless.with_local
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-with-local
  "Ensure that the annotation was added with default values"
  (check-stateless-annotation org.cloejb.test.unit.stateless.with_local "" "" ""))

(deftest test-local-with-local
  "Ensure that the @Local annotation was added with the correct interfaces"
  (check-local-annotation org.cloejb.test.unit.stateless.with_local []))


; Check a stateless session EJB that declares an empty list of remote interfaces
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.with_empty_remote
  :remote []
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-with-empty-remote
  :class org.cloejb.test.unit.stateless.with_empty_remote
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-with-empty-remote
  "Ensure that the annotation was added with default values"
  (check-stateless-annotation org.cloejb.test.unit.stateless.with_empty_remote "" "" ""))

(deftest test-remote-with-empty-remote
  "Ensure that the @Remote annotation was added with an empty list"
  (check-remote-annotation org.cloejb.test.unit.stateless.with_empty_remote []))


; Check a stateless session EJB that declares a list of remote interfaces
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.with_remote
  :remote [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-with-remote
  :class org.cloejb.test.unit.stateless.with_remote
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-with-remote
  "Ensure that the annotation was added with default values"
  (check-stateless-annotation org.cloejb.test.unit.stateless.with_remote "" "" ""))

(deftest test-remote-with-remote
  "Ensure that the @Remote annotation was added with the correct interfaces"
  (check-remote-annotation org.cloejb.test.unit.stateless.with_remote []))


; Check a complete example of a stateless session EJB
(gen-stateless-ejb
  :name org.cloejb.test.unit.stateless.complete
  :ejb-name complete
  :remote [org.cloejb.test.unit.stateless.local]
  :local [org.cloejb.test.unit.stateless.remote]
  :implements [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :main false)

(defclasstest test-class-complete
  :class org.cloejb.test.unit.stateless.complete
  :superclass Object
  :interfaces [org.cloejb.test.unit.stateless.local org.cloejb.test.unit.stateless.remote]
  :methods [[sayHello [] String]])

(deftest test-annotations-complete
  "Ensure that the annotation was added with given name"
  (check-stateless-annotation org.cloejb.test.unit.stateless.complete "complete" "" ""))

(deftest test-local-complete
  "Ensure that the @Local annotation was added with the correct interfaces"
  (check-local-annotation org.cloejb.test.unit.stateless.complete [org.cloejb.test.unit.stateless.local]))

(deftest test-remote-complete
  "Ensure that the @Remote annotation was added with the correct interfaces"
  (check-remote-annotation org.cloejb.test.unit.stateless.complete [org.cloejb.test.unit.stateless.remote]))
