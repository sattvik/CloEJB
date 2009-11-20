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
     :doc "Tests the org.cloejb.local namespace by creating a local interface
          and testing if it is correctly built using reflection."}
  org.cloejb.test.unit.local
  (:use
     org.cloejb.local
     org.cloejb.test.unit.util.reflective-class-tester
     clojure.contrib.test-is)
  (:import javax.ejb.Local))

(def #^{:private true}
  classname
  "org.cloejb.local_test_interface")

(def #^{:private true}
  interface-names
  [java.lang.Runnable java.io.Serializable])

; Generate a local interface against which to test
(gen-local-interface
  :name org.cloejb.test.unit.local.interface
  :extends [java.lang.Runnable java.io.Serializable]
  :methods [[setSomething [String] void]
            [getSomething [] String]])

(defclasstest local-interface-class-test
  :class org.cloejb.test.unit.local.interface
  :interfaces [java.lang.Runnable java.io.Serializable]
  :methods [[setSomething [String] void]
            [getSomething [] String]])

(deftest test-annotations
  "Most importantly, ensure that the annotation was added."
  (let [annotation (.getAnnotation org.cloejb.test.unit.local.interface Local)]
    (is annotation)
    (is (empty? (.value annotation)))))
