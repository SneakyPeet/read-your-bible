(ns bible.authentication.events
  (:require [re-frame.core :as rf]
            ["firebase/auth" :as firebase-auth]
            [bible.authentication.models :as authentication.models]
            [bible.authentication.state :as authentication.state]
            #_[bible.notebooks.events :as notebooks.events]
            [bible.firebase.firestore :as firestore-fx]
            [bible.navigation.routes :as navigation.routes]))


(def auth (firebase-auth/getAuth))

(def providers [{:title "Google"
                 :provider (firebase-auth/GoogleAuthProvider.)}])


;; Listen for authentication events

(rf/reg-event-fx
  ::initialize
  (fn [_ _]
    {::firebase-on-auth-state-changed [::logged-in ::logged-out]}))


(rf/reg-event-fx
  ::logged-in
  (fn [{:keys [db]} [_ firebase-user]]
    {:db (authentication.state/login db (authentication.models/firebase-auth-user->user firebase-user))
     :goto (navigation.routes/logged-in-route)
     ;;:dispatch [::notebooks.events/subscribe-to-notebooks-changes]
     })) ;; TODO


(rf/reg-event-fx
  ::logged-out
  (fn [{:keys [db]} _]
    {:db   (authentication.state/logout db)
     :dispatch [:app/reset]
     :goto navigation.routes/login-page
     ::firestore-fx/unsub-all-snapshots nil}))


(rf/reg-fx
  ::firebase-on-auth-state-changed
  (fn [[signed-in-evt signed-out-evt]]
    (firebase-auth/onAuthStateChanged
      auth
      (fn [firebase-user]
        (if firebase-user
          (rf/dispatch [signed-in-evt firebase-user])
          (rf/dispatch [signed-out-evt]))))))


;; Start login process

(rf/reg-event-fx
  ::start-authentication
  (fn [_ [_ auth-provider]]
    {::firebase-start-authentication auth-provider}))


(defn start-login [auth-provider]
  (rf/dispatch-sync [::start-authentication auth-provider]))


(rf/reg-fx
  ::firebase-start-authentication
  (fn [provider]
    (-> (firebase-auth/signInWithPopup auth provider)
        #_(.then (fn [result]
                   (prn "IN")
                   (js/console.log result)))
        #_(.catch (fn [err]
                    (prn "FAIL")
                    (js/console.log err))))))


;; Logout

(rf/reg-event-fx
  ::logout
  (fn [_ _]
    {::firebase-logout nil}))


(rf/reg-fx
  ::firebase-logout
  (fn [_]
    (firebase-auth/signOut auth))) ;; This will trigger ::firebase-on-auth-state-changed


(defn logout []
  (rf/dispatch [::logout]))


(def logout-evt [::logout])
