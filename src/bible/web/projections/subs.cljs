(ns bible.web.projections.subs
  (:require [re-frame.core :as rf]
            [bible.web.projections.state :as projections.state]
            [bible.domain.projections :as domain.projections]))

(rf/reg-sub
  ::projection-state-by-type
  (fn [db]
    (projections.state/projection-state-by-type db)))


(rf/reg-sub
  ::times-read
  :<- [::projection-state-by-type]
  (fn [projections]
    (-> (get projections domain.projections/projection-type-times-read)
        (select-keys [:testaments :bible]))))

(def times-read-sub ::times-read)
