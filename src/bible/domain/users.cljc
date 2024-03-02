(ns bible.domain.users)

(defn firebase-auth-user->user
  [auth-user]
  {:user-id (.-uid auth-user)})
