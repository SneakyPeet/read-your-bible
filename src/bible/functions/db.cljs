(ns bible.functions.db
  (:require ["firebase-admin/firestore" :as firestore]))


(defn db [] (firestore/getFirestore))

