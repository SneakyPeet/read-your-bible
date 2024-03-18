(ns bible.web.global-projections.views
  (:require [bible.web.global-projections.subs :as global-projections.subs]))

(defn daily-read-stat []
  (let [{:keys [total-users total-events]} @(global-projections.subs/daily-read-stat-sub)]
    [:div
     (when total-users
       [:div.heading.has-text-primary
        total-users " people"
        " have read "
        total-events " chapters"
        " today"])]))
