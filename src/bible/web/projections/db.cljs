(ns bible.web.projections.db
  (:require [bible.web.firebase.config :as firebase]
            ["firebase/firestore" :as firestore]
            [bible.domain.projections :as domain.projections]))


(def projections-table-name "projections")

(def projections-collection (firestore/collection firebase/firestore projections-table-name))


(defn projections-subscribe-query [user-id]
  (firestore/query
    projections-collection
    (firestore/where "user-id" "==" user-id)))


(defn next-projection-mutations [user-id projections events]
  (->> (domain.projections/next-projections user-id projections events)
       (map (fn [{:keys [id] :as projection}]
              [:set
               (firestore/doc firebase/firestore projections-table-name id)
               projection]))))
