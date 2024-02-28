(ns bible.firebase
  (:require [shadow.resource :as rc]
            ["firebase/app" :as firebase-app]))

(def firebase-config  (js/JSON.parse (rc/inline "config.json")))

(def app (firebase-app/initializeApp firebase-config))
