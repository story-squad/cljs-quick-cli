(ns quick-cli.app.views
  (:require
   [re-frame.core :as re-frame]
   [quick-cli.app.subs :as subs]
   [reagent.core :as r]
   [clojure.string]
   ["react-router-dom" :refer (Route Link) :rename {BrowserRouter Router}]
   [syn-antd.menu :as menu]
   [syn-antd.layout :as layout]
   [quick-cli.app.components.home :refer [index]]
   [quick-cli.app.components.about :refer [about]]
   [quick-cli.app.components.users :refer [users]]
   [quick-cli.app.components.fluxx :refer [card-game]]))

(defn reframe-test []
  (let [name (re-frame/subscribe [::subs/name])]
    (fn []
      [:h1 "Name: " @name])))

(defn main-page []
  (let [_ (re-frame/subscribe [::subs/name])]
    [:> Router
     [layout/layout
      [layout/layout-header {:class "my-header"}
       [menu/menu {:class "my-header-menu" :theme "dark" :mode "horizontal"}
        [menu/menu-item {:class "my-header-menu-item"}
         [:> Link {:to "/"} "Notes"]]
        [menu/menu-item {:class "my-header-menu-item"}
         [:> Link {:to "/links"} "Links"]]
        [menu/menu-item {:class "my-header-menu-item"}
         [:> Link {:to "/reframe"} "Reframe"]]
        [menu/menu-item {:class "my-header-menu-item"}
         [:> Link {:to "/game"} "Game"]]
        [menu/menu-item {:class "my-header-menu-item"}
         [:> Link {:to "/people"} "People"]]]]
      [layout/layout-content {:class "my-content"}
       [:div {:class "site-layout-content"}
        [:> Route {:path "/links" :component (r/reactify-component about)}]
        [:> Route {:path "/game" :component (r/reactify-component card-game)}]
        [:> Route {:path "/people" :component (r/reactify-component users)}]
        [:> Route {:path "/reframe" :component (r/reactify-component reframe-test)}]
        [:> Route {:path "/" :exact true :component  (r/reactify-component index)}]]]
      [layout/layout-footer {:class "my-footer"} "StorySquad 2021"]]]))
