(ns bible.domain.projections
  (:require [bible.domain.books :as domain.books]
            [clojure.math :as math]))

(defprotocol ChapterReadEventProjection
  (projection-type [this])
  (initial-state [this])
  (next-state [this state event]))



;; PROJECTION IMPLS
(def times-books-read-projection
  (reify ChapterReadEventProjection
    (projection-type [_] "times-books-read")
    (initial-state [this]
      (->> domain.books/books
           (map (fn [{:keys [book-id]}]
                  {:book-id book-id
                   :total   0}))))
    (next-state [this state event]
      (let [state             (vec state)
            {:keys [book-id]} event
            chapters          (get-in domain.books/books-by-id [book-id :chapters])
            last-chapter?      (= chapters (:chapter event))
            increase          (double (/ 1 chapters))]
        (update-in state [(dec book-id) :total] #(cond-> (+ % increase)
                                                   last-chapter?
                                                   math/round))))))


;; CORE

(def projections
  [times-books-read-projection])


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
