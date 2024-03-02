(ns bible.web.components.core
  (:require [re-frame.core :as rf]))


(defn container [child]
  [:div.paper.container child])


(defn menu [title menu-items]
  [:div
   [:div {:style {:margin-bottom "1rem"
                  :text-align    "right"}}
    (when title [:strong title])
    (->> menu-items
         (map-indexed
           (fn [i item]
             [:span {:key   i
                     :style {:margin "0 0.5rem"}} item])))]])


(defn menu-item [text event]
  [:a {:on-click #(rf/dispatch event)} text])


(defn menu-page [title menu-items content]
  [:div {:style {:height "85vh"}}
   [menu title menu-items]
   content])

(defn button [opts child]
  [:button opts child])


(defn buttons [buttons]
  (into [:div] buttons))


(defn form [opts children]
  [:form opts
   children])


(defn text-input [opts children]
  [:input (assoc opts :type "text") children])
