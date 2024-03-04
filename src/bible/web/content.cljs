(ns bible.web.content
  (:require [tongue.core :as tongue]))

(def ^:private default-dict :en)

(def ^:private dictionary
  {:tongue/fallback :en

   :en
   {:navigation/login  "login/register"
    :navigation/logout "logout"

    :registration/heading "Welcome to TODO"
    :registration/explainer-text "TODO Explain"
    :registration/button-text "Start"

    :login/explainer-text "TODO"

    :tongue/missing-key "Missing key {1}"}})


(def ^:private translate (tongue/build-translate dictionary))


(defn value [& keys]
  (apply translate default-dict keys))

(defn navigation-login [] (value :navigation/login))
(defn navigation-logout [] (value :navigation/logout))

(defn registration-heading [] (value :registration/heading))
(defn registration-explainer-text [] (value :registration/explainer-text))
(defn registration-button-text [] (value :registration/button-text))

(defn login-explainer-text [] (value :login/explainer-text))
