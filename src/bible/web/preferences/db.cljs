(ns bible.web.preferences.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]))

(def preferences-table-name "preferences")

(def preferences-collection (firestore/collection firebase/firestore preferences-table-name))


(defn preferences-subscribe-query [user-id]
  (firestore/query
    preferences-collection
    (firestore/where "user-id" "==" user-id)))


(defn preferences-mutations [user-id preferences]
  [[:set
    (firestore/doc firebase/firestore preferences-table-name user-id)
    (assoc preferences :user-id user-id)]])
