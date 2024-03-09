(ns bible.web.projections.subs
  (:require [re-frame.core :as rf]
            [bible.web.projections.state :as projections.state]
            [bible.domain.projections :as domain.projections]
            [bible.web.reading-lists.state :as reading-lists.state]
            [bible.domain.reading-lists :as domain.reading-lists]
            ["firebase/firestore" :as firestore]))


(defn round-2 [n]
  (-> n
      (* 100)
      js/Math.round
      (/ 100)
      double))


(rf/reg-sub
  ::projection-state-by-type
  (fn [db]
    (projections.state/projection-state-by-type db)))


(rf/reg-sub
  ::times-read
  :<- [::projection-state-by-type]
  (fn [projections]
    (let [{:keys [bible testaments]} (get projections domain.projections/projection-type-times-read)]
      (into [["Bible" (round-2 (* 100 bible))]]
            (map (fn [{:keys [testament total]}] [testament (* 100 total)]) testaments)))))

(def times-read-sub ::times-read)

;;https://gist.github.com/remvee/2735ee151ab6ec075255
(defn week-number
  "Week number according to the ISO-8601 standard, weeks starting on
  Monday. The first week of the year is the week that contains that
  year's first Thursday (='First 4-day week'). The highest week number
  in a year is either 52 or 53."
  [ts]
  (let [year       (.getFullYear ts)
        month      (.getMonth ts)
        date       (.getDate ts)
        day        (.getDay ts)
        thursday   (js/Date. year month (- (+ date 4) (if (= 0 day) 7 day)))
        year-start (js/Date. year 0 1)]
    (Math/ceil (/ (+ (/ (- (.getTime thursday)
                           (.getTime year-start))
                        (* 1000 60 60 24))
                     1)
                  7))))

(def activity-lookup (->> (for [week (range 1 54)
                                day-of-week (range 1 8)]
                            [[day-of-week week] 0])
                          (into {})))



(rf/reg-sub
  ::activity
  :<- [::projection-state-by-type]
  (fn [projections]
    (let [history (get projections domain.projections/projection-read-history)]
      (->> history
           (reduce
             (fn [r {:keys [chapters-read ^firestore/Timestamp date]}]
               (let [jsdate (.toDate date)
                     week   (week-number jsdate)
                     day-of-week (.getDay jsdate)]
                 (assoc r [day-of-week week] chapters-read)))
             activity-lookup)
           (group-by ffirst)
           (sort-by first)
           reverse
           (map (fn [[day-of-week vals]]
                  {:name (get ["M" "T" "W" "T" "F" "S" "S"] (dec day-of-week))
                   :data (->> vals
                              (map (fn [[[_ week] total]]
                                     {:x week
                                      :y total}))
                              (sort-by :x))}))))))


(def activity-sub ::activity)


(rf/reg-sub
  ::lists-read
  (fn [db]
    (let [projections (projections.state/projection-state-by-type db)
          lists-times-read (get projections domain.projections/projection-lists-times-read)
          lists (reading-lists.state/reading-lists-by-id db)
          stats (->> lists-times-read
                     (map (fn [{:keys [list-id chapters-read]}]
                            (let [list           (get lists list-id)
                                  total-chapters (domain.reading-lists/total-chapters list)
                                  n              (/ chapters-read total-chapters)
                                  n'             (- n (js/Math.floor n))
                                  percent        (js/Math.round (* 100 n'))]
                              {:title    (:title list)
                               :position (:position list)
                               :percent  percent})))
                     (sort-by :position)
                     )]
      {:labels (map :title stats)
       :series (map :percent stats)})))


(def lists-read-sub ::lists-read)
