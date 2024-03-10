(ns bible.web.manual-entires.views
  (:require [re-frame.core :as rf]
            [bible.domain.books :as domain.books]
            [bible.web.manual-entires.subs :as manual-entries.subs]
            [bible.web.manual-entires.events :as manual-entries.events]))


(def book-options
  (->> domain.books/books
       (map (fn [{:keys [book-id title]}]
              [:option {:key book-id :value book-id} title]))))


(defn capture []
  (let [{:keys [book-id selected-chapters capturing?]} @(manual-entries.subs/selection-sub)
        total-chapters (domain.books/book-total-chapters book-id)]
    [:div
     (if capturing?
       [:div.has-text-centered
        [:div.block
         [:h3.is-size-3.has-font-weight-bold "Manual capture"]
         [:p "Pick a book and chapters to manually mark as read today"]]
        [:div.field
         [:div.control
          [:div.select
           [:select#translation-select
            {:value     book-id
             :on-change #(manual-entries.events/select-book (js/parseInt (.. % -target -value)))}
            book-options]]
          [:div.help "capture one book at a time"]]]
        [:div.buttons.is-centered
         (->> (range 1 (inc total-chapters))
              (map (fn [i]
                     (let [selected? (contains? selected-chapters i)
                           f (if selected?
                               manual-entries.events/deselect-chapter
                               manual-entries.events/select-chapter)]
                       [:button.button.is-small
                        {:key   i
                         :on-click #(f i)
                         :class (when selected? "is-primary")}
                        i]))))]
        [:div.buttons.is-centered
         [:button.button.is-primary
          {:on-click #(manual-entries.events/capture)}
          (if (empty? selected-chapters) "Cancel" "Capture")]]]
       [:div.buttons.is-centered
        [:button.button
         {:on-click #(manual-entries.events/start-capturing)}
         "Capture Manaul Entries"]]
       )]))
