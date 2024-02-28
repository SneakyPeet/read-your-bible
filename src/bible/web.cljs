(ns bible.web
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.dom :as gdom]
            ["react-dom/client" :refer [createRoot]]
            [bible.firebase]
            #_[feedme.components.fx]

            #_[feedme.navigation.state :as navigation.state]

            #_[feedme.authentication.events :as authentication.events]
            #_[feedme.authentication.state :as authentication.state]

            #_[feedme.navigation.events]
            #_[feedme.navigation.views :as navigation.views]

            #_[feedme.notebooks.state :as notebooks.state]))


;; Initial App State

(defn- initial-state []
  {}
  #_(merge
    (navigation.state/initial-state)
    (authentication.state/initial-state)
    (notebooks.state/initial-state)))


(rf/reg-event-fx
  :app/initialize
  (fn [_ _]
    {:db (initial-state)
     :dispatch [#_::authentication.events/initialize]}))


(rf/reg-event-fx
  :app/reset
  (fn [_ _]
    {:db       (initial-state)
     #_#_:goto :page/login-page}))


;; Entry point


(def ^:private root-element-id "root")

(defonce root (createRoot (gdom/getElement root-element-id)))

(defn temp [] [:div [:h1 "HI"]])

(defn render! []
  (.render root (r/as-element [temp #_feedme.navigation.views/current-page])))


(defn init! []
  (rf/dispatch-sync [:app/initialize])
  (render!))


(defn ^:dev/after-load re-render!
  []
  (rf/clear-subscription-cache!)
  (render!))
