(ns bible.web.content
  (:require [tongue.core :as tongue]))

(def ^:private default-dict :en)

(def ^:private dictionary
  {:tongue/fallback :en

   :en
   {:app/title "Read Your Bible"

    :navigation/login  "login"
    :navigation/logout "logout"

    :registration/heading "Let's get started"
    :registration/explainer-text "We just need a little bit of information to get you going. If you have never used Professor Grant Horner's Bible-Reading System, Leave the Starting Day as 1. If you have select the day you want to start on."
    :registration/button-text "Start"

    :login/explainer-text "How do you want to log in?"

    :tongue/missing-key "Missing key {1}"

    :translation/en "English: "
    :translation/af "Afrikaans: "}})


(def ^:private translate (tongue/build-translate dictionary))


(defn value [& keys]
  (apply translate default-dict keys))

(defn app-title [] (value :app/title))

(defn navigation-login [] (value :navigation/login))
(defn navigation-logout [] (value :navigation/logout))

(defn registration-heading [] (value :registration/heading))
(defn registration-explainer-text [] (value :registration/explainer-text))
(defn registration-button-text [] (value :registration/button-text))

(defn login-explainer-text [] (value :login/explainer-text))

(defn translation-language [l]
  (value (keyword (str "translation/" l))))
