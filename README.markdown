CloEJB: Integrating Clojure and EJB
===================================

The CloEJB project seeks to simplify integration of EJB technology with the
Clojure programming language.  Using Java from within Clojure is easy, and (as
of version 1.0) Clojure provides the `gen-interface` and `gen-class` macros so
that Java can use Clojure.  However, there is at least one major deficiency of
these macros: no support for Java annotations.  As a result, in order to use
Clojure from an EJB container, you must rely on XML deployment descriptors.
