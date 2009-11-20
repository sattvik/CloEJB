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
     :doc "Uses reflection to test the properties of a class."}
  org.cloejb.test.unit.util.reflective-class-tester
  (:use clojure.contrib.test-is))

(def
  #^{:doc "The ‘void’ return type as a keyword."}
  void Void/TYPE)

(defn- test-method
  "Tests to see if a method exists in the class and returns the correct type."
  [[name param-types return-type] class]
  (try
    (let [method (.getMethod class name (into-array Class param-types))]
      (is (= return-type (.getReturnType method))))
    (catch NoSuchMethodException e
      (report {:type :fail
               :expected [name param-types return-type]
               :actual (.getClass e)}))))

(defn do-reflective-tests
  "Actually performs the reflective class tests."
  [{:keys [class superclass interfaces methods omit-object-methods]}]
  (assert class)
  (is (= superclass (.getSuperclass class)))
  (is (= interfaces (vec (.getInterfaces class))))
  (dorun (map test-method methods (repeat class))))

(defmacro defclasstest
  "Verifies the structure of a class by performing a series of reflective
  tests.  This function takes a map as a parameter with the following keys:

  :class [required]

     The class to test (as a Class object)

  :superclass [required, see description]

    The superclass of the class given in :class; if :class is an interface, a
    primitive, void, or java.lang.Object, it may be omitted.

  :interfaces [required, see description]

    If :class is an interface, a vector of interfaces the :class extends.  If
    :class is a class, a vector of interfaces the :class implements.  If class
    does not extend or implement interfaces, it may be omitted.

  :methods [optional]

    A vector of methods to check to see if they are defined in :class.  If
    omitted or nil, this test will be skipped.  Use a format similar to
    defining methods using ‘gen-class’."
  [name & options]
  (when *load-tests*
    (let [{:keys [class superclass interfaces methods]
           :or {:interfaces []}} (apply hash-map options)
          option-map {:class class
                      :superclass superclass
                      :interfaces interfaces
                      :methods (when methods
                                 (apply vector (map (fn [[m p r]] [(str m) p r]) methods)))}]
      `(def 
         ~(with-meta name {:test `(fn [] (do-reflective-tests ~option-map))})
         (fn [] (test-var (var ~name)))))))
