(ns bible.web.preferences.views
  (:require [re-frame.core :as rf]
            [bible.web.preferences.events :as preferences.events]
            [bible.web.preferences.subs :as preferences.subs]
            [bible.domain.translations :as domain.translations]
            [bible.web.content :as cn]))

(def translation-options
  (->> domain.translations/translations
       (map (fn [{:keys [id title lang]}]
              [:option {:key id :value id} (str (cn/translation-language lang) title)]))))

(defn set-translation []
  (let [translation @(rf/subscribe [preferences.subs/translation-sub])]
    [:div.field
     [:div.control
      [:div.select
       [:select#translation-select
        {:default-value translation
         :on-change #(preferences.events/set-translation (.. % -target -value))}
        translation-options]]]]))
