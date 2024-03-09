(ns bible.web.projections.views
  (:require [re-frame.core :as rf]
            [bible.web.projections.subs :as projections.subs]
            ["react-apexcharts$default" :as chart]
            [reagent.core :as r]))


(def rchart
  (r/adapt-react-class chart))


;; Donut Progress Charts

(defn donut-progress-opts [title value]
  {:key title
   :options {:labels [(.toUpperCase title)]
             :plotOptions {:radialBar {:hollow {:margin 0 :size "55%"}
                                       :track {:show true
                                               :strokeWidth "80%"}
                                       :dataLabels {:showOn "always"
                                                    :name   {:offsetY  -5
                                                             :show     true
                                                             :color    "#888"
                                                             :fontSize "13px"}
                                                    :value  {:color    "#888"
                                                             :fontSize "13px"
                                                             :show     true
                                                             :offsetY -5}}}}
             :stroke {:lineCap "round"}}
   :series [value]
   :type "radialBar"
   :height 140})


(defn read-counts []
  (let [counts @(rf/subscribe [projections.subs/times-read-sub])]
    [:div.is-flex.is-flex-wrap-nowrap.is-justify-content-space-between.is-align-items-center
     (->> counts
          (map (fn [[title total]]
                 [:div {:key title
                        :style {:width "33%"}}
                  [rchart (donut-progress-opts title total)]])))
     ]))

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

;; Lists read

(defn lists-read []
  (let [{:keys [labels series]} @(rf/subscribe [projections.subs/lists-read-sub])]
    [:div
     [rchart {:options {:labels labels
                        :plotOptions {:radialBar {:hollow     {:margin 0 :size "15%"}
                                                  :track      {:show        true
                                                               :strokeWidth "80%"}
                                                  :dataLabels {:showOn "always"
                                                               :name   {:offsetY  -5
                                                                        :show     true
                                                                        :color    "#888"
                                                                        :fontSize "13px"}
                                                               :value  {:color    "#888"
                                                                        :fontSize "13px"
                                                                        :show     true
                                                                        :offsetY  -5}}}}
                        :stroke {:lineCap "round"}}
              :series  series
              :type    "radialBar"
           #_#_   :height  140}]]))

;; Page

(defn all-charts []
  [:div
   [read-counts]
   [activity]
   [lists-read]])
