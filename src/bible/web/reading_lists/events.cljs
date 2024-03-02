(ns bible.web.reading-lists.events
  (:require [re-frame.core :as rf]
            [bible.web.authentication.state :as authentication.state]
            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.web.reading-lists.db :as readling-lists.db]
            [bible.web.firebase.firestore :as firestore-fx]))


(rf/reg-event-fx
  ::initialize-lists
  (fn [{:keys [db]} _]
    (let [user-id (authentication.state/user-id db)
          query (readling-lists.db/readling-lists-subscribe-query user-id)]
      {::firestore-fx/on-snapshot
       {:query     query
        :on-change [::apply-reading-lists-db-changes]}})))


(def initialize-lists-event [::initialize-lists])


(rf/reg-event-fx
  ::apply-reading-lists-db-changes
  (fn [{:keys [db]} [_ changes]]
    (if (reading-lists.state/lists-have-not-been-initialized-on-signup? db changes)
      ;; We create the initial DB entries.
      ;; This will force the subscribe query to update and call this function again providing the new lists
      {:db (reading-lists.state/set-loaded-on-login db)
       ::firestore-fx/write-batch
       {:mutations (readling-lists.db/default-reading-list-mutations
                     (authentication.state/user-id db))}}
      ;; Simply set the lists on the state
      {:db
       (reduce
         (fn [db {:keys [type id data]}]
           (cond
             (= :add type)
             (reading-lists.state/add-reading-list db id data)
             (= :modify type)
             (reading-lists.state/modify-reading-list db id data)
             (= :remove type)
             (reading-lists.state/remove-reading-list db id)))
         (reading-lists.state/set-loaded-on-login db)
         changes)})))