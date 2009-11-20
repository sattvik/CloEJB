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
     gen-html-docs))

(def #^{:private true} usage
"Usage: gendocs.clj [--file FILE] namespace ...

A simple wrapper around the functionality of clojure.contrib.gen-html-docs.
Generates documentation for the given namespaces.
")

(with-command-line
  *command-line-args*
  usage
  [[file "The file to which to write the generated HTML documentation."]
    namespaces]
  (let [ns-symbols (map symbol namespaces)]
    (if (nil? file)
      (generate-documentation ns-symbols)
      (generate-documentation-to-file file ns-symbols))))
