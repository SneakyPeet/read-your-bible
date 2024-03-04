(ns bible.web.authentication.views
  (:require [bible.web.authentication.events :as authentication.events]
            [bible.web.content :as cn]))


(defn login-page []
  [:div.is-flex.is-flex-direction-column.is-align-items-center
   [:p.mb-5 (cn/login-explainer-text)]
   (->> authentication.events/providers
        (map (fn [{:keys [title provider]}]
               [:button.button.is-large {:key title :on-click #(authentication.events/start-login provider)} title])))])
