(ns dev.core
  (:require [bible.firebase]
            [bible.web :as web]
            [dev.firebase.core :as firebase-dev]))


(defn init! []
  (firebase-dev/init!)
  (web/init!))
