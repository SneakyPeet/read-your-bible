(ns bible.functions.core
  (:require ["firebase-admin/app" :as firebase-app]
            ["firebase-functions/v2" :as functions]
            #_["firebase-functions/v2/https" :as functions-https]
            #_[bible.functions.new-users :as new-users]
            [bible.functions.chapters-read-today :as read-today]))


(defonce app (firebase-app/initializeApp))


(def exports
  #js {#_#_:newUsers new-users/export
       :readToday read-today/export})
