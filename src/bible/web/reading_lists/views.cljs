(ns bible.web.reading-lists.views
  (:require [re-frame.core :as rf]
            [bible.web.reading-lists.subs :as reading-lists.subs]
            [bible.web.reading-lists.events :as reading-lists.events]))


(defn reading-list []
  (let [reading-lists @(rf/subscribe [reading-lists.subs/reading-lists-view])]
    [:div
     (->> reading-lists
          (map (fn [{:keys [id title chapter]}]
                 [:div {:key id}
                  [:small title]
                  [:br]
                  [:label chapter]
                  [:button {:on-click #(reading-lists.events/increment-reading-list-index id)}
                   ">"]])))]))
