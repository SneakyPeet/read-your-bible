(ns bible.web.reading-lists.db
  (:require [bible.web.firebase.config :as firebase]
            [bible.core :as core]
            ["firebase/firestore" :as firestore]))


(def reading-lists-table-name "reading-lists")

(def readling-lists-collection (firestore/collection firebase/firestore reading-lists-table-name))

(defn readling-lists-subscribe-query [user-id]
  (firestore/query
    readling-lists-collection
    (firestore/where "user-id" "==" user-id)))


(defn default-reading-list-mutations [user-id]
  (->> core/default-reading-lists
       (map #(let [docref (firestore/doc readling-lists-collection)]
               [:set
                docref
                (assoc %
                       ;;TODO this should probably move to a models function in the domain
                       :id (.-id docref)
                       :user-id user-id
                       :last-read-date (firestore/Timestamp.now))]))))
