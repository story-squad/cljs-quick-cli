(ns quick-cli.app.components.home
  (:require [cljs.core]
            [reagent.core :as r]
            [syn-antd.button :as button]
            [syn-antd.list :as list]
            [syn-antd.card :as card]
            [syn-antd.switch :as switch]
            [syn-antd.radio :as radio])
  (:require [syn-antd.icons.delete-outlined :as delete-outlined])
  (:require [quick-cli.app.state :refer [page-state get-strike-value]]
            [quick-cli.app.requests :refer [delete-page]]
            [quick-cli.app.components.pages :refer [render-markdown refresh-pages pages]]))

(defn remove-page [id]
  (delete-page id)
  (refresh-pages))

(defn page-view [page]
  [:div.md-to-html
   [card/card
    [:div.card-body.p-4
     (when (and (@page-state :editing) (get-strike-value (page :id)))
       [button/button {:on-click (fn [] (remove-page (page :id)))
                       :danger true
                       :type "primary"
                       } "Delete"])
     [render-markdown page]]]])
     

(defn page_component []
  (r/reactify-component pages))


(defn index []
  (let [sort-function (if (= "+" (str (@page-state :sort-order))) #(< %1 %2) #(> %1 %2))]
    [:<>
     [delete-outlined/delete-outlined {:class "trash-icon"}]
     [switch/switch {:class "trash-switch"
                     :checked (@page-state :editing)
                     :on-click (fn []
                                 (swap! page-state assoc :editing (not (@page-state :editing))))} "ok"]
     [radio/radio-group
      [radio/radio-button {:value "+"
                           :on-click (fn []
                                       (swap! page-state assoc :sort-order "+"))} "asc"]
      [radio/radio-button {:value "-"
                           :on-click (fn []
                                       (swap! page-state assoc :sort-order "-"))} "desc"]]
     [:div {:class "content is-normal container"}
      [page_component]
      [list/list {:class "page-list"}
       (map (fn [p] [:li
                     {:key ((nth p 1) :id)}
                     [page-view (nth p 1)]]) (sort-by #(get-in (val %) [:ts]) sort-function (@page-state :pages)))]]]))
   
