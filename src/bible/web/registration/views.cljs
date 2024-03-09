(ns bible.web.registration.views
  (:require [bible.web.content :as cn]
            [bible.web.reading-lists.events :as reading-lists.events]
            [bible.web.preferences.events :as preferences.events]
            [bible.domain.translations :as domain.translations]
            [bible.web.preferences.views :as preferences.views]))


(def max-allowed-days 100)


(defn registration-page []
  [:div.mt-5;.is-flex.is-flex-direction-column.is-align-items-center



   [:div.content.has-text-centered
    [:h1.title (cn/registration-heading)]
    [:p (cn/registration-explainer-text)]]

   [:form
    {:on-submit (fn [evt]
                  (.preventDefault evt)
                  (let [read-index (dec (js/parseInt (.-value (js/document.getElementById "day-select"))))
                        translation-id (.-value (js/document.getElementById "translation-select"))]
                    (preferences.events/set-translation translation-id)
                    (reading-lists.events/create-initial-reading-lists read-index)))}

    [:div.field
     [:div.label "Starting Day"]
     [:div.control
      [:div.select
       [:select#day-select
        (->> (range 1 (inc max-allowed-days))
             (map (fn [i]
                    [:option {:key i :value i} i])))]]]
     [:p.help "Leave as 1 if you don't know where to start"]]

    [:div.field
     [:div.label "Preferred bible translation"]
     [:div.control
      [:div.select
       [:select#translation-select
        preferences.views/translation-options]]]
     [:p.help "Will link to the translation in the youversion app when you click the read button. You can always change this later."]]

    [:div.field
     [:div.control
      [:button.button.is-info {:type "submit"} (cn/registration-button-text)]]]]])
