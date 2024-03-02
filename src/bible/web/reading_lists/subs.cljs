(ns bible.web.reading-lists.subs
  (:require [re-frame.core :as rf]
            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.domain.books :as domain.books]))


(rf/reg-sub
  ::reading-lists-view
  (fn [db]
    (->> (reading-lists.state/reading-lists db)
         (sort-by :position)
         (map (fn [{:keys [id current-book current-chapter title] :as reading-list}]
                {:id id
                 :title title
                 :chapter (str (domain.books/book-title current-book) " " current-chapter)
                 :data reading-list})))))


(def reading-lists-view ::reading-lists-view)
