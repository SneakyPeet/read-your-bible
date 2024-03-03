(ns bible.web.authentication.state)


(defn initial-state [logged-in-event logged-out-event]
  {::user nil
   ::logged-in-event logged-in-event
   ::logged-out-event logged-out-event})


(defn logged-in-event [db]
  (::logged-in-event db))


(defn logged-out-event [db]
  (::logged-out-event db))


(defn login [db user]
  (assoc db ::user user))


(defn logout [db]
  (dissoc db ::user))


(defn user-id [db]
  (get-in db [::user :user-id]))


(defn logged-in? [db]
  (some? (user-id db)))
