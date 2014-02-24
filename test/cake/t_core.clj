(ns cake.t-core
  (:use midje.sweet)
  (:use [cake.core]))

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

(def no-change-planned '())


(facts "about plans"

       (fact "No plans required if both current-state and required-state are the same"
             (plan {:name "foo"} {:name "foo"}) => '()
             (plan {:name "foo" :hosts {}} {:name "foo" :hosts {}} ) => '())

       (fact "Invalid environments"
             (plan {} {}) => (throws java.lang.AssertionError "Assert failed: (required-state :name)"))

       (fact "New plan"

             (fact "New environment with no required hosts"
                   (plan {} {:name "env"}) => '((create {:name "env"})))

             (fact "New environment with required hosts"
                   (plan {}
                         {:name :env
                          :hosts {:hosta { :image "ami-foo" }
                                  :hostb { :image "ami-poo"}}})
                   => (fn [plan]
                        (and (= (first plan) '(create { :name :env}))
                             (or (= (second plan) '(create { :name :hosta :image "ami-foo" }))
                                 (= (second plan) '(create { :name :hostb :image "ami-poo" })))
                             (or (= (nth plan 2)  '(create { :name :hosta :image "ami-foo" }))
                                 (= (nth plan 2)  '(create { :name :hostb :image "ami-poo" })))
                             )))) ; Yes, this is insane!

       (fact "Compare with existing empty environment"
             (plan {:name "env"} {:name "env"}) => no-change-planned)


       (fact "Host image change"
             (plan {:name "env" :hosts { :host { :image "ami-ver-1" }}}
                   {:name "env" :hosts { :host { :image "ami-ver-2" }}})
             => '((create {:name :host :image "ami-ver-2"}))))

