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
     :doc "Tests the org.cloejb.remote namespace by creating a remote interface
          and testing if it is correctly built using reflection."}
  org.cloejb.test.unit.remote
  (:use
     org.cloejb.remote
     org.cloejb.test.unit.util.reflective-class-tester
     clojure.contrib.test-is)
  (:import javax.ejb.Remote))

(def #^{:private true}
  classname
  "org.cloejb.test.unit.remote.interface")

(def #^{:private true}
  interface-names
  [java.lang.Runnable java.io.Serializable])

; Generate a remote interface against which to test
(gen-remote-interface
  :name org.cloejb.test.unit.remote.interface
  :extends [java.lang.Runnable java.io.Serializable]
  :methods [[setSomething [String] void]
            [getSomething [] String]])

(defclasstest remote-interface-class-test
  :class org.cloejb.test.unit.remote.interface
  :interfaces [java.lang.Runnable java.io.Serializable]
  :methods [[setSomething [String] void]
            [getSomething [] String]])

(deftest test-annotations
  "Most importantly, ensure that the annotation was added."
  (let [annotation (.getAnnotation org.cloejb.test.unit.remote.interface Remote)]
    (is annotation)
    (is (empty? (.value annotation)))))
