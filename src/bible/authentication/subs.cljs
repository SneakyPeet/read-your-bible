(ns bible.authentication.subs
  (:require [re-frame.core :as rf]
            [bible.authentication.state :as authentication.state]))


(rf/reg-sub
  ::user-id
  (fn [db]
    (authentication.state/user-id db)))
