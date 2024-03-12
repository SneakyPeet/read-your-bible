(ns bible.web.projections.views
  (:require [re-frame.core :as rf]
            [bible.web.projections.subs :as projections.subs]
         #_   ["react-apexcharts$default" :as chart]
            [reagent.core :as r]))


#_(def rchart
  (r/adapt-react-class chart))



;; Activity

#_(defn activity []
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
     [:div.heading.has-text-right "progress"]
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
                    [:div.heading
                     {:class (when (>= read-total 1) "has-text-primary")}
                     (.toFixed read-total 2)]]]])))]))


(defn books-read []
  (let [stats @(rf/subscribe [projections.subs/books-read-sub])]
    [:div
     [:div.heading.has-text-right "books"]
     (->> stats
          (map (fn [{:keys [title total read read-before?]}]
                 [:div
                  {:key title}
                  [:div.heading {:style {:margin-bottom "0"}} title]
                  [:div.is-flex.is-flex-wrap-wrap.is-justify-content-start.is-align-items-center
                   (let [base-background (if read-before?
                                           "hsl(171, 100%, 80%)"
                                           "hsl(0, 0%, 96%)")]
                     (->> (range 1 (inc total))
                          (map (fn [i]
                                 [:div {:key   (str title i)
                                        :style {:margin        "1px"
                                                :border-radius "2px"
                                                :width         "0.4rem"
                                                :height        "0.4rem"
                                                :background-color (if (<= i read)
                                                                    "hsl(171, 100%, 41%)"
                                                                    base-background)}}]))))]])))]))


(defn streak []
  (let [{:keys [total previous-max currently-in-streak?]} (get @(projections.subs/streaks-sub) "daily")]
    [:div.is-flex.is-flex-wrap-nowrap.is-justify-content-space-between.heading.has-text-primary
     [:div "Streak: " (if currently-in-streak? total 0)]
     [:div "Longest: " previous-max]]))
;; Page


(defn all-charts []
  [:div.mb-5
   [:div.block [times-read]]
   [:div [books-read]]])
