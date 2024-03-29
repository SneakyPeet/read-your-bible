(ns bible.web.reading-lists.state)

(defn initial-state []
  {::loaded-on-login? false
   ::reading-lists {}
   ::allow-increment? true})


(defn lists-have-not-been-initialized-on-signup? [db readling-list-db-changes]
  (and
    (false? (::loaded-on-login? db))
    (empty? readling-list-db-changes)))


(defn any-reading-lists? [db]
  (not (empty? (::reading-lists db))))


(defn set-loaded-on-login [db]
  (assoc db ::loaded-on-login? true))


(defn add-reading-list [db id reading-list]
  (assoc-in db [::reading-lists id] reading-list))


(defn modify-reading-list [db id reading-list]
  (assoc-in db [::reading-lists id] reading-list))


(defn remove-reading-list [db id]
  (update db ::reading-lists dissoc id))


(defn reading-lists [db]
  (vals (::reading-lists db)))


(defn reading-lists-by-id [db]
  (::reading-lists db))


(defn read-list [db id]
  (get-in db [::reading-lists id]))


(defn allow-increment [db]
  (assoc db ::allow-increment? true))


(defn prevent-increment [db]
  (assoc db ::allow-increment? false))


(defn allow-increment? [db]
  (::allow-increment? db))
