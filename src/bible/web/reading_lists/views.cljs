(ns bible.web.reading-lists.views
  (:require [re-frame.core :as rf]
            [bible.web.reading-lists.subs :as reading-lists.subs]
            [bible.web.reading-lists.events :as reading-lists.events]))


(defn reading-list []
  (let [reading-lists @(rf/subscribe [reading-lists.subs/reading-lists-view])
        allow-increment @(rf/subscribe [reading-lists.subs/allow-increment-sub])]
    (if (empty? reading-lists)
      [:h1 "LOADING"]
      [:div
       (->> reading-lists
            (map (fn [{:keys [id title chapter]}]
                   [:div.is-flex.is-flex-wrap-nowrap.is-justify-content-space-between.is-align-items-center.mb-2
                    {:key   id
                     :style {:border-bottom "#dbdbdb 1px solid"}}
                    [:div
                     [:label.heading.mb-0 title]
                     [:a.is-size-5.has-text-grey-dark chapter]]
                    [:button.button.is-small.is-align-items-flex-end
                     {:disabled (not allow-increment)
                      :on-click #(reading-lists.events/increment-reading-list-index id)}
                     ">"]])))])))
