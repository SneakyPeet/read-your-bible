(ns bible.domain.read-events)


(def chapter-read-evt-type-list "list")
(def chapter-read-evt-type-manual-entry "manual-entry")


(defn list-read-event [reading-list date]
  (let [{:keys [current-book current-chapter read-index id user-id]} reading-list]
    {:user-id     user-id
     :book-id     current-book
     :chapter     current-chapter
     :read-date   date
     :create-date date
     :type        chapter-read-evt-type-list
     :type-data   {:list-id    id
                   :read-index read-index}}))


(defn manual-entry-read-event [user-id book-id chapter-id read-date]
  {:user-id     user-id
   :book-id     book-id
   :chapter     chapter-id
   :read-date   read-date
   :create-date "TODO"
   :type        chapter-read-evt-type-manual-entry
   :type-data   {}})
