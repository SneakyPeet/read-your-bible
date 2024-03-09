(ns bible.web.projections.subs
  (:require [re-frame.core :as rf]
            [bible.web.projections.state :as projections.state]
            [bible.domain.projections :as domain.projections]))


(defn round-2 [n]
  (-> n
      (* 100)
      js/Math.round
      (/ 100)
      double))


(rf/reg-sub
  ::projection-state-by-type
  (fn [db]
    (projections.state/projection-state-by-type db)))


(rf/reg-sub
  ::times-read
  :<- [::projection-state-by-type]
  (fn [projections]
    (let [{:keys [bible testaments]} (get projections domain.projections/projection-type-times-read)]
      (into [["Bible" (round-2 (* 100 bible))]]
            (map (fn [{:keys [testament total]}] [testament (* 100 total)]) testaments)))))

(def times-read-sub ::times-read)
