(ns bible.web.global-projections.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]))



(def daily-read-stats-table-name "daily-read-stats")

(def daily-read-stats-collection
  (firestore/collection firebase/firestore daily-read-stats-table-name))


(def daily-read-stat-subscribe-query
  (firestore/query
    daily-read-stats-collection
    (firestore/orderBy "date" "desc")
    (firestore/limit 1)))
