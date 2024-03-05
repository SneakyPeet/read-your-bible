(ns bible.domain.youversion
  (:require [bible.domain.books :as domain.books]
            [bible.domain.translations :as domain.translations]))


(defn get-youversion-url [translation book-id chapter]
  (let [translation-id (domain.translations/get-youversion-id translation)
        book-alias       (domain.books/get-youversion-alias book-id)]
    (str "https://www.bible.com/bible/"
         translation-id "/" book-alias "." chapter)))
