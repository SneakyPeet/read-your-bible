(ns bible.web.authentication.views
  (:require [bible.web.authentication.events :as authentication.events]
            ["firebase/auth" :as firebase-auth]))


(defn login-page []
  [:div
   [:h1.is-size-5 "Choose a login option"]
   [:div.is-flex.is-flex-direction-column
    {:style {:max-width "300px"}}
    [:button.button.mb-3.is-danger {:on-click #(authentication.events/start-login (firebase-auth/GoogleAuthProvider.))} "Google"]
    [:div.field.has-addons
     [:div.control
      [:a.button.is-primary
       {:on-click #(authentication.events/start-email-authentication
                     (.-value (js/document.getElementById "login-email")))}
       "Send me a link"]]
     [:div.control
      [:input.input {:id "login-email" :type "email" :placeholder "Email"}]]]
    [:button.button.is-warning
     {:on-click #(authentication.events/start-anonymous-authentication)} "I just want to try it out"]]])
