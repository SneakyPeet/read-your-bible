(ns dev.core
  (:require [bible.web.firebase.config]
            [bible.web.core :as web]
            [dev.firebase.core :as firebase-dev]))


(defn init! []
  (firebase-dev/init!)
  (web/init!))
