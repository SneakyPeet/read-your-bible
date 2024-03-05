(ns bible.web.reading-lists.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]
            [bible.domain.reading-lists :as domain.reading-lists]
            [bible.web.read-events.db :as read-events.db]
            [bible.web.projections.db :as projections.db]))


(def reading-lists-table-name "reading-lists")

(def reading-lists-collection (firestore/collection firebase/firestore reading-lists-table-name))

(defn reading-lists-subscribe-query [user-id]
  (firestore/query
    reading-lists-collection
    (firestore/where "user-id" "==" user-id)))


(defn default-reading-list-mutations [user-id read-index]
  (let [date (firestore/Timestamp.now)
        mutations (->> domain.reading-lists/default-reading-lists
                       (map (fn [default-reading-list]
                              (let [{:keys [reading-list events]} (domain.reading-lists/init-default-reading-list default-reading-list user-id date read-index)
                                    docref                        (firestore/doc firebase/firestore reading-lists-table-name (:id reading-list))]
                                {:event-mutations    (map read-events.db/chapter-read-event-mutation events)
                                 :read-list-mutation [:set docref reading-list]}))))
        read-list-mutations (map :read-list-mutation mutations)
        events-mutations (->> (map :event-mutations mutations)
                              (reduce into))
        projection-mutations (projections.db/next-projection-mutations
                               user-id [] (map last events-mutations))]
    {:read-list-mutations  read-list-mutations
     :event-mutations      events-mutations
     :projection-mutations projection-mutations}))


(defn reading-list-doc-ref [read-list-id]
  (firestore/doc firebase/firestore reading-lists-table-name read-list-id))


(defn increment-reading-list-mutations [user-id reading-list projections]
  (let [date (firestore/Timestamp.now)
        docref (reading-list-doc-ref (:id reading-list))
        reading-list' (domain.reading-lists/increment-reading-list reading-list date)
        event-mutation (read-events.db/chapter-read-event-mutation-from-reading-list reading-list date)
        projection-mutations (projections.db/next-projection-mutations user-id projections [(last event-mutation)])]
    (concat
      [[:set docref reading-list']
       event-mutation]
      projection-mutations)))
