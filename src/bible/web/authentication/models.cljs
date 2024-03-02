(ns bible.web.authentication.models)

(defn firebase-auth-user->user
  [auth-user]
  {:user-id (.-uid auth-user)})
