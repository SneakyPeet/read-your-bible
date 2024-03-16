(ns bible.functions.new-users
  (:require ["firebase-functions" :as functions]
            ["firebase-admin/app" :as firebase-app]
            ["firebase/firestore" :as firestore]
            [bible.functions.db :as db]))

(defn doc-ref []
  (.doc (db/global-projections-collection) "new-users"))

(defn- function [user]
  (prn "****")
  (let [user-id (.-uid user)
        midnight-utc (.toDate (firestore/Timestamp.now))
        _ (.setHours midnight-utc 0 0 0 0)
        date (firestore/Timestamp.fromDate midnight-utc)
        sec  (.-seconds date)
        doc-ref (.doc (db/global-projections-collection) "new-user")]

    (-> (.runTransaction
          (db/db)
          (fn [transaction]
            (-> transaction
                (.get (doc-ref))
                (.then (fn [doc]
                         (let [next-doc (if (.exists doc)
                                          (prn "newDoc")
                                          (prn "exists"))]))))))
        (.then (fn [_]))
        (.catch (fn [err1])))

    ))


(def export
  (.onCreate (functions/auth.user) function))
