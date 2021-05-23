(ns quick-cli.app.state
  (:require   [re-frame.core :as re-frame]
              [quick-cli.app.subs :as subs]))

(defn get-strike-value [id]
  (let [strike-through? (re-frame/subscribe [::subs/strike-through-pages])]
    (@strike-through? id)))

(defn set-strike-value [id checked]
  (re-frame/dispatch [:set-strike-value [id checked]]))

(defn add-user-to-state
  [id name email]
  (when (> (count email) 0)
    (re-frame/dispatch [:add-user-to-db [id name email]])))

(defn add-page-to-state
  [id text ts]
  (when (> (count id) 0)
    (re-frame/dispatch [:add-page-to-db [id text ts]])))
