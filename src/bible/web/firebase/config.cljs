(ns bible.web.firebase.config
  (:require [shadow.resource :as rc]
            ["firebase/app" :as firebase-app]
            ["firebase/firestore" :as firestore]
            ["firebase/analytics" :as analytics]))

(def firebase-config  (js/JSON.parse (rc/inline "config.json")))

(def app (firebase-app/initializeApp firebase-config))

(def firestore (firestore/getFirestore app))


(goog-define use-analytics? false)

(def analytics
  (when use-analytics?
    (analytics/getAnalytics app)))
