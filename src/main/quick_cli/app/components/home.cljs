(ns quick-cli.app.components.home
  (:require [cljs.core]
            [reagent.core :as r]
            [syn-antd.button :as button]
            [syn-antd.list :as list]
            [syn-antd.card :as card]
            [syn-antd.switch :as switch]
            )
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
  [:<>
   [switch/switch {:checked (@page-state :editing)
                   :on-click (fn []
                               (swap! page-state assoc :editing (not (@page-state :editing))))}]
   [:div {:class "content is-normal container"}
   [page_component]
   [list/list {:class "page-list"}
    (map (fn [p] [:li
                  {:key ((nth p 1) :id)}
                  [page-view (nth p 1)]]) (sort-by :ts  #(> %1 %2) (@page-state :pages)))]

      
   ]])
   
