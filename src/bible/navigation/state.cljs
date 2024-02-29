(ns bible.navigation.state
  (:require [bible.navigation.routes :as navigation.routes]))


(defn initial-state []
  {::current-page navigation.routes/loading-page})


(defn current-page [state]
  (::current-page state))


(defn set-current-page [state page]
  (assoc state ::current-page page))
