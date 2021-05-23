(ns quick-cli.app.components.users
  (:require
   [re-frame.core :as re-frame]
   [quick-cli.app.subs :as subs]))

(defn user-card [u]
  [:li.list-group-item {:key (u :id)}
   [:div.card
    [:div.card-body
     [:h5.card-title (u :name)]
     [:h6.card-subtitle (u :email)]]]])

(defn users []
  [:div.container
   [:ul.list-group
    (let  [users (re-frame/subscribe [::subs/users-sorted-by-name])]
      (for [u @users]
        (user-card (last u))))]])
