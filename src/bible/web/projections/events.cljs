(ns bible.web.projections.events
  (:require [re-frame.core :as rf]
            [bible.web.authentication.state :as authentication.state]
            [bible.web.projections.state :as projections.state]
            [bible.web.projections.db :as projections.db]
            [bible.web.firebase.firestore :as firestore-fx]
            ["firebase/firestore" :as firestore]))


(rf/reg-event-fx
  ::initialize-projections
  (fn [{:keys [db]} _]
    (let [user-id (authentication.state/user-id db)
          query   (projections.db/projections-subscribe-query user-id)]
      {::firestore-fx/on-snapshot
       {:query     query
        :on-change [::apply-projection-db-changes]}})))


(def initialize-projections-event [::initialize-projections])


(rf/reg-event-fx
  ::apply-projection-db-changes
  (fn [{:keys [db]} [_ changes]]
    {:db
     (reduce
       (fn [db {:keys [type id data]}]
         (cond
           (= :add type)
           (projections.state/add-projection db id data)
           (= :modify type)
           (projections.state/modify-projection db id data)
           (= :remove type)
           (projections.state/remove-projection db id)))
       db
       changes)}))
