(ns bible.web
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.dom :as gdom]
            ["react-dom/client" :refer [createRoot]]
            [bible.firebase.config]
            #_[feedme.components.fx]

            [bible.navigation.state :as navigation.state]
            [bible.navigation.events]
            [bible.navigation.views :as navigation.views]
            [bible.navigation.routes :as navigation.routes]

            [bible.authentication.events :as authentication.events]
            [bible.authentication.state :as authentication.state]


            #_[feedme.notebooks.state :as notebooks.state]))


;; Initial App State

(defn- initial-state []
  (merge
    (navigation.state/initial-state)
    (authentication.state/initial-state)
  ;;  (notebooks.state/initial-state)
    ))


(rf/reg-event-fx
  :app/initialize
  (fn [_ _]
    {:db (initial-state)
     :dispatch [::authentication.events/initialize]
     }))


(rf/reg-event-fx
  :app/reset
  (fn [_ _]
    {:db  (initial-state)
     :goto navigation.routes/login-page}))


;; This still feels bad, but feels better than having the events inside the auth
(rf/reg-event-fx
  :app/logged-in
  (fn [_ _]
    {}))


;; Entry point


(def ^:private root-element-id "root")

(defonce root (createRoot (gdom/getElement root-element-id)))

(defn render! []
  (.render root (r/as-element [bible.navigation.views/current-page])))


(defn init! []
  (rf/dispatch-sync [:app/initialize])
  (render!))


(defn ^:dev/after-load re-render!
  []
  (rf/clear-subscription-cache!)
  (render!))
