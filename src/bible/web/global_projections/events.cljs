(ns bible.web.global-projections.events
  (:require [re-frame.core :as rf]
            [bible.web.global-projections.state :as global-projections.state]
            [bible.web.global-projections.db :as global-projections.db]
            [bible.web.firebase.firestore :as firestore-fx]
            ["firebase/firestore" :as firestore]))



(rf/reg-event-fx
  ::initialize-daily-read-stat
  (fn [_ _]
    (let [query  global-projections.db/daily-read-stat-subscribe-query]
      {::firestore-fx/on-snapshot
       {:query     query
        :on-change [::apply-daily-read-stat-db-changes]}})))


(def initialize-daily-read-stat-event [::initialize-daily-read-stat])


(rf/reg-event-fx
  ::apply-daily-read-stat-db-changes
  (fn [{:keys [db]} [_ changes]]
    {:db
     (reduce
       (fn [db {:keys [type id data]}]
         (cond
           (= :add type)
           (global-projections.state/add-daily-read-stat db id data)
           (= :modify type)
           (global-projections.state/modify-daily-read-stat db id data)
           (= :remove type)
           (global-projections.state/remove-daily-read-stat db id)))
       db
       changes)}))
