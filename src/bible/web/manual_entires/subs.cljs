(ns bible.web.manual-entires.subs
  (:require [re-frame.core :as rf]
            [bible.web.manual-entires.state :as manual-entries.state]))


(rf/reg-sub
  ::selection
  (fn [db]
    (manual-entries.state/selection db)))


(defn selection-sub []
  (rf/subscribe [::selection]))
