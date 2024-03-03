(ns bible.web.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.dom :as gdom]
            ["react-dom/client" :refer [createRoot]]
            [bible.web.firebase.config]
            #_[feedme.components.fx]

            [bible.web.navigation.state :as navigation.state]
            [bible.web.navigation.events]
            [bible.web.navigation.views :as navigation.views]
            [bible.web.navigation.routes :as navigation.routes]

            [bible.web.authentication.events :as authentication.events]
            [bible.web.authentication.state :as authentication.state]

            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.web.reading-lists.events :as reading-lists.events]

            [bible.web.firebase.firestore :as firestore-fx]))


;; Initial App State

(defn- initial-state []
  (merge
    (navigation.state/initial-state)
    (authentication.state/initial-state [::logged-in] [::logged-out])
    (reading-lists.state/initial-state)))


(rf/reg-event-fx
  ::initialize
  (fn [_ _]
    {:db (initial-state)
     :dispatch authentication.events/initialize-evt}))


(rf/reg-event-fx
  ::reset
  (fn [_ _]
    {:db  (initial-state)
     :goto navigation.routes/landing-page}))


(rf/reg-event-fx
  ::logged-in
  (fn [_ _]
    {:goto  navigation.routes/dashboard-page
     :dispatch reading-lists.events/initialize-lists-event}))


(rf/reg-event-fx
  ::logged-out
  (fn [_ _]
    {:dispatch                          [::reset]
     :goto                              navigation.routes/login-page
     ::firestore-fx/unsub-all-snapshots nil}))


;; Entry point


(def ^:private root-element-id "root")

(defonce root (createRoot (gdom/getElement root-element-id)))

(defn render! []
  (.render root (r/as-element [bible.web.navigation.views/current-page])))


(defn init! []
  (rf/dispatch-sync [::initialize])
  (render!))


(defn ^:dev/after-load re-render!
  []
  (rf/clear-subscription-cache!)
  (render!))
