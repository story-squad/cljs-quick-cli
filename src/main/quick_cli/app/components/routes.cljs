(ns quick-cli.app.components.routes
  (:require [reagent.core :as r])
  (:require [clojure.string])
  (:require ["react-router-dom" :refer (Route Link) :rename {BrowserRouter Router}])
  (:require [syn-antd.menu :as menu])
  (:require [syn-antd.layout :as layout])
  (:require [quick-cli.app.components.home :refer [index]]
            [quick-cli.app.components.about :refer [about]]
            [quick-cli.app.components.users :refer [users]]
            ;; [quick-cli.app.state :refer [path-atom]]
            [quick-cli.app.components.fluxx :refer [card-game]]))

;; react-router looks for component classes, return a "reactify'd" component 
(defn reactive-component
  [name]
  ({"index" (r/reactify-component index)
    "links" (r/reactify-component about)
    "people" (r/reactify-component users)
    "game" (r/reactify-component card-game)} name))

;; (defn determine-class [path]
;;   (if (clojure.string/starts-with? (. (. js/document -location) -pathname) path) "nav-link active" "nav-link"))

(defn routes []
    ;;  [:p "Story Squad is a game where imagination comes to play. It’s where generating ideas scores big."]
    ;;  [:p "Story Squad springs storytellers into action by partnering them up to participate in interactive & immersive creative challenges."]
    ;;  [:p "Become a master of your craft by submitting original drawings & handwritten stories, receiving and giving real feedback, sharing points in a squad-vs-squad matchup, and finally see if you won."]]]
     [:> Router
      [layout/layout
       [layout/layout-header {:class "my-header"}
        [menu/menu {:class "my-header-menu" :theme "dark" :mode "horizontal"}
         [menu/menu-item {:class "my-header-menu-item"}
          [:> Link {:to "/"} "Notes"]]
         [menu/menu-item {:class "my-header-menu-item"}
          [:> Link {:to "/links"} "Links"]]
         [menu/menu-item {:class "my-header-menu-item"}
          [:> Link {:to "/game"} "Game"]]
         [menu/menu-item {:class "my-header-menu-item"}
          [:> Link {:to "/people"} "People"]]]
        ]
       [layout/layout-content {:class "my-content"}
        [:div {:class "site-layout-content"}
         [:> Route {:path "/links" :component (reactive-component "links")}]
         [:> Route {:path "/game" :component (reactive-component "game")}]
         [:> Route {:path "/people" :component (reactive-component "people")}]
         [:> Route {:path "/" :exact true :component (reactive-component "index")}]]]
       [layout/layout-footer {:class "my-footer"} "StorySquad 2021"]]]
       )

