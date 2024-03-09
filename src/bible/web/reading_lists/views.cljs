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
            (map (fn [{:keys [id title chapter youversion-url read-today?]}]
                   [:div.is-flex.is-flex-wrap-nowrap.is-justify-content-space-between.is-align-items-center.mb-2
                    {:key   id
                     :style {:border-bottom "#dbdbdb 1px solid"}}

                    [:div
                     [:label.heading.mb-0 title]
                     [:a.is-size-5
                      {:href youversion-url
                       :target "_blank"
                       :class (if read-today? "has-text-primary" "has-text-grey-dark")}
                      chapter]]
                    [:div.buttons.is-align-items-flex-end
                     #_[:a.button.is-small.mr-5
                      {:href youversion-url
                       :target "_blank"}
                      "read"] ;;TODO
                     [:button.button.is-small
                      {:disabled (not allow-increment)
                       :on-click #(reading-lists.events/increment-reading-list-index id)}
                      ">"]]])))])))
