(ns bible.navigation.views
  (:require [re-frame.core :as rf]
            [bible.navigation.subs :as navigation.subs]
            [bible.navigation.routes :as navigation.routes]
            [bible.authentication.views :as authentication.views]
            [bible.authentication.events :as authentication.events]
         #_   [bible.notebooks.views :as notebooks.views]
            [bible.components.core :as c]
            [bible.content :as cn]))


(defn logged-in-container
  ([title child & menu-items]
   [c/menu-page
    title
    (concat
      menu-items
      [[c/menu-item (cn/value :navigation/logout) [authentication.events/logout-evt]]])
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
       [:h1 "LOGGED IN"]

       #_#_   :page/notebook
       [logged-in-container
        [notebooks.views/notebook-name]
        [notebooks.views/notebook-page]
        [notebooks.views/close-notebook-menu]]

       :else
       [:h1 "No bible.navigation.views for " (str current-page)])]))
