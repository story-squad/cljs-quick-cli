(ns quick-cli.app.core
  (:require [reagent.dom :as rdom])
  (:require [quick-cli.app.requests :refer [get-users get-pages]])
  (:require [quick-cli.app.components.routes :refer [routes]]))

(defn render []
  (rdom/render [routes] (.getElementById js/document "root")))

(defn ^:export main []
  (get-users)
  (get-pages)
  (render))

(defn ^:dev/after-load reload! []
  (get-users)
  (get-pages)
  (render))
