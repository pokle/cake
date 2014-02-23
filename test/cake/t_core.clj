(ns cake.t-core
  (:use midje.sweet)
  (:use [cake.core]))

(facts "about `first-element`"
  (fact "it normally returns the first element"
    (first-element [1 2 3] :default) => 1
    (first-element '(1 2 3) :default) => 1)

  ;; I'm a little unsure how Clojure types map onto the Lisp I'm used to.
  (fact "default value is returned for empty sequences"
    (first-element [] :default) => :default
    (first-element '() :default) => :default
    (first-element nil :default) => :default
    (first-element (filter even? [1 3 5]) :default) => :default))


;; Might want a recursive state definition so that hosts and containers
;; look similar
;; At the very least, a host has a name, an image, and child hosts
;; An environment could be seen as a single host :-)
;; Levels: environment -> host -> container
(def sample-recursive
  {:name "recursive-environment"
   :hosts {
            :host-a { :image "ami-simple-host-1" }
            :host-b { :image "ami-simple-host-1" }
            :host-c { :image "ami-container-host-1"
                      :hosts {
                              :pdf-service-container { :image "pdf-service:1.0" }}}}})


(facts "about plans"
       (fact "No plans required if both current-state and required-state are the same"
             (plan {:name "foo"} {:name "foo"}) => '()
             (plan {:name "foo" :hosts {}} {:name "foo" :hosts {}} ) => '())

       (fact "Invalid environments"
             (plan {} {}) => (throws java.lang.AssertionError "Assert failed: (required-state :name)"))

       (fact "New plan generates an environment"

             ; Environment with no hosts
             (plan {} {:name "env"}) => '((create {:name "env"}))

             ; Environment with hosts
             (plan {} {:name "env" :hosts {:a {} :b {}}})
                   => '(
                         (create "env")
                         (create "env" :a)
                         (create "env" :b))))

