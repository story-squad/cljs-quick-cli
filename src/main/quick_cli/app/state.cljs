(ns quick-cli.app.state
  (:require   [reagent.ratom :as r]
              ;; [cljs.pprint :as pp]
              ))
;; ---- state ----

(defonce state (r/atom (sorted-map)))

(defonce page-state (r/atom {:text "" :id "" 
                             :pages (sorted-map) 
                             :editing false :sort-order "+"}))

(defonce strike-atom (r/atom {:pages (hash-map)}))

(defn get-strike-value [id]
  (let [tf ((@strike-atom :pages) id)]
    tf))

(defn set-strike-value [id checked]
  (swap! strike-atom update-in [:pages] assoc id checked))

;; (add-watch state :state
;;            (fn [key _atom _old-state new-state]
;;              (println "---" key "atom changed ---")
;;              (pp/pprint new-state)))

;; (add-watch page-state :pages
;;            (fn [key _atom _old-state new-state]
;;              (println "---" key "atom changed ---")
;;              (pp/pprint new-state)))

;; (add-watch path-atom :active
;;            (fn [key _atom _old-state new-state]
;;              (println "---" key "atom changed ---")
;;              (pp/pprint new-state)))

(defn add-user-to-state
  [id name email]
  (when (> (count email) 0)
    (swap! state assoc id {:id id :name name :email email})))

(defn add-page-to-state
  [id text ts]
  (when (> (count id) 0)
    (swap! page-state update-in [:pages] assoc ts {:id id :text text :ts ts})))

