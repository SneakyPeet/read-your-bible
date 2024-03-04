(ns bible.web.reading-lists.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]
            [bible.domain.reading-lists :as domain.reading-lists]
            [bible.web.read-events.db :as read-events.db]))


(def reading-lists-table-name "reading-lists")

(def reading-lists-collection (firestore/collection firebase/firestore reading-lists-table-name))

(defn reading-lists-subscribe-query [user-id]
  (firestore/query
    reading-lists-collection
    (firestore/where "user-id" "==" user-id)))


(defn default-reading-list-mutations [user-id read-index]
  (let [date (firestore/Timestamp.now)]
    (->> domain.reading-lists/default-reading-lists
         (map #(let [data (domain.reading-lists/init-default-reading-list % user-id date read-index)
                     docref (firestore/doc firebase/firestore reading-lists-table-name (:id data))]
                 [:set docref data])))))


(defn reading-list-doc-ref [read-list-id]
  (firestore/doc firebase/firestore reading-lists-table-name read-list-id))


(defn increment-reading-list-mutations [reading-list]
  (let [date (firestore/Timestamp.now)
        docref (reading-list-doc-ref (:id reading-list))
        reading-list' (domain.reading-lists/increment-reading-list reading-list date)]
    [[:set docref reading-list']
     (read-events.db/chapter-read-event-mutation reading-list' date)]))
