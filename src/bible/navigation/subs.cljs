(ns bible.navigation.subs
  (:require [re-frame.core :as rf]
            [bible.navigation.state :as state]))

(rf/reg-sub
  ::current-page
  (fn [db _]
    (state/current-page db)))
