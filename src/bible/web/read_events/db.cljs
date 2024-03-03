(ns bible.web.read-events.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]
            [bible.domain.read-events :as domain.read-events]))

(def read-events-table-name "read-events")

(def read-events-collection (firestore/collection firebase/firestore read-events-table-name))


(defn chapter-read-event-mutation [reading-list date]
  (let [data (domain.read-events/list-read-event reading-list date)
        docref (firestore/doc firebase/firestore read-events-table-name (:id data))]
    [:set docref data]))
