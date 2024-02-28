(ns bible.core)

;; BIBLE

;; Copied from the books.xlsx output column
(def ^:private book-config
  [1 "Genesis" 50 "Old" "GEN"
   2 "Exodus" 40 "Old" "EXO"
   3 "Leviticus" 27 "Old" "LEV"
   4 "Numbers" 36 "Old" "NUM"
   5 "Deuteronomy" 34 "Old" "DEU"
   6 "Joshua" 24 "Old" "JOS"
   7 "Judges" 21 "Old" "JDG"
   8 "Ruth" 4 "Old" "RUT"
   9 "1st Samuel" 31 "Old" "1SA"
   10 "2nd Samuel" 24 "Old" "2SA"
   11 "1st Kings" 22 "Old" "1KI"
   12 "2nd Kings" 25 "Old" "2KI"
   13 "1st Chronicles" 29 "Old" "1CH"
   14 "2nd Chronicles" 36 "Old" "2CH"
   15 "Ezra" 10 "Old" "EZR"
   16 "Nehemiah" 13 "Old" "NEH"
   17 "Esther" 10 "Old" "EST"
   18 "Job" 42 "Old" "JOB"
   19 "Psalms" 150 "Old" "PSA"
   20 "Proverbs" 31 "Old" "PRO"
   21 "Ecclesiastes" 12 "Old" "ECC"
   22 "Song of Songs" 8 "Old" "SNG"
   23 "Isaiah" 66 "Old" "ISA"
   24 "Jeremiah" 52 "Old" "JER"
   25 "Lamentations" 5 "Old" "LAM"
   26 "Ezekiel" 48 "Old" "EZK"
   27 "Daniel" 12 "Old" "DAN"
   28 "Hosea" 14 "Old" "HOS"
   29 "Joel" 3 "Old" "JOL"
   30 "Amos" 9 "Old" "AMO"
   31 "Obadiah" 1 "Old" "OBA"
   32 "Jonah" 4 "Old" "JON"
   33 "Micah" 7 "Old" "MIC"
   34 "Nahum" 3 "Old" "NAM"
   35 "Habakkuk" 3 "Old" "HAB"
   36 "Zephaniah" 3 "Old" "ZEP"
   37 "Haggai" 2 "Old" "HAG"
   38 "Zechariah" 14 "Old" "ZEC"
   39 "Malachi" 4 "Old" "MAL"
   40 "Matthew" 28 "New" "MAT"
   41 "Mark" 16 "New" "MRK"
   42 "Luke" 24 "New" "LUK"
   43 "John" 21 "New" "JHN"
   44 "Acts" 28 "New" "ACT"
   45 "Romans" 16 "New" "ROM"
   46 "1st Corinthians" 16 "New" "1CO"
   47 "2nd Corinthians" 13 "New" "2CO"
   48 "Galatians" 6 "New" "GAL"
   49 "Ephesians" 6 "New" "EPH"
   50 "Philippians" 4 "New" "PHP"
   51 "Colossians" 4 "New" "COL"
   52 "1st Thessalonians" 5 "New" "1TH"
   53 "2nd Thessalonians" 3 "New" "2TH"
   54 "1st Timothy" 6 "New" "1TI"
   55 "2nd Timothy" 4 "New" "2TI"
   56 "Titus" 3 "New" "TIT"
   57 "Philemon" 1 "New" "PHM"
   58 "Hebrews" 13 "New" "HEB"
   59 "James" 5 "New" "JAS"
   60 "1st Peter" 5 "New" "1PE"
   61 "2nd Peter" 3 "New" "2PE"
   62 "1st John" 5 "New" "1JN"
   63 "2nd John" 1 "New" "2JN"
   64 "3rd John" 1 "New" "3JN"
   65 "Jude" 1 "New" "JUD"
   66 "Revelation" 22 "New" "REV"])


(def ^:private book-config-keys
  [:book-id :title :chapters :testament :youversion-alias])


(def books
  (->> book-config
       (partition (count book-config-keys))
       (map #(zipmap book-config-keys %))))


(def books-by-id
  (->> books
       (map (juxt :book-id identity))
       (into {})))

;; LISTS

(def default-reading-lists
  [{:title "1"
    :books [40 41 42 43]
    :current-book 40
    :current-chapter 1
    :read-index 0}
   {:title "2"
    :books [1 2 3 4 5]
    :current-book 1
    :current-chapter 1
    :read-index 0}
   {:title "3"
    :books [45 46 47 48 49 50 51 58]
    :current-book 45
    :current-chapter 1
    :read-index 0}
   {:title "4"
    :books [52 53 54 55 56 57 59 60 61 62 63 64 65 66]
    :current-book 52
    :current-chapter 1
    :read-index 0}
   {:title "5"
    :books [18 21 22]
    :current-book 18
    :current-chapter 1
    :read-index 0}
   {:title "6"
    :books [19]
    :current-book 19
    :current-chapter 1
    :read-index 0}
   {:title "7"
    :books [20]
    :current-book 20
    :current-chapter 1
    :read-index 0}
   {:title "8"
    :books [6 7 8 9 10 11 12 13 14 15 16 17]
    :current-book 6
    :current-chapter 1
    :read-index 0}
   {:title "9"
    :books [23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39]
    :current-book 23
    :current-chapter 1
    :read-index 0}
   {:title "10"
    :books [44]
    :current-book 44
    :current-chapter 1
    :read-index 0}])


;; LIST CALCULATIONS

(defn- book-chapter-numbers [book-id]
  (let [chapters (get-in books-by-id [book-id :chapters])]
    (->> (range 1 (inc chapters))
         (map (fn [chapter-number] [book-id chapter-number]))
         vec)))


(defn- list-chapters [book-ids]
  (->> book-ids
       (map book-chapter-numbers)
       (reduce into)))


(defn find-book-chapter-at-index
  "given a list of books, finds the book and chapter for the given index"
  [book-ids read-index]
  (let [chapter-lookup (list-chapters book-ids)
        total-books   (count chapter-lookup)
        lookup-index (mod read-index total-books)
        [book-id chapter] (nth chapter-lookup lookup-index)]
    {:book-id book-id
     :chapter chapter}))


(comment
  (find-book-chapter-at-index [40 41 42 42] 94)
  ,)


(defn- step-reading-list
  [f reading-list]
  (let [{:keys [books read-index]} reading-list
        next-index                 (f read-index)
        {:keys [book-id chapter]}  (find-book-chapter-at-index books next-index)]
    (assoc reading-list
           :current-book book-id
           :current-chapter chapter
           :read-index next-index)))


(defn increment-reading-list
  [reading-list]
  (step-reading-list inc reading-list))


(defn decrement-reading-list
  [reading-list]
  (step-reading-list dec reading-list))


(comment

  (increment-reading-list (first default-reading-lists))

  (decrement-reading-list (first default-reading-lists))

  ,)


;; EVENTS

(def chapter-read-evt-type-list "list")
(def chapter-read-evt-type-manual-entry "manual-entry")


(defn list-read-event [reading-list]
  (let [{:keys [current-book current-chapter read-index id]} reading-list]
    {:book-id     current-book
     :chapter     current-chapter
     :read-date   "TODO"
     :create-date "TODO"
     :type        chapter-read-evt-type-list
     :type-data   {:list-id id
                   :read-index read-index}}))


(comment
  (list-read-event (first default-reading-lists))
  ,)


(defn manual-entry-read-event [book-id chapter-id read-date]
  {:book-id     book-id
   :chapter     chapter-id
   :read-date   read-date
   :create-date "TODO"
   :type        chapter-read-evt-type-manual-entry
   :type-data   {}})