(ns bible.web.registration.views
  (:require [bible.web.content :as cn]
            [bible.web.reading-lists.events :as reading-lists.events]))


(def max-allowed-days 100)


(defn registration-page []
  [:div.is-flex.is-flex-direction-column.is-align-items-center

   [:h1.title (cn/registration-heading)]

   [:div.field
    [:div.control
     [:p (cn/registration-explainer-text)]]]

   [:form
    {:on-submit (fn [evt]
                  (.preventDefault evt)
                  (let [read-index (js/parseInt (.-value (js/document.getElementById "day-select")))]
                    (reading-lists.events/create-initial-reading-lists read-index)))}

    [:div.field
     [:div.control
      [:div.select
       [:select#day-select
        (->> (range 0 (inc max-allowed-days))
             (map (fn [i]
                    [:option {:key i :value i} i])))]]]]

    [:div.field
     [:div.control
      [:button.button.is-info {:type "submit"} (cn/registration-button-text)]]]]])
