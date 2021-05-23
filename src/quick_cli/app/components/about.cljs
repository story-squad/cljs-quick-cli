(ns quick-cli.app.components.about
  (:require [syn-antd.anchor :as anchor]))

(defn about []
  [:div {:class "container wide"}
   [anchor/anchor
    [anchor/anchor-link {:title "ClojureScript" :href "https://clojurescript.org/" :target "new"}]
    [anchor/anchor-link {:title "Reagent" :href "https://reagent-project.github.io/" :target "new"}]
    [anchor/anchor-link {:title "Re-frame" :href "https://day8.github.io/re-frame/" :target "new"}]
    [anchor/anchor-link {:title "Leiningen" :href "https://leiningen.org/" :target "new"}]
    [anchor/anchor-link {:title "CLJS Cheatsheet" :href "https://cljs.info/cheatsheet/" :target "new"}]]])
