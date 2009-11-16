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


(use 
  '(clojure.contrib
     command-line
     test-is))

(def #^{:private true} usage
"Usage: runtest.clj [--file FILE] namespace ...

A simple wrapper around the functionality of clojure.contrib.test-is.  Runs
tests on the given namespaces.
")

;(defn- run-tests [& namespaces]
;  (let [summary (assoc (apply merge-with + (map test-ns namespaces)) :type :summary)]
;    (report summary)
;    summary))

(defn- run-ns-tests
  "Runs the various tests and returns a summary of the results as a map."
  [namespaces &]
  (assoc (apply merge-with + (map test-ns namespaces)) :type :summary))

(defn- failures-or-errors?
  [summary]
  (not (zero? (+ (:fail summary) (:error summary)))))


(with-command-line
  *command-line-args*
  usage
  [[file "The file to which to write the test report."]
    namespaces]
  (let [ns-symbols (map symbol namespaces)]
    (apply require ns-symbols)
    (let [summary (run-ns-tests ns-symbols)]
      (do
        (report summary)
        (when (failures-or-errors? summary)
          (System/exit 1))))))
