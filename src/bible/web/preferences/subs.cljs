(ns bible.web.preferences.subs
  (:require [re-frame.core :as rf]
            [bible.web.preferences.state :as preferences.state]))


(rf/reg-sub
  ::translation
  (fn [db]
    (preferences.state/translation db)))


(def translation-sub ::translation)
