(ns bible.functions.chapters-read-today
  (:require ["firebase-functions/v2/firestore" :as firestore-fn]
            ["firebase-admin/firestore" :as firestore]
            [bible.functions.db :as db]
            [bible.domain.read-events :as read-events]))


(defn midnight []
  (let [d (.toDate (firestore/Timestamp.now))]
    (.setHours d 0 0 0 0)
    (firestore/Timestamp.fromDate d)))


(defn- function [evt]
  (let [midnight (midnight)
        doc-ref (.doc (db/global-projections-collection) (str "read-today-" (.-seconds midnight)))
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
                           (if (.-exists doc)
                             (let [{:strs [users events] :as d} (js->clj (.data doc))
                                   users (-> users
                                             set
                                             (conj user-id))
                                   events (-> events
                                              set
                                              (conj evt-id))]
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
                                             :total-events 1}))
                             ))))))
          (.then (fn [_]))
          (.catch (fn [err]
                    (js/console.error err)
                    (js/Promise.reject err))))
      (js/Promise.resolve()))

    ))


(def export (firestore-fn/onDocumentCreated "read-events/{docId}" function))
