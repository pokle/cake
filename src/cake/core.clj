(ns cake.core)

;;; This is an incorrect implementation, such as might be written by
;;; someone who was used to a Lisp in which an empty list is equal to
;;; nil.
(defn first-element [sequence default]
  (if (empty? sequence)
    default
    (first sequence)))

(defn survey [environment-name]
  "Survey an environment, and return its current state")


(defn plan [current-state required-state]
  "Works out the steps to go from the current state to the required state"
  (if (empty? current-state)
             (list (list 'create required-state))
             (list)))