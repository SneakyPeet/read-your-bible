(ns bible.web.registration.views
  (:require [bible.web.content :as cn]
            [bible.web.reading-lists.events :as reading-lists.events]
            [bible.web.preferences.events :as preferences.events]
            [bible.domain.translations :as domain.translations]))


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
                  (let [read-index (dec (js/parseInt (.-value (js/document.getElementById "day-select"))))
                        translation-id (.-value (js/document.getElementById "translation-select"))]
                    (preferences.events/set-translation translation-id)
                    (reading-lists.events/create-initial-reading-lists read-index)))}

    [:div.field
     [:div.control
      [:div.select
       [:select#day-select
        (->> (range 1 (inc max-allowed-days))
             (map (fn [i]
                    [:option {:key i :value i} i])))]]]]

    [:div.field
     [:div.control
      [:div.select
       [:select#translation-select
        (->> domain.translations/translations
             (map (fn [{:keys [id title]}]
                    [:option {:key id :value id} (str title)])))]]]]

    [:div.field
     [:div.control
      [:button.button.is-info {:type "submit"} (cn/registration-button-text)]]]]])
