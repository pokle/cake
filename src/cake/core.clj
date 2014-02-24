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


(defn plan-hosts [current-hosts required-hosts]

  (map (fn [[required-host-name required-host-spec]]
         (let [current-host-spec (get current-hosts required-host-name)]
           (if-not (= current-host-spec required-host-spec)
             (list 'create (assoc required-host-spec :name required-host-name))
             nil
             )
           )
         ) required-hosts))

(defn plan [current-state required-state]

  "Works out the steps to go from the current state to the required state"

  (assert (required-state :name))

  (if (empty? current-state)
    (conj
      (plan-hosts (get current-state :hosts) (get required-state :hosts))
      (list 'create (dissoc required-state :hosts)))
    (plan-hosts (get current-state :hosts) (get required-state :hosts))))


