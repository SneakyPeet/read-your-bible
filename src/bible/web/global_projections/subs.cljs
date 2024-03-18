(ns bible.web.global-projections.subs
  (:require [re-frame.core :as rf]
            [bible.web.global-projections.state :as global-projections.state]))


(rf/reg-sub
  ::daily-read-stat
  (fn [db]
    (global-projections.state/current-daily-stat db)))


(defn daily-read-stat-sub []
  (rf/subscribe [::daily-read-stat]))
