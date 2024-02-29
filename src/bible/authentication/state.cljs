(ns bible.authentication.state)


(defn initial-state []
  {::user nil})


(defn login [db user]
  (assoc db ::user user))


(defn logout [db]
  (dissoc db ::user))


(defn user-id [db]
  (get-in db [::user :user-id]))
