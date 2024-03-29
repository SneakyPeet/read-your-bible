(ns bible.web.projections.state
  (:require [bible.domain.projections :as domain.projections]))


(defn initial-state []
  {::projections {}})


(defn add-projection [db id projection]
  (assoc-in db [::projections id] projection))


(defn modify-projection [db id projection]
  (assoc-in db [::projections id] projection))


(defn remove-projection [db id]
  (update db ::projections dissoc id))


(defn projection-state-by-type [db]
  (domain.projections/projection-state-by-type (vals (::projections db))))


(defn projections [db]
  (vals (::projections db)))
