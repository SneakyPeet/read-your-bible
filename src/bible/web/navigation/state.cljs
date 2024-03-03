(ns bible.web.navigation.state
  (:require [bible.web.navigation.routes :as navigation.routes]))


(defn initial-state []
  {::current-page navigation.routes/landing-page})


(defn current-page [state]
  (::current-page state))


(defn set-current-page [state page]
  (assoc state ::current-page page))
