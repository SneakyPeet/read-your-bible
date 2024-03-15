(ns bible.web.reading-lists.events
  (:require [re-frame.core :as rf]
            [bible.web.authentication.state :as authentication.state]
            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.web.reading-lists.db :as reading-lists.db]
            [bible.web.projections.state :as projections.state]
            [bible.web.firebase.firestore :as firestore-fx]
            [bible.web.navigation.routes :as navigation.routes]
            ["firebase/firestore" :as firestore]))


(rf/reg-event-fx
  ::initialize-lists
  (fn [{:keys [db]} _]
    (let [user-id (authentication.state/user-id db)
          query (reading-lists.db/reading-lists-subscribe-query user-id)]
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
       :goto navigation.routes/register-page}
      ;; Simply set the lists on the state
      (let [db' (->> db
                     reading-lists.state/set-loaded-on-login
                     reading-lists.state/allow-increment)]
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
           db'
           changes)}))))


(rf/reg-event-fx
  ::create-initial-reading-lists
  (fn [{:keys [db]} [_ read-index]]
    (let [{:keys [read-list-mutations
                  event-mutations
                  projection-mutations]} (reading-lists.db/default-reading-list-mutations
                                           (authentication.state/user-id db) read-index)
          anonymous? (authentication.state/anonymous? db)]
      (cond-> 
          {:goto navigation.routes/dashboard-page
           :db   (reading-lists.state/prevent-increment db)
           ::firestore-fx/write-batch
           {:mutations (concat read-list-mutations projection-mutations)}}

        (not anonymous?)
        (assoc ::firestore-fx/write-batches {:mutations event-mutations})))))


(defn create-initial-reading-lists [read-index]
  (rf/dispatch [::create-initial-reading-lists read-index]))


(rf/reg-event-fx
  ::increment-read-list-index
  (fn [{:keys [db]} [_ read-list-id]]
    (let [read-list (reading-lists.state/read-list db read-list-id)
          projections (projections.state/projections db)
          user-id (authentication.state/user-id db)]
      {:db (-> db
               (reading-lists.state/prevent-increment))
       ::firestore-fx/write-batch
       {:mutations (reading-lists.db/increment-reading-list-mutations
                     user-id read-list projections)}})))


(defn increment-reading-list-index [read-list-id]
  (rf/dispatch [::increment-read-list-index read-list-id]))
