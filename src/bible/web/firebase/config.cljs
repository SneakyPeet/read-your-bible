(ns bible.web.firebase.config
  (:require [shadow.resource :as rc]
            ["firebase/app" :as firebase-app]
            ["firebase/firestore" :as firestore]))

(def firebase-config  (js/JSON.parse (rc/inline "config.json")))

(def app (firebase-app/initializeApp firebase-config))

(def db (firestore/getFirestore app))
