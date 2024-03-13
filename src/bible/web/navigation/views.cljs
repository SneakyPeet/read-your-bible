(ns bible.web.navigation.views
  (:require [re-frame.core :as rf]
            [bible.web.navigation.subs :as navigation.subs]
            [bible.web.navigation.routes :as navigation.routes]
            [bible.web.authentication.views :as authentication.views]
            [bible.web.authentication.events :as authentication.events]
            [bible.web.reading-lists.views :as reading-lists.views]
            [bible.web.reading-lists.subs :as reading-lists.subs]
            [bible.web.registration.views :as registration.views]
            [bible.web.authentication.subs :as authentication.subs]
            [bible.web.projections.views :as projection.views]
            [bible.web.preferences.views :as preferences.views]
            [bible.web.manual-entires.views :as manual-entries.views]
            [bible.web.content :as cn]))


(defn wrapper [child]
  (let [logged-in? @(rf/subscribe authentication.subs/logged-in-sub)]
    [:div
     [:section {:class (when logged-in? "section pt-0")}
      [:div
       [:div child]]]]))


(defn logout []
  (let [logged-in? @(rf/subscribe authentication.subs/logged-in-sub)]
    (when logged-in?
      [:div.buttons
       [:a.button
        {:on-click authentication.events/logout}
        (cn/navigation-logout)]])))


(defn landing []
  [:div
   [:div.hero.is-primary
    [:div.hero-body
     [:h1.title.is-3
      "Welcome to the read your bible app!"]
     [:h3.sub-title.is-5
      "An free electronic bookmark for the"
      " Professor Grant Horner's Bible-Reading System"]]]
   [:div.section.content
    [:h2.title.is-4 "How does it work?"]
    [:p [:b "Click on the next chapter to read."]" This will open the youversion app and you can pick which translation to use."]
    [:img.mb-5 {:src "/img/step1.png" :style {:max-width "300px"}}]
    [:p "Once you have read the chapter" [:b " click the button to mark the chapter as read."]]
    [:img.mb-5 {:src "/img/step2.png" :style {:max-width "300px"}}]
    [:p [:b "Read the next chapter of the next book."]" Try and read from all 10 lists in one day."]
    [:img.mb-5 {:src "/img/step3.png" :style {:max-width "300px"}}]
    [:p "Feel like reading more from the same book?" [:b " You can read from any list at any time."]]
    [:img.mb-5 {:src "/img/step4.png" :style {:max-width "300px"}}]
    [:p "Want to read a book and chapter not in the current reading list? Simply " [:b "capture the chapters you read manually."]]
    [:img.mb-5 {:src "/img/manual-capture.png" :style {:max-width "300px"}}]
    [:p [:b "See"] " progress for each list."]
    [:img.mb-5 {:src "/img/list-projection.png" :style {:max-width "300px"}}]
    [:p [:b "See"] " how much of the bible you have read."]
    [:img.mb-5 {:src "/img/chapter-projection.png" :style {:max-width "300px"}}]
    [:hr]
    [:div.block [authentication.views/login-page]]
    [:div {:style {:margin-bottom "5rem"}}]]])


(defn loader []
  [:div.is-flex.is-flex-direction-column.is-align-items-center
   {:style {:margin "40vh 0"}}
   [:div.page-loader]])


(defn app []
  (let [any-reading-lists? @(rf/subscribe [reading-lists.subs/any-sub])]
    (if any-reading-lists?
      [:div.mt-2
       [reading-lists.views/reading-list]
       [:div.block [projection.views/streak]]
       [:div.block [manual-entries.views/capture]]
       [:div.block [projection.views/all-charts]]
       [:div.block [preferences.views/set-translation]]
       [logout]]
      [loader])))


(defn current-page []
  (let [current-page @(rf/subscribe [::navigation.subs/current-page])]
    [wrapper
     (cond
       (= current-page navigation.routes/dashboard-page)
       [app]

       (= current-page navigation.routes/landing-page)
       [landing]

       (= current-page navigation.routes/loading-page)
       [loader]

       (= current-page navigation.routes/login-page) ;;TODO REMOVE
       [authentication.views/login-page]

       (= current-page navigation.routes/register-page)
       [registration.views/registration-page]

       :else
       [:h1 "No bible.web.navigation.views for " (str current-page)])]))
