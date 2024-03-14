(ns bible.web.authentication.events
  (:require [re-frame.core :as rf]
            ["firebase/auth" :as firebase-auth]
            [bible.domain.users :as users]
            [bible.web.authentication.state :as authentication.state]))


(def auth (firebase-auth/getAuth))

(def providers [{:title "Google"
                 :provider (firebase-auth/GoogleAuthProvider.)}])


;; Listen for authentication events

(rf/reg-event-fx
  ::initialize
  (fn [_ _]
    {::firebase-on-auth-state-changed [::logged-in ::logged-out]}))

(def initialize-evt [::initialize])

(rf/reg-event-fx
  ::logged-in
  (fn [{:keys [db]} [_ firebase-user]]
    {:db (authentication.state/login
           db (users/firebase-auth-user->user firebase-user))
     :dispatch (authentication.state/logged-in-event db)}))


(rf/reg-event-fx
  ::logged-out
  (fn [{:keys [db]} _]
    {:db (authentication.state/logout db)
     :dispatch (authentication.state/logged-out-event db)}))


(rf/reg-fx
  ::firebase-on-auth-state-changed
  (fn [[signed-in-evt signed-out-evt]]
    (firebase-auth/onAuthStateChanged
      auth
      (fn [firebase-user]
        (if firebase-user
          (rf/dispatch [signed-in-evt firebase-user])
          (rf/dispatch [signed-out-evt]))))

    (when (firebase-auth/isSignInWithEmailLink auth js/window.location.href)
      (let [email (js/window.prompt "Please provide your email")]
        (-> (firebase-auth/signInWithEmailLink auth email js/window.location.href)
            (.catch (fn [err]
                      (js/alert (.-message err))
                      (js/console.log err))))))))


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

(rf/reg-event-fx
  ::start-anonymous-authentication
  (fn [_ _]
    {::firebase-start-anonymous-authentication nil}))


(defn start-anonymous-authentication []
  (rf/dispatch-sync [::start-anonymous-authentication]))


(rf/reg-fx
  ::firebase-start-anonymous-authentication
  (fn []
    (firebase-auth/signInAnonymously auth)))


(rf/reg-event-fx
  ::start-email-authentication
  (fn [_ [_ email]]
    {::firebase-start-email-authentication email}))


(defn start-email-authentication [email]
  (rf/dispatch-sync [::start-email-authentication email]))


(goog-define auth-redirect-domain "http://localhost:3020")


(rf/reg-fx
  ::firebase-start-email-authentication
  (fn [email]
    (-> (firebase-auth/sendSignInLinkToEmail auth email (clj->js {:url js/window.location.href
                                                                  :handleCodeInApp true}))
        (.then (fn [result]
                 (js/alert "Check your email for a login link")
                 (js/console.log result)))
        (.catch (fn [err]
                  (js/alert (.-message err))
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
