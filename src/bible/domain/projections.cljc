(ns bible.domain.projections
  (:require [bible.domain.books :as domain.books]
            [clojure.math :as math]))

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

;; CORE

(def projections
  [times-read-projection
   lists-read-projection])


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
