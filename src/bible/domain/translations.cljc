(ns bible.domain.translations)

(def translations-config
  ["AMP" "Amplified Bible" "en" 1588
   "AMPC" "Amplified Bible, Classic Edition" "en" 8
   "ASV" "American Standard Version" "en" 12
   "BOOKS" "The Books of the Bible NT" "en" 31
   "BSB" "English: Berean Standard Bible" "en" 3034
   "CEB" "Common English Bible" "en" 37
   "CEV" "Contemporary English Version" "en" 392
   "CEVDCI" "Contemporary English Version Interconfessional Edition" "en" 303
   "CEVUK" "Contemporary English Version (Anglicised) 2012" "en" 294
   "CJB" "Complete Jewish Bible" "en" 1275
   "CSB" "Christian Standard Bible" "en" 1713
   "DARBY" "Darby's Translation 1890" "en" 478
   "DRC1752" "Douay-Rheims Challoner Revision 1752" "en" 55
   "EASY" "EasyEnglish Bible 2018" "en" 2079
   "ERV" "Holy Bible: Easy-to-Read Version" "en" 406
   "ESV" "English Standard Version 2016" "en" 59
   "FBV" "Free Bible Version" "en" 1932
   "FNVNT" "First Nations Version" "en" 3633
   "GNBDC" "Good News Bible (British) with DC section 2017" "en" 416
   "GNBUK" "Good News Bible (British Version) 2017" "en" 296
   "GNT" "Good News Translation" "en" 68
   "GNTD" "Good News Translation (US Version)" "en" 69
   "GNV" "Geneva Bible" "en" 2163
   "GW" "GOD'S WORD" "en" 70
   "GWC" "St Paul from the Trenches 1916" "en" 1047
   "HCSB" "Holman Christian Standard Bible" "en" 72
   "ICB" "International Children’s Bible" "en" 1359
   "JUB" "Jubilee Bible" "en" 1077
   "KJV" "King James Version" "en" 1
   "KJVAAE" "King James Version with Apocrypha, American Edition" "en" 546
   "KJVAE" "King James Version, American Edition" "en" 547
   "LEB" "Lexham English Bible" "en" 90
   "LSB" "Legacy Standard Bible" "en" 3345
   "MEV" "Modern English Version" "en" 1171
   "MP1650" "Psalms of David in Metre 1650 (Scottish Psalter)" "en" 1365
   "MP1781" "Scottish Metrical Paraphrases 1781" "en" 3051
   "NABRE" "New American Bible, revised edition" "en" 463
   "NASB1995" "New American Standard Bible - NASB 1995" "en" 100
   "NASB2020" "New American Standard Bible - NASB" "en" 2692
   "NCV" "New Century Version" "en" 105
   "NET" "New English Translation" "en" 107
   "NIRV" "New International Reader’s Version" "en" 110
   "NIV" "New International Version" "en" 111
   "NIVUK" "New International Version (Anglicised)" "en" 113
   "NKJV" "New King James Version" "en" 114
   "NLT" "New Living Translation" "en" 116
   "NMV" "New Messianic Version Bible" "en" 2135
   "NRSV" "New Revised Standard Version" "en" 2016
   "NRSVUE" "New Revised Standard Version Updated Edition 2021" "en" 3523
   "PEV" "Plain English Version" "en" 2530
   "RAD" "Radiate New Testament" "en" 2753
   "RSV" "Revised Standard Version" "en" 2020
   "RSV-C" "Revised Standard Version Old Tradition 1952" "en" 2017
   "RSVCI" "Revised Standard Version" "en" 3548
   "RV1885" "Revised Version 1885" "en" 477
   "RV1895" "Revised Version with Apocrypha 1885, 1895" "en" 1922
   "TCENT" "The Text-Critical English New Testament" "en" 3427
   "TLV" "Tree of Life Version" "en" 314
   "TOJB2011" "The Orthodox Jewish Bible" "en" 130
   "TS2009" "The Scriptures 2009" "en" 316
   "WBMS" "Wycliffe's Bible with Modern Spelling" "en" 2407
   "WEBBE" "World English Bible British Edition" "en" 1204
   "WEBUS" "World English Bible, American English Edition, without Strong's Numbers" "en" 206
   "WMB" "World Messianic Bible" "en" 1209
   "WMBBE" "World Messianic Bible British Edition" "en" 1207
   "YLT98" "Young's Literal Translation 1898" "en" 821
   "ABA" "Bybel vir almal" "af" 2
   "AFR20" "Die Bybel 2020-vertaling" "af" 3499
   "AFR53" "Afrikaans 1933/1953" "af" 5
   "AFR83" "Afrikaans 1983" "af" 6
   "CAB23" "Contemporary Afrikaans Bible 2023" "af" 3660
   "DB" "Die Boodskap" "af" 50
   "NLV" "Nuwe Lewende Vertaling" "af" 117]
  )

(def default-translation "NLT")

(def translation-config-keys [:id :title :lang :youversion-id])

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
