(ns dev.deploy
  (:require [clojure.string :as string]))

(def index-main-module "main.js")
(def index-html-path "firebase/public/index.html")
(def manifest-path "firebase/public/js/manifest.edn")
(def index-main-module-script-regex #"<script src=\"\/js\/main\S*.js\"><\/script>")
(def index-main-dev-script "<script src=\"/js/main.js\"></script>")

(defn get-js-output-name
  {:dev/future "This assumes a single output module"}
  []
  (->> (slurp manifest-path)
       read-string
       first
       :output-name))


(defn replace-js-module-name
  {:dev/tech-debt "This name is not great"}
  []
  (let [output-name (get-js-output-name)
        html        (slurp index-html-path)
        new-html    (string/replace-first
                   html
                   index-main-module
                   output-name)]
    (spit index-html-path new-html)))


(defn reset-js-module-for-dev []
  (let [html (slurp index-html-path)
        new-html
        (string/replace-first html index-main-module-script-regex index-main-dev-script)]
    (spit index-html-path new-html)))


(defn app-post-build-release-hook
  {:shadow.build/stage :flush}
  [build-state & args]
  (prn "Updating js module path in index.html")
  (replace-js-module-name)
  build-state)


(defn reset-for-dev-hook
  {:shadow.build/stage :configure}
  [build-state & args]
  (prn "Reset js module path in index.html")
  (reset-js-module-for-dev)
  build-state)
