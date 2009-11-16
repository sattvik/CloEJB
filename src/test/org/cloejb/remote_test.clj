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
          and testing if it is correctly built using reflection.."}
  org.cloejb.remote-test
  (:refer-clojure :exclude [get-method])
  (:use
     org.cloejb.remote
     clojure.contrib.test-is)
  (:import javax.ejb.Remote))

(def #^{:private true}
  classname
  "org.cloejb.remote_test_interface")

(def #^{:private true}
  interface-names
  [java.lang.Runnable java.io.Serializable])

; Generate a remote interface against which to test
(gen-remote-interface
  :name org.cloejb.remote_test_interface
  :extends [java.lang.Runnable java.io.Serializable]
  :methods [[setSomething [String] void]
            [getSomething [] String]])

(def
  #^{:private true
     :doc "The ‘void’ return type as a keyword."}
  void Void/TYPE)

(def
  #^{:private true
     :doc "An instance of the class of the interface defined above"}
  interface (atom nil))

(defn load-interface [f]
  "A test fixture that sets up the ‘interface’ def."
  (do
    (when (nil? @interface)
      (swap! interface (constantly (Class/forName classname))))
    (f)))

(defn- method
  "Returns a named method from the interface."
  [name args]
  (.getMethod @interface name (into-array Class args)))

(defn- return-type
  "Gets the simple name of the return type of a method."
  [method]
  (.getReturnType method))

(defn- parameter-types
  "Gets the parameter types for a method in a vector."
  [method]
  (vec (.getParameterTypes method)))

(defn- test-method
  "Helper method to test the definition of a method."
  [ret-type name param-types]
  (let [method (method name param-types)]
    (is (= param-types (parameter-types method)))
    (is (= ret-type (return-type method)))))

(use-fixtures :once load-interface)

(deftest test-class-name
  "Test that the class’s name is correct."
  (is (= classname (.getName @interface))))

(deftest test-superclass
  "Tests that the interface has no superclass."
  (is (nil? (.getSuperclass @interface))))

(deftest test-interfaces
  "Tests that the interface extends the requested interfaces."
  (is (= interface-names (vec (.getInterfaces @interface)))))

(deftest test-num-methods
  "Ensures there are the correct number of methods."
  (is (= 3 (alength (.getMethods @interface)))))

(deftest test-run-method
  "Ensures void run() exists."
  (test-method void "run" []))

(deftest test-get-method
  "Ensures String getSomething() exists."
  (test-method String "getSomething" []))

(deftest test-set-method
  "Ensures void setSomething() exists."
  (test-method void "setSomething" [String]))

(deftest test-annotations
  "Most importantly, ensure that the annotation was added."
  (let [annotations (.getAnnotation @interface Remote)]
    (is (not (nil? annotations)))
    (is (empty? (.value annotations)))))
