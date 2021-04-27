(ns quick-cli.app.components.navbar
  (:require [clojure.string])
  (:require [quick-cli.app.state :refer [path-atom]])
  (:require ["react-router-dom"  :refer [Link]]))

(defn determine-class [path]
  (if (clojure.string/starts-with? (. (. js/document -location) -pathname) path) "nav-link active" "nav-link"))

(defn navbar []
  (let [current-path (@path-atom :active)]
    [:nav {:class "navbar is-transparent"
           :on-click (fn [] (swap! path-atom assoc :active (. (. js/document -location) -pathname)))}
     
     
     [:div {:class "navbar-brand"}
      
      [:a.navbar-item
       [:> Link {:to "/" :class (if (= current-path "/") "nav-link active" "nav-link")} [:img {:src "/shark.png" :alt "Shark" :height "28"}]]
      ]
      
      
      [:div.card-body {:class "navbar-burger" :data-target "navbarExampleTransparentExample"}
       [:span {:aria-hidden true}]
       [:span {:aria-hidden true}]
       [:span {:aria-hidden true}]]
            
      ]

     [:div {:id "navbarExampleTransparentExample" :class "navbar-menu"}
      
      [:div {:class "navbar-start"}
       
       
       [:a.navbar-item
        [:> Link {:to "/" :class (if (= current-path "/") "nav-link active" "nav-link")} "Home"]]
       [:div {:id "navbarExampleTransparentExample" :class "navbar-menu"}

        [:a.navbar-item
         [:> Link {:to "/pages" :class (determine-class "/pages")} "Pages"]]
        [:a.navbar-item
         [:> Link {:to "/users/" :class (determine-class "/users")} "Users"]]]
       
       ]
     
      
      [:div {:class "navbar-end"}
      [:a.navbar-item
       [:> Link {:to "/about" :class (determine-class "/about")} "About"]]]]]))
