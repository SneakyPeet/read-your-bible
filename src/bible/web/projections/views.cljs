(ns bible.web.projections.views
  (:require [re-frame.core :as rf]
            [bible.web.projections.subs :as projections.subs]
            ["react-apexcharts$default" :as chart]
            [reagent.core :as r]))


(def rchart
  (r/adapt-react-class chart))



;; Activity

(defn activity []
  (let [activity @(rf/subscribe [projections.subs/activity-sub])]
    [:div #_{:style {:overflow-y "scroll"}}
     [rchart {:type "heatmap"
              :height 150
              :series activity
              :options {:dataLabels {:enabled false}
                        :legend {:show false}
                        :plotOptions {:heatmap {:colorScale {:ranges [{:from  0
                                                                       :to    0
                                                                       :color "#cacaca"}
                                                                      {:from 1
                                                                       :to 50
                                                                       :color "#00A100"}
                                                                      ]}}}
                        :toolbar {:show true}
                        :width "100%"
                        :xaxis {:labels {:show false}}
                        :yaxis {:labels {:show false}}}}]]))


;; NEW

(defn times-read []
  (let [stats @(rf/subscribe [projections.subs/times-read-sub])]
    [:div
     [:div.heading.has-text-right "times read"]
     (->> stats
          (map (fn [{:keys [title percent read-total]}]
                 [:div.mb-1.has-background-white-ter
                  {:key title
                   :style {:height "2rem"
                           :width "100%"
                           :position "relative"}}

                  [:div.has-background-primary
                   {:style {:position "absolute"
                            :top 0
                            :height           "100%"
                            :width            (str percent "%")
                            :z-index 0}}]
                  [:div.p-2 {:style {:position "relative"}}
                   [:div.is-flex.is-flex-wrap-nowrap.is-justify-content-space-between
                    [:div.heading title]
                    [:div.heading (.toFixed read-total 2)]]]

                  ])))]))

;; Page


(defn all-charts []
  [:div.mb-5
   [:div [times-read]]
  #_#_#_ [read-counts]
   [activity]
   [lists-read]])
