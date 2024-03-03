(ns bible.web.navigation.events
  (:require [re-frame.core :as rf]
            [bible.web.navigation.state :as state]))


(rf/reg-event-db
  ::goto
  (fn [db [_ page]]
    (state/set-current-page db page)))


(rf/reg-fx
  :goto
  (fn [page]
    (rf/dispatch [::goto page])))


(defn goto-route [route]
  (rf/dispatch [::goto route]))
