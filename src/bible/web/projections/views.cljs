(ns bible.web.projections.views
  (:require [re-frame.core :as rf]
            [bible.web.projections.subs :as projections.subs]
            ))


(defn read-counts []
  (let [{:keys [testaments bible]} @(rf/subscribe [projections.subs/times-read-sub])]
    [:div
     [:div.level.is-mobile
      [:div.level-item.has-text-centered
       [:div
        [:div.heading "BIBLE"]
        [:div.title bible]]]
      (->> testaments
           (map (fn [{:keys [testament total]}]
                  [:div.level-item.has-text-centered {:key testament}
                   [:div
                    [:div.heading testament]
                    [:div.title total]]])))]]))
