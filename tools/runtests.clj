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
     duck-streams
     test-is))

(def #^{:private true} usage
"Usage: runtest.clj [--file FILE] namespace ...

A simple wrapper around the functionality of clojure.contrib.test-is.  Runs
tests on the given namespaces.
")

(defn- run-ns-tests
  "Runs the various tests and returns a summary of the results as a map."
  [namespaces]
  (let [ns-symbols (map symbol namespaces)]
    (do
      (apply require ns-symbols)
      (assoc (apply merge-with + (map test-ns ns-symbols)) :type :summary))))

(defn- failures-or-errors?
  "Returns true if there were failures or errors"
  [summary]
  (not (zero? (+ (:fail summary) (:error summary)))))

(with-command-line
  *command-line-args*
  usage
  [[file  f "The file to which to write the test report."]
   [depth d "The stack depth to show in the case of an error"]
    namespaces]
  (binding [*stack-trace-depth* (when depth (Integer/parseInt depth))
            *test-out* (if (nil? file) *out* (writer file))]
    (let [summary (run-ns-tests namespaces)]
      (do
        (report summary)
        (when file (.close *test-out*))
        (when (failures-or-errors? summary)
          (System/exit 1))))))
