(ns bible.web.navigation.views
  (:require [re-frame.core :as rf]
            [bible.web.navigation.subs :as navigation.subs]
            [bible.web.navigation.routes :as navigation.routes]
            [bible.web.navigation.events :as navigation.events]
            [bible.web.authentication.views :as authentication.views]
            [bible.web.authentication.events :as authentication.events]
            [bible.web.reading-lists.views :as reading-lists.views]
            [bible.web.registration.views :as registration.views]
            [bible.web.authentication.subs :as authentication.subs]
            [bible.web.content :as cn]))


(defn wrapper [child]
  (let [logged-in? @(rf/subscribe authentication.subs/logged-in-sub)
        [auth-fn auth-button-title] (if logged-in?
                                      [authentication.events/logout
                                       (cn/navigation-logout)]
                                      [#(navigation.events/goto-route navigation.routes/login-page)
                                       (cn/navigation-login)])]
    [:div
     [:nav.navbar {:role       "navigation"
                   :aria-label "main navigation"}
      [:div.navbar-brand {:style {:justify-content "end"}}
       [:div.navbar-item
        [:div.buttons
         [:a.button.is-primary
          {:on-click auth-fn}
          auth-button-title]]]]]
     [:section.section.pt-0
      [:div.container

       child]]]))


(defn current-page []
  (let [current-page @(rf/subscribe [::navigation.subs/current-page])]
    [wrapper
     (cond
       (= current-page navigation.routes/dashboard-page)
       [:div [reading-lists.views/reading-list]]

       (= current-page navigation.routes/landing-page)
       [:h1 "landing"]

       (= current-page navigation.routes/loading-page)
       [:h1 "Loading"]

       (= current-page navigation.routes/login-page)
       [authentication.views/login-page]

       (= current-page navigation.routes/register-page)
       [registration.views/registration-page]

       :else
       [:h1 "No bible.web.navigation.views for " (str current-page)])]))
