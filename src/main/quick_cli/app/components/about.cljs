(ns quick-cli.app.components.about)

(defn about []
  [:div.container
   [:h2 "About"]
   [:ul.list-group
    [:li.list-group-item.text-light.bg-dark]
    [:li.list-group-item.text-light.bg-dark [:a {:href "https://clojurescript.org/" :target "new"} "ClojureScript"]]
    [:li.list-group-item.text-light.bg-dark [:a {:href "https://reagent-project.github.io/" :target "new"} "Reagent"]]
    [:li.list-group-item.text-light.bg-dark [:a {:href "https://shadow-cljs.github.io/docs/UsersGuide.html" :target "new"} "Shadow-CLJS"]]
    [:li.list-group-item.text-light.bg-dark [:a {:href "https://leiningen.org/" :target "new"} "Leiningen"]]
    [:li.list-group-item.text-light.bg-dark [:a {:href "https://cljs.info/cheatsheet/" :target "new"} "CLJS Cheatsheet"]]
    ]])
