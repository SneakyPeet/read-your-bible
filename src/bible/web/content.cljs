(ns bible.web.content
  (:require [tongue.core :as tongue]))

(def ^:private default-dict :en)

(def ^:private dictionary
  {:tongue/fallback :en

   :en
   {:navigation/login  "login/register"
    :navigation/logout "logout"

    :tongue/missing-key "Missing key {1}"}})


(def ^:private translate (tongue/build-translate dictionary))


(defn value [& keys]
  (apply translate default-dict keys))

(defn navigation-login [] (value :navigation/login))
(defn navigation-logout [] (value :navigation/logout))