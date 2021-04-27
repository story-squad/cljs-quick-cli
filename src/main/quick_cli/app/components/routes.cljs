(ns quick-cli.app.components.routes
  (:require [reagent.core :as r])
  (:require ["react-router-dom"  :refer [Route] :rename {BrowserRouter Router}])
  (:require [quick-cli.app.components.home :refer [index]]
            [quick-cli.app.components.about :refer [about]]
            [quick-cli.app.components.users :refer [users]]
            [quick-cli.app.components.navbar :refer [navbar]]))

;; react-router looks for component classes, return a "reactify'd" component 
(defn reactive-component
  [name]
  ({"index" (r/reactify-component index)
    "about" (r/reactify-component about)
    "users" (r/reactify-component users)} name))


(defn routes []
  [:section {:class "app-section"}
    [:div {:class "app"}
      [navbar]
      [:p "Story Squad is a game where imagination comes to play. It’s where generating ideas scores big."]
      [:p "Story Squad springs storytellers into action by partnering them up to participate in interactive & immersive creative challenges."]
      [:p "Become a master of your craft by submitting original drawings & handwritten stories, receiving and giving real feedback, sharing points in a squad-vs-squad matchup, and finally see if you won."]]]
     [:> Router
      [:> Route {:path "/about" :component (reactive-component "about")}]
      [:> Route {:path "/users" :component (reactive-component "users")}]
      [:> Route {:path "/" :exact true :component (reactive-component "index")}]])

