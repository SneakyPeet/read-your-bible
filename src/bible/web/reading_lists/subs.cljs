(ns bible.web.reading-lists.subs
  (:require [re-frame.core :as rf]
            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.web.preferences.state :as preferences.state]
            ["firebase/firestore" :as firestore]
            [bible.domain.books :as domain.books]))


(rf/reg-sub
  ::reading-lists-view
  (fn [db]
    (->> (reading-lists.state/reading-lists db)
         (sort-by :position)
         (map (fn [{:keys                                       [id current-book current-chapter title
                           ^firestore/Timestamp last-read-date] :as reading-list}]
                (js/console.log last-read-date)
                {:id             id
                 :title          title
                 :chapter        (str (domain.books/book-title current-book) " " current-chapter)
                 :youversion-url (preferences.state/youversion-url db current-book current-chapter)
                 :data           reading-list
                 :read-today?    (reading-lists.state/read-today? db id)})))))


(def reading-lists-view ::reading-lists-view)


(rf/reg-sub
  ::allow-increment?
  (fn [db]
    (reading-lists.state/allow-increment? db)))

(def allow-increment-sub ::allow-increment?)


(rf/reg-sub
  ::any?
  (fn [db]
    (reading-lists.state/any-reading-lists? db)))


(def any-sub ::any?)
