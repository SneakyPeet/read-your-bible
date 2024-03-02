(ns bible.web.authentication.subs
  (:require [re-frame.core :as rf]
            [bible.web.authentication.state :as authentication.state]))


(rf/reg-sub
  ::user-id
  (fn [db]
    (authentication.state/user-id db)))
