(ns bible.web.global-projections.state
  (:require ["firebase/firestore" :as firestore]))


(defn initial-state []
  {::daily-read-stats {}})


(defn add-daily-read-stat [db id stat]
  (assoc-in db [::daily-read-stats id] stat))


(defn modify-daily-read-stat [db id stat]
  (assoc-in db [::daily-read-stats id] stat))


(defn remove-daily-read-stat [db id]
  (update db ::daily-read-stats dissoc id))


(defn current-daily-stat [db]
  (let [midnight (js/Date.)]
    (.setUTCHours midnight 0 0 0 0)
    (let [check-date (firestore/Timestamp.fromDate midnight)
          stat (->> (::daily-read-stats db)
                    vals
                    (sort-by :date)
                    last)]
      (when (and stat
                 (>= (:date stat) check-date)
                 (>= (:total-users stat) 2))
        stat))))
