(ns bible.web.reading-lists.subs
  (:require [re-frame.core :as rf]
            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.web.preferences.state :as preferences.state]
            ["firebase/firestore" :as firestore]
            [bible.domain.books :as domain.books]))


(defn- saturation-lookup-fn [lists]
  (let [read-seconds (->> lists
                          (map (fn [{:keys [^firestore/Timestamp last-read-date]}]
                                 (.-seconds last-read-date)))
                          (sort))
        min (first read-seconds)
        max (last read-seconds)
        diff (- max min)]
    (fn [list]
      (if (zero? diff)
        0
        (let [sec (.-seconds (:last-read-date list))
              val (- sec min)]
          (js/Math.round (* 100 (/ val diff))))))))


(rf/reg-sub
  ::reading-lists-view
  (fn [db]
    (let [lists (reading-lists.state/reading-lists db)
          lookup-saturation (saturation-lookup-fn lists)]
      (->> lists
           (sort-by :position)
           (map (fn [{:keys                                                                             [id current-book current-chapter title
                                                                   ^firestore/Timestamp last-read-date] :as reading-list}]
                  {:id             id
                   :title          title
                   :chapter        (str (domain.books/book-title current-book) " " current-chapter)
                   :youversion-url (preferences.state/youversion-url db current-book current-chapter)
                   :data           reading-list
                   :saturation     (lookup-saturation reading-list)}))))))


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
