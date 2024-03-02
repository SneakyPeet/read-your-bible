(ns bible.web.read-events.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]
            [bible.domain.read-events :as domain.read-events]))

(def read-events-table-name "read-events")

(def read-events-collection (firestore/collection firebase/firestore read-events-table-name))


(defn chapter-read-event-mutation [reading-list date]
  (let [docref (firestore/doc read-events-collection)
        data (domain.read-events/list-read-event reading-list date)]
    [:set docref data]))
