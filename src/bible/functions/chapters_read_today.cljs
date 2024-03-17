(ns bible.functions.chapters-read-today
  (:require ["firebase-functions/v2/firestore" :as firestore-fn]
            ["firebase-admin/firestore" :as firestore]
            [bible.functions.db :as db]
            [bible.domain.read-events :as read-events]))


(defn- collection []
  (-> (db/db)
      (.collection "daily-read-stats")))


(defn- get-doc-ref [^firestore/Timestamp date]
  (let [id (.toISOString (.toDate date))]
    (.doc (collection) id)))


(defn get-midnight []
  (let [d (.toDate (firestore/Timestamp.now))]
    (.setHours d 0 0 0 0)
    (firestore/Timestamp.fromDate d)))


(defn- function [evt]
  (let [midnight (get-midnight)
        doc-ref (get-doc-ref midnight)
        user-id (.get evt.data "user-id")
        evt-type (.get evt.data "type")
        evt-id (.get evt.data "id")]
    (if (contains? #{read-events/chapter-read-evt-type-list
                     read-events/chapter-read-evt-type-manual-entry} evt-type)
      (-> (.runTransaction
            (db/db)
            (fn [transaction]
              (-> transaction
                  (.get doc-ref)
                  (.then (fn [doc]
                           (if-let [data (js->clj (.data doc))]
                             (let [users (-> (get data "users")
                                             set
                                             (conj user-id)
                                             vec)
                                   events (-> (get data "events")
                                              set
                                              (conj evt-id)
                                              vec)]
                               (.update transaction
                                        doc-ref
                                        (clj->js {:users users
                                                  :events events
                                                  :total-users (count users)
                                                  :total-events (count events)})))
                             (.set transaction
                                   doc-ref
                                   (clj->js {:date         midnight
                                             :users        [user-id]
                                             :events       [evt-id]
                                             :total-users  1
                                             :total-events 1}))))))))
          (.then (fn [_]))
          (.catch (fn [err]
                    (js/console.error err)
                    (js/Promise.reject err))))
      (js/Promise.resolve()))))


(def export (firestore-fn/onDocumentCreated "read-events/{docId}" function))
