(ns bible.web.reading-lists.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]
            [bible.domain.reading-lists :as domain.reading-lists]))


(def reading-lists-table-name "reading-lists")

(def readling-lists-collection (firestore/collection firebase/firestore reading-lists-table-name))

(defn readling-lists-subscribe-query [user-id]
  (firestore/query
    readling-lists-collection
    (firestore/where "user-id" "==" user-id)))


(defn default-reading-list-mutations [user-id]
  (let [date (firestore/Timestamp.now)]
    (->> domain.reading-lists/default-reading-lists
         (map #(let [docref (firestore/doc readling-lists-collection)]
                 [:set
                  docref
                  (domain.reading-lists/init-default-reading-list % (.-id docref) user-id date)])))))
