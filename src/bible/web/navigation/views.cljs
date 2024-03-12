(ns bible.web.navigation.views
  (:require [re-frame.core :as rf]
            [bible.web.navigation.subs :as navigation.subs]
            [bible.web.navigation.routes :as navigation.routes]
            [bible.web.navigation.events :as navigation.events]
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
      [:div.container
       [:div child]]]]))


(defn logout []
  (let [logged-in? @(rf/subscribe authentication.subs/logged-in-sub)]
    (when logged-in?
      [:div.buttons.is-centered
       [:a.button
        {:on-click authentication.events/logout}
        (cn/navigation-logout)]])))


(defn landing []
  [:div
   [:div.content
    [:div.hero.is-primary {:style {:height "100vh"}}
     [:div.hero-body
      [:h1.title.has-text-centered
       {:style {:margin-top "2rem"
                :margin-bottom "5rem"}}
       "Welcome to the read your bible app!"]
      [:p.has-text-centered
       {:style {:margin-bottom "5rem"}}
       "This app is an electronic bookmark for the"
       " Professor Grant Horner's Bible-Reading System"]
      #_[:div
         [:h5 "How to use it?"]
         [:ul
          [:li "Read all 10 chapters as displayed on the page."]
          [:li "You can click the name of a book to mark it as read."]
          [:li "Click '>' to load the chapters for the next days reading."]
          [:li "The app saves the selected day on your current device."]
          [:li "You can also type in the day you want to jump to."]
          [:li "The blue progress bar shows chapter progress."]
          [:li "The green progress bar shows list progress."]
          [:li "Once you complete a list, that list will start over."]
          [:li "For more information, the plan can be downloaded " [:a {:href "/download.pdf" :target "_blank"} "here"] "."]]]
      [authentication.views/login-page]]]]])


(defn loader []
  [:div.is-flex.is-flex-direction-column.is-align-items-center
   {:style {:margin "40vh 0"}}
   [:div.page-loader]])


(defn app []
  (let [any-reading-lists? @(rf/subscribe [reading-lists.subs/any-sub])]
    (if any-reading-lists?
      [:div
       [:div.columns.mt-2
        [:div.column.is-half
         [:div.block
          [reading-lists.views/reading-list]
          [projection.views/streak]]
         [:div
          [:div.block [manual-entries.views/capture]]]]
        [:div.column
         [:div.block [projection.views/all-charts]]
         [:div.block [preferences.views/set-translation]]
         [logout]]]]
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
