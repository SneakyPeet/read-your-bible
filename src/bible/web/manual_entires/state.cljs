(ns bible.web.manual-entires.state)

(defn initial-state []
  {::capturing? false
   ::book-id 1
   ::selected-chapters #{}})


(defn complete-capture [db]
  (merge db (initial-state)))


(defn start-capturing [db]
  (assoc db ::capturing? true))


(defn capturing? [db] (::capturing? db))


(defn select-book [db book-id]
  (assoc db
         ::book-id book-id
         ::selected-chapters #{}))


(defn select-chapter [db chapter]
  (update db ::selected-chapters conj chapter))


(defn deselect-chapter [db chapter]
  (update db ::selected-chapters disj chapter))


(defn selection [db]
  {:book-id (::book-id db)
   :selected-chapters (::selected-chapters db)
   :capturing? (::capturing? db)})
