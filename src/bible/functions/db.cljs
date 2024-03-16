(ns bible.functions.db
  (:require ["firebase-admin/firestore" :as firestore]))


(defn db [] (firestore/getFirestore))

(defn global-projections-collection []
  (-> (db)
      (.collection "global-projections")))