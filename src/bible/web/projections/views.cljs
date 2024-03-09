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
   :height 150})

(defn read-counts []
  (let [counts @(rf/subscribe [projections.subs/times-read-sub])]
    [:div.is-flex.is-flex-wrap-nowrap.is-justify-content-space-between.is-align-items-center
     (->> counts
          (map (fn [[title total]]
                 [:div {:key title
                        :style {:width "33%"}}
                  [rchart (donut-progress-opts title total)]])))
     ]))
