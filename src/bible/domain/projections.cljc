(ns bible.domain.projections
  (:require [bible.domain.books :as domain.books]
            [bible.domain.read-events :as domain.read-events]
            [clojure.math :as math]
            ["firebase/firestore" :as firestore]))

(defprotocol ChapterReadEventProjection
  (projection-type [this])
  (initial-state [this])
  (next-state [this state event]))


(defn round-2 [n]
  (-> n
      (* 100)
      math/round
      (/ 100)
      double))

(defn round-3 [n]
  (-> n
      (* 1000)
      math/round
      (/ 1000)
      double))


(def projection-type-times-read "books-read")
(def projection-lists-times-read "lists-read")
(def projection-streaks "streaks")
(def projection-read-history "read-history")
(def projection-timeline "timeline")


;; PROJECTION IMPLS

(defn times-read [books]
  (let [chapters (->> books
                      (map :chapters)
                      (reduce into)
                      (map :total))
        total-chapters (count chapters)
        least-read-total (first (sort chapters))
        read-total (->> chapters
                        (filter #(> % least-read-total))
                        count)]
    (round-3 (+ least-read-total (/ read-total total-chapters)))))


(def times-read-projection
  (reify ChapterReadEventProjection

    (projection-type [_] projection-type-times-read)

    (initial-state [this]
      {:books (->> domain.books/books
                   (map (fn [{:keys [book-id chapters testament]}]
                          {:book-id book-id
                           :testament testament
                           :total   0
                           :chapters (->> (range 1 (inc chapters))
                                          (map (fn [c]
                                                 {:chapter c
                                                  :total   0})))})))
       :testaments [{:testament "new" :total     0}
                    {:testament "old" :total     0}]
       :bible 0})

    (next-state [this state event]
      (let [state             (-> state
                                  (update :books vec)
                                  (update :testaments vec))
            {:keys [book-id chapter]} event
            chapters          (get-in domain.books/books-by-id [book-id :chapters])
            last-chapter?      (= chapters (:chapter event))
            increase          (double (/ 1 chapters))
            state-next (update-in state [:books (dec book-id)]
                                  (fn [book]
                                    (-> book
                                        (update :total #(cond-> (+ % increase)
                                                          last-chapter?
                                                          math/round))
                                        (update :chapters vec)
                                        (update-in [:chapters (dec chapter) :total] inc))))]
        (-> state-next
            (assoc :bible (times-read (:books state-next))
                   :testaments (->> (:books state-next)
                                    (group-by :testament)
                                    (map (fn [[testament books]]
                                           {:testament testament
                                            :total (times-read books)})))))))))


(def lists-read-projection
  (reify ChapterReadEventProjection

    (projection-type [_] projection-lists-times-read)

    (initial-state [_]
      [])
    (next-state [_ state event]
      (let [lookup (->> state
                        (map (juxt :list-id identity))
                        (into {}))
            {:keys [list-id read-index]} (:type-data event)]
        (if-not (and list-id read-index)
          state
          (let [read-list (-> (get lookup list-id {:list-id list-id :chapters-read 0})
                              (update :chapters-read inc))]
            (vals
              (assoc lookup list-id read-list))))))))

(def seconds-in-a-day 86400)

(def initial-streak-data {:last-read-date (firestore/Timestamp.fromMillis 0)
                          :total          0
                          :previous-max   0})

(defn- update-streak [streak]
  (let [{:keys [last-read-date previous-max total]} streak
        now-ts (firestore/Timestamp.now)
        midnight-date (.toDate now-ts)
        _             (.setHours midnight-date 0 0 0 0)
        midnight-today (-> midnight-date
                           (firestore/Timestamp.fromDate)
                           (.-seconds))
        midnight-yesterday (- midnight-today seconds-in-a-day)
        last-read-today? (> (.-seconds last-read-date) midnight-today)
        last-read-yesterday? (> (.-seconds last-read-date) midnight-yesterday)]
    (cond
      last-read-today?
      (assoc streak :last-read-date now-ts)

      last-read-yesterday?
      (-> streak
          (assoc :last-read-date now-ts)
          (update :total inc))

      :else
      (assoc streak
             :last-read-date now-ts
             :total          1
             :previous-max   (max previous-max total)))))


(defn- update-readlist-streaks [list-streaks event]
  (let [lookup (->> list-streaks
                    (map (juxt :list-id identity))
                    (into {}))
        list-id (get-in event [:type-data :list-id])
        streak (get lookup list-id (assoc initial-streak-data :list-id list-id))]
    (-> (assoc lookup list-id (update-streak streak))
        vals)))


(defn currently-in-streak [streak]
  (let [midnight-date (.toDate (firestore/Timestamp.now))
        _ (.setHours midnight-date 0 0 0 0)
        midnight-seconds (-> midnight-date
                          (firestore/Timestamp.fromDate)
                          (.-seconds))
        check-seconds (- midnight-seconds seconds-in-a-day)]
    (>= (.-seconds (:last-read-date streak))
        check-seconds)))


(def streaks-projection
  (reify ChapterReadEventProjection

    (projection-type [_] projection-streaks)

    (initial-state [_]
      {:daily initial-streak-data
       :reading-lists []})
    (next-state [_ state event]
      (cond-> state

        (or (domain.read-events/list-read-event? event)
            (domain.read-events/manual-read-event? event))
        (update :daily update-streak)

        (domain.read-events/list-read-event? event)
        (update :reading-lists update-readlist-streaks event)))))


(def read-history-projection
  (reify ChapterReadEventProjection

    (projection-type [_] projection-read-history)

    (initial-state [_]
      [])
    (next-state [_ state event]
      (if (or (domain.read-events/list-read-event? event)
              (domain.read-events/manual-read-event? event))
        (let [lookup           (->> state
                                    (map (juxt #(.-seconds (:date %)) identity))
                                    (into {}))
              ^firestore/Timestamp read-date (:read-date event)
              read-js-date     (.toDate read-date)
              _                (.setHours read-js-date 0 0 0 0)
              midnight         (firestore/Timestamp.fromDate read-js-date)
              midnight-seconds (.-seconds midnight)
              entry            (get lookup midnight-seconds {:date          midnight
                                                             :chapters-read 0})
              entry'           (-> entry
                                   (assoc :date midnight)
                                   (update :chapters-read inc))]
          (->> (assoc lookup midnight-seconds entry')
               vals
               (sort-by #(.-seconds (:date %)))
               reverse
               (take 365)))
        state))))


(def timeline-projection
  (reify ChapterReadEventProjection

    (projection-type [_] projection-timeline)

    (initial-state [_]
      [])

    (next-state [_ state event]
      (if (domain.read-events/list-read-event? event)
        (let [{:keys [book-id
                      read-date
                      chapter]} event
              lookup            (->> state
                                     (map (juxt :book-id identity))
                                     (into {}))
              entry             (get lookup book-id {:book-id    book-id
                                                     :start-date read-date
                                                     :end-date   read-date})
              entry'            (if (= 1 chapter)
                                  (assoc entry
                                         :start-date read-date
                                         :end-date read-date)
                                  (assoc entry :end-date read-date))]
          (vals (assoc lookup book-id entry')))
        state))))

;; CORE

(def projections
  [times-read-projection
   lists-read-projection
   streaks-projection
   read-history-projection
   timeline-projection])


(defn initialize-projection [user-id projection-impl]
  (let [t (projection-type projection-impl)]
    {:projection-type t
     :id              (str user-id "-" t)
     :user-id         user-id
     :state           (initial-state projection-impl)}))


(defn projections-by-type [projections]
  (->> projections
       (map (juxt :projection-type identity))
       (into {})))


(defn projection-state-by-type [projections]
  (->> projections
       (map (juxt :projection-type :state))
       (into {})))


(defn next-projections [user-id current-projections events]
  (let [projections-lookup (projections-by-type current-projections)
        events (sort-by (juxt :book-id :chapter) events)] ;;TODO think about this
    (->> projections
         (map (fn [projection-impl]
                (let [current-projection (get projections-lookup (projection-type projection-impl)
                                              (initialize-projection user-id projection-impl))]
                  (reduce
                    (fn [p evt]
                      (update p :state #(next-state projection-impl % evt)))
                    current-projection
                    events)))))))
