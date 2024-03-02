(ns bible.web.firebase.firestore
  (:require [re-frame.core :as rf]
            ["firebase/firestore" :as firestore]
            [bible.web.firebase.config :as firebase]))


(rf/reg-fx
  ::set-doc
  (fn [{:keys [doc data on-success on-error]}]
    (-> (firestore/setDoc doc (clj->js data))
        (.then (fn []
                 (when on-success
                   (rf/dispatch on-success))))
        (.catch (fn [err]
                  (js/console.error err)
                  (when on-error
                    (rf/dispatch (conj on-error err))))))))


(rf/reg-fx
  ::add-doc
  (fn [{:keys [collection data on-success on-error]}]
    (-> (firestore/addDoc collection (clj->js data))
        (.then (fn [doc-ref]
                 (let [id (.-id doc-ref)]
                   (when on-success
                     (rf/dispatch (conj on-success id))))))
        (.catch (fn [err]
                  (js/console.error err)
                  (when on-error
                    (rf/dispatch (conj on-error err))))))))


;; [[:set doc-ref data]
;;  [:update doc-ref data]
;;  [:delete doc-ref]]
(rf/reg-fx
  ::write-batch
  (fn [{:keys [mutations on-success on-error]}]
    (let [batch (firestore/writeBatch firebase/db)]
      (doseq [[t doc-ref data] mutations]
        (cond
          (= :set t)
          (.set batch doc-ref (clj->js data))
          (= :update t)
          (.update batch doc-ref (clj->js data))
          (= :delete t)
          (.delete batch)))
      (-> (.commit batch)
          (.then (fn []
                   (when on-success
                     (rf/dispatch on-success))))
          (.catch (fn [err]
                    (js/console.error err)
                    (when on-error
                      (rf/dispatch (conj on-error err)))))))))


(defonce ^:private *on-snapshot-subs (atom {}))


(defn- extract-change-data [change]
  (js->clj (.data (.-doc change)) :keywordize-keys true))


(defn- extract-changes [snapshot]
  (reduce
    (fn [changes change]
      (let [change-type (.-type change)
            id          (.. change -doc -id)]
        (conj changes
              (cond
                (= "added" change-type)
                {:type :add :id id :data (extract-change-data change)}

                (= "modified" change-type)
                {:type :modify :id id :data (extract-change-data change)}

                (= "removed" change-type)
                {:type :remove :id id}))))
    []
    (.docChanges snapshot)))


(rf/reg-fx
  ::on-snapshot
  (fn [{:keys [query on-subscribe on-change]}]
    (let [unsub (firestore/onSnapshot
                  query
                  (fn [snapshot]
                    (when on-change
                      (rf/dispatch (conj on-change (extract-changes snapshot))))))
          unsub-id (str (.getTime (js/Date.)) (rand-int 1000000))]
      (swap! *on-snapshot-subs assoc unsub-id unsub)
      (when on-subscribe
        (rf/dispatch (conj on-subscribe unsub-id))))))


(rf/reg-fx
  ::unsub-all-snapshots
  (fn [_]
    (doseq [unsub (vals @*on-snapshot-subs)]
      (try
        (unsub)
        (catch :default e
          (js/console.error e))))))


(rf/reg-fx
  ::unsub-snapshots
  (fn [unsub-id]
    (when-let [unsub (get @*on-snapshot-subs unsub-id)]
      (try
        (unsub)
        (catch :default e
          (js/console.error e))))))
