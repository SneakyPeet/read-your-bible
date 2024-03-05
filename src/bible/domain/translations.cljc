(ns bible.domain.translations)

(def translations-config
  ["NLT" "New Living Translation" 116
   "NIV" "New International Version" 111
   "ESV" "English Standard Version 2016" 59
   "KJV" "King James Version" 1
   "NKJV" "New King James Version" 114
   "ABA" "Die Bybel 2020 Vertaling" 2])

(def default-translation "NLT")

(def translation-config-keys [:id :title :youversion-id])

(def translations
  (->> translations-config
       (partition (count translation-config-keys))
       (map #(zipmap translation-config-keys %))))


(def translations-by-key
  (->> translations
       (map (juxt :id identity))
       (into {})))


(defn get-youversion-id [k]
  (get-in translations-by-key [k :youversion-id]))
