(ns bible.authentication.views
  (:require [bible.authentication.events :as authentication.events]))


(defn login-page []
  [:div
   [:h1 "Login"]
   (->> authentication.events/providers
        (map (fn [{:keys [title provider]}]
               [:button {:key title :on-click #(authentication.events/start-login provider)} title])))])


(defn logout-button []
  [:button {:on-click #(authentication.events/logout)} "Logout"])
