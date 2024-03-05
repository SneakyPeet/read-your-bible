(ns bible.web.preferences.events
  (:require [re-frame.core :as rf]
            [bible.web.authentication.state :as authentication.state]
            [bible.web.preferences.state :as preferences.state]
            [bible.web.preferences.db :as preferences.db]
            [bible.web.firebase.firestore :as firestore-fx]
            ["firebase/firestore" :as firestore]))


(rf/reg-event-fx
  ::initialize-preferences
  (fn [{:keys [db]} _]
    (let [user-id (authentication.state/user-id db)
          query   (preferences.db/preferences-subscribe-query user-id)]
      {::firestore-fx/on-snapshot
       {:query     query
        :on-change [::apply-preferences-db-changes]}})))


(def initialize-preferences-event [::initialize-preferences])


(rf/reg-event-fx
  ::apply-preferences-db-changes
  (fn [{:keys [db]} [_ changes]]
    {:db
     (reduce
       (fn [db {:keys [type _ data]}]
         (cond
           (= :add type)
           (preferences.state/set-preferences db data)
           (= :modify type)
           (preferences.state/set-preferences db data)
           (= :remove type)
           (preferences.state/remove-preferences db)))
       db
       changes)}))


(rf/reg-event-fx
  ::set-preferences
  (fn [{:keys [db]} [_ id value]]
    (let [user-id (authentication.state/user-id db)
          preferences' (-> (preferences.state/preferences db)
                           (assoc id value))]
      {::firestore-fx/write-batch
       {:mutations (preferences.db/preferences-mutations user-id preferences')}})))


(defn set-translation [translation]
  (rf/dispatch [::set-preferences :translation translation]))
