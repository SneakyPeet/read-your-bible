(ns bible.domain.reading-lists
  (:require [bible.domain.books :as domain.books]
            [bible.domain.read-events :as domain.read-events]))

(def default-reading-lists
  [{:title "1. Gospel"
    :position 1
    :books [40 41 42 43]
    :current-book 40
    :current-chapter 1
    :read-index 0}
   {:title "2. Torah"
    :position 2
    :books [1 2 3 4 5]
    :current-book 1
    :current-chapter 1
    :read-index 0}
   {:title "3. Letters to churches"
    :position 3
    :books [45 46 47 48 49 50 51 58]
    :current-book 45
    :current-chapter 1
    :read-index 0}
   {:title "4. Personal letters"
    :position 4
    :books [52 53 54 55 56 57 59 60 61 62 63 64 65 66]
    :current-book 52
    :current-chapter 1
    :read-index 0}
   {:title "5. Wisdom"
    :position 5
    :books [18 21 22]
    :current-book 18
    :current-chapter 1
    :read-index 0}
   {:title "6. Psalms"
    :position 6
    :books [19]
    :current-book 19
    :current-chapter 1
    :read-index 0}
   {:title "7. Proverbs"
    :position 7
    :books [20]
    :current-book 20
    :current-chapter 1
    :read-index 0}
   {:title "8. History"
    :position 8
    :books [6 7 8 9 10 11 12 13 14 15 16 17]
    :current-book 6
    :current-chapter 1
    :read-index 0}
   {:title "9. Prophets"
    :position 9
    :books [23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39]
    :current-book 23
    :current-chapter 1
    :read-index 0}
   {:title "10. Acts of the Apostles"
    :position 10
    :books [44]
    :current-book 44
    :current-chapter 1
    :read-index 0}])


(defn- step-reading-list
  [f reading-list date]
  (let [{:keys [books read-index]} reading-list
        next-index                 (f read-index)
        {:keys [book-id chapter]}  (domain.books/find-book-chapter-at-index books next-index)]
    (assoc reading-list
           :current-book book-id
           :current-chapter chapter
           :read-index next-index
           :last-read-date date)))


(defn increment-reading-list
  [reading-list date]
  (step-reading-list inc reading-list date))


(defn decrement-reading-list
  [reading-list date]
  (step-reading-list dec reading-list date))


(comment

  (increment-reading-list (first default-reading-lists) nil)

  (decrement-reading-list (first default-reading-lists) nil)

  ,)


(defn init-default-reading-list [default-reading-list user-id create-date read-index]
  (let [reading-list (assoc default-reading-list
                            :id (str user-id "-" (:position default-reading-list))
                            :user-id user-id
                            :last-read-date create-date
                            :create-date create-date)
        reduce-initial-data {:reading-list reading-list
                             :events []}]
    (reduce
      (fn [state _]
        (-> state
            (update :reading-list increment-reading-list create-date)
            (update :events conj (domain.read-events/read-on-registration-event (:reading-list state) create-date))))
      reduce-initial-data
      (range read-index))))
