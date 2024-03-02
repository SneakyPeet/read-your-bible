(ns bible.web.navigation.views
  (:require [re-frame.core :as rf]
            [bible.web.navigation.subs :as navigation.subs]
            [bible.web.navigation.routes :as navigation.routes]
            [bible.web.authentication.views :as authentication.views]
            [bible.web.authentication.events :as authentication.events]
         #_   [bible.web.notebooks.views :as notebooks.views]
            [bible.web.components.core :as c]
            [bible.web.content :as cn]))


(defn logged-in-container
  ([title child & menu-items]
   [c/menu-page
    title
    (concat
      menu-items
      [[c/menu-item (cn/value :navigation/logout) authentication.events/logout-evt]])
    child]))


(defn current-page []
  (let [current-page @(rf/subscribe [::navigation.subs/current-page])]
    [c/container
     (cond
       (= current-page navigation.routes/loading-page)
       [:h1 "Loading"]

       (= current-page navigation.routes/login-page)
       [authentication.views/login-page]

       (= current-page navigation.routes/dashboard-page)
       [logged-in-container [:h1 "LOGGED IN"]]

       #_#_   :page/notebook
       [logged-in-container
        [notebooks.views/notebook-name]
        [notebooks.views/notebook-page]
        [notebooks.views/close-notebook-menu]]

       :else
       [:h1 "No bible.web.navigation.views for " (str current-page)])]))
