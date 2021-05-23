(ns quick-cli.app.components.home
  (:require [cljs.core] ;; ClojureScript
            [reagent.core :as r] ;; ReAgent
            [re-frame.core :as re-frame] ;; Re-Frame
            [quick-cli.app.subs :as subs] ;; ^^
            [syn-antd.button :as button] ;; Ant Design
            [syn-antd.list :as list];; ^^
            [syn-antd.card :as card];; ^^
            [syn-antd.switch :as switch];; ^^
            [syn-antd.radio :as radio];; ^^
            [syn-antd.icons.delete-outlined :as delete-outlined];; ^^
            [quick-cli.app.requests :refer [delete-page get-pages]] ;; api requests
            [quick-cli.app.components.pages :refer [render-markdown pages]] ;; components
            ))

(defn remove-page [id]
  (delete-page id)
  (get-pages))

(defn page-view [page]
  (let [editing? (re-frame/subscribe [::subs/editing?])]
    (fn []
      [:div.md-to-html
       [card/card
        [:div.card-body.p-4
         (when @editing?
           [button/button {:on-click (fn [] (remove-page (page :id)))
                           :danger true
                           :type "primary"} "Delete"])
         [render-markdown page]]]])))

(defn page_component []
  (r/reactify-component pages))

(defn index []
  (let [editing? (re-frame/subscribe [::subs/editing?])
        sorted-pages (re-frame/subscribe [::subs/sorted-pages])]
    (fn []
      [:<>
       [delete-outlined/delete-outlined {:class "trash-icon"}]
       [switch/switch {:class "trash-switch"
                       :checked @editing?
                       :on-click #(re-frame/dispatch [:toggle-editing? (not @editing?)])} "ok"]
       [radio/radio-group
        [radio/radio-button {:value "+"
                             :on-click #(re-frame/dispatch [:set-sort-order "+"])} "asc"]
        [radio/radio-button {:value "-"
                             :on-click #(re-frame/dispatch [:set-sort-order "-"])} "desc"]]
       [:div {:class "content is-normal container"}
        [page_component]
        [list/list {:class "page-list"} (map (fn [p] [:li {:key (p :id)} [page-view p]]) @sorted-pages)]]])))
