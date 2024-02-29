(ns bible.navigation.routes)

(def loading-page "/loading")
(def login-page "/login")
(def dashboard-page "/dashboard")

(defn logged-in-route []
  dashboard-page)
