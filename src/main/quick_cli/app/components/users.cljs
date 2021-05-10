(ns quick-cli.app.components.users
  (:require [quick-cli.app.state :refer [state]]))

(defn user-card [u]
  [:li.list-group-item {:key (u :id)}
   [:div.card
    [:div.card-body
     [:h5.card-title (u :name)]
     [:h6.card-subtitle (u :email)]]]])

(defn user-list [users]
  (for [u (sort-by :name > users)]
    (user-card u)))


(defn users []
  (let [state (vals @state)]
    [:div.container
     [:ul.list-group
      (when state (user-list state))]]))
