(ns bible.web.reading-lists.views
  (:require [re-frame.core :as rf]
            [bible.web.reading-lists.subs :as reading-lists.subs]))


(defn reading-list []
  (let [reading-lists @(rf/subscribe [reading-lists.subs/reading-lists-view])]
    [:div
     (->> reading-lists
          (map (fn [{:keys [id title chapter read-index]}]
                 [:div {:key id}
                  [:small title]
                  [:br]
                  [:label chapter]])))]))
