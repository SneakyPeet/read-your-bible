(ns bible.web.reading-lists.subs
  (:require [re-frame.core :as rf]
            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.web.preferences.state :as preferences.state]
            ["firebase/firestore" :as firestore]
            [bible.domain.books :as domain.books]))

;; this is too wild
(defn- highlight-lookup-fn [lists]
  (let [seconds (->> lists
                     (map (fn [{:keys [^firestore/Timestamp last-read-date]}]
                            (.-seconds last-read-date)))
                     set
                     (sort))
        seconds-count (count seconds)
        multiplier (if (<= seconds-count 1)
                     0.0
                     (double (/ 100 seconds-count)))
        oldest-second (first seconds)
        *next-set?   (atom false)
        seconds-lookup (->> seconds
                            reverse
                            (map-indexed (fn [i s]
                                           [s (js/Math.round (- 100 (* i multiplier)))]))
                            (into {}))
        lookup (->> lists
                    (sort-by (fn [{:keys [^firestore/Timestamp last-read-date
                                          position]}]
                               [(.-seconds last-read-date) position]))
                    (map (fn [{:keys [^firestore/Timestamp last-read-date
                                      id]}]
                           (let [sec (.-seconds last-read-date)
                                 oldest? (= sec oldest-second)]
                             [id
                              {:next? (if (and oldest?
                                               (false? @*next-set?))
                                        (reset! *next-set? true) ;returns true
                                        false)
                               :saturation (if oldest?
                                             0
                                             (get seconds-lookup sec))}])))
                    (into {}))]
    (fn [list]
      (get lookup (:id list) {:next? false :saturation 0}))))


(rf/reg-sub
  ::reading-lists-view
  (fn [db]
    (let [lists (reading-lists.state/reading-lists db)
          lookup-highlight (highlight-lookup-fn lists)]
      (->> lists
           (sort-by :position)
           (map (fn [{:keys [id
                             current-book
                             current-chapter
                             title] :as reading-list}]
                  (let [{:keys [saturation next?]} (lookup-highlight reading-list)]
                    {:id             id
                     :title          title
                     :chapter        (str (domain.books/book-title current-book) " " current-chapter)
                     :youversion-url (preferences.state/youversion-url db current-book current-chapter)
                     :data           reading-list
                     :saturation     saturation
                     :next?          next?})))))))


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
