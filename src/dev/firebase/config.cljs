(ns dev.firebase.config
  (:require [shadow.resource :as rc]))

(def config (js->clj (js/JSON.parse (rc/inline "firebase.json"))))


(defn- emulator-host-port [k]
  [(get-in config ["emulators" k "host"])
   (get-in config ["emulators" k "port"])])


(defn auth-host-port []
  (emulator-host-port "auth"))


(defn firestore-host-port []
  (emulator-host-port "firestore"))


(defn storage-host-port []
  (emulator-host-port "storage"))


(defn functions-host-port []
  (emulator-host-port "functions"))
