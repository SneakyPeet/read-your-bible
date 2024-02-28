(ns dev.firebase.core
  (:require [dev.firebase.config :as dev-config]
            ["firebase/auth" :as firebase-auth]
            ["firebase/firestore" :as firebase-firestore]
            ["firebase/storage" :as firebase-storage]
            ["firebase/functions" :as firebase-functions]))


(goog-define use-auth-emulator? false)
(goog-define use-firestore-emulator? false)
(goog-define use-storage-emulator? false)
(goog-define use-functions-emulator-from-web? false)


(defn- init-auth-emulator! []
  (let [[host port] (dev-config/auth-host-port)
        location    (str "http://" host ":" port)]

    (firebase-auth/connectAuthEmulator (firebase-auth/getAuth) location)
    (prn (str "Using Auth Emulator On: " location))))


(defn- init-firestore-emulator! []
  (let [[host port] (dev-config/firestore-host-port)
        location    (str "http://" host ":" port)]
    (firebase-firestore/connectFirestoreEmulator (firebase-firestore/getFirestore) host port)
    (prn (str "Using Firestore Emulator On: " location))))


(defn- init-storage-emulator! []
  (let [[host port] (dev-config/storage-host-port)
        location    (str "http://" host ":" port)]
    (firebase-storage/connectStorageEmulator (firebase-storage/getStorage) host port)
    (prn (str "Using Storage Emulator On: " location))))


(defn- init-functions-emulator! []
  (let [[host port] (dev-config/functions-host-port)
        location    (str "http://" host ":" port)]
    (firebase-functions/connectFunctionsEmulator (firebase-functions/getFunctions) host port)
    (prn (str "Using Functions Emulator On: " location))))


(defn init! []
  (when use-auth-emulator? (init-auth-emulator!))
  (when use-firestore-emulator? (init-firestore-emulator!))
  (when use-storage-emulator? (init-storage-emulator!))
  (when use-functions-emulator-from-web? (init-functions-emulator!)))
