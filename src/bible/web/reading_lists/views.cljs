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
            (map (fn [{:keys [id title chapter youversion-url saturation]}]
                   [:div.is-flex.is-flex-wrap-nowrap.is-justify-content-space-between.is-align-items-center.mb-2
                    {:key   id
                     :style {:border-bottom "#dbdbdb 1px solid"}}

                    [:div
                     [:label.heading.mb-0 {:position "relative"}
                      title
                      #_[:span.has-background-success-light.has-text-primary
                       {:style {:font-size "0.5rem"
                                :margin "1px 0.3rem"
                                :padding "0.06rem"
                                :border-radius "5px"
                                :position "absolute"
                                }} 10]]
                     [:a.is-size-5
                      {:style {:color (str "hsl(171 " saturation "% 41% / 1)")}
                       :href youversion-url
                       :target "_blank"}
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
