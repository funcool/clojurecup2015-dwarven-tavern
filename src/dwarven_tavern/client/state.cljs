(ns dwarven-tavern.client.state
  (:require [beicon.core :as rx]
            [promesa.core :as prom]))

(def initial-state
  {:player :dialelo
   :location :home
   :room-list []
   :current-game {:room "Room 1"
                  :width 10
                  :height 10
                  :total-time 10
                  :time-progress 9
                  :players {:dialelo {:pos [1 2]
                                      :dir :south}
                            :alotor {:pos [3 4]
                                     :dir :north}}
                  :team1 {:score 1
                          :members [:dialelo]}
                  :team2 {:score 2
                          :members [:alotor]}
                  :barrel {:pos [5 5]
                           :dir :south}}})

(defmulti transition (fn [state [ev]] ev))

(defn transact!
  [db ev]
  (let [tx (transition @db ev)]
    (println "Transition: " tx)
    (cond
      (rx/observable? tx)
      (rx/on-value tx (fn [acc]
                        (println acc)
                        (transact! db acc)))

      (prom/promise? tx)
      (prom/then tx #(transact! db %))

      :else
      (reset! db tx))))
