(ns bible.web.manual-entires.events
  (:require [re-frame.core :as rf]
            [bible.web.manual-entires.state :as manual-entries.state]
            [bible.web.authentication.state :as authentication.state]
            [bible.domain.read-events :as domain.read-events]
            [bible.web.read-events.db :as read-events.db]
            [bible.web.projections.db :as projections.db]
            [bible.web.projections.state :as projections.state]
            ["firebase/firestore" :as firestore]
            [bible.web.firebase.firestore :as firestore-fx]))



(rf/reg-event-db
  ::start-capturing
  (fn [db _]
    (manual-entries.state/start-capturing db)))


(defn start-capturing []
  (rf/dispatch [::start-capturing]))


(rf/reg-event-db
  ::select-book
  (fn [db [_ book-id]]
    (manual-entries.state/select-book db book-id)))


(defn select-book [book-id]
  (rf/dispatch [::select-book book-id]))


(rf/reg-event-db
  ::select-chapter
  (fn [db [_ chapter]]
    (manual-entries.state/select-chapter db chapter)))


(rf/reg-event-db
  ::deselect-chapter
  (fn [db [_ chapter]]
    (manual-entries.state/deselect-chapter db chapter)))


(defn select-chapter [chapter]
  (rf/dispatch [::select-chapter chapter]))


(defn deselect-chapter [chapter]
  (rf/dispatch [::deselect-chapter chapter]))


(rf/reg-event-fx
  ::capture
  (fn [{:keys [db]} _]
    (let [user-id (authentication.state/user-id db)
          {:keys [book-id selected-chapters]} (manual-entries.state/selection db)
          read-date (firestore/Timestamp.now)
          events (->> selected-chapters
                      sort
                      (map #(domain.read-events/manual-entry-read-event
                              user-id book-id % read-date)))
          event-mutations (map read-events.db/chapter-read-event-mutation events)
          projections (projections.state/projections db)
          projection-mutations (projections.db/next-projection-mutations user-id projections events)]
      {:db (manual-entries.state/complete-capture db)
       ::firestore-fx/write-batch
       {:mutations (concat projection-mutations
                           event-mutations)}})))


(defn capture []
  (rf/dispatch [::capture]))
