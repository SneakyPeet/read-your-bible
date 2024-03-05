(ns bible.web.preferences.state
  (:require [bible.domain.translations :as domain.translations]
            [bible.domain.youversion :as domain.youversion]))


(defn initial-state []
  {::preferences {:translation domain.translations/default-translation}})


(defn set-preferences [db preferences]
  (assoc db ::preferences preferences))


(defn remove-preferences [db]
  (dissoc db ::preferences))


(defn preferences [db]
  (::preferences db))


(defn translation [db]
  (:translation (::preferences db)))


(defn youversion-url [db book-id chapter]
  (let [translation (translation db)]
    (domain.youversion/get-youversion-url translation book-id chapter)))
