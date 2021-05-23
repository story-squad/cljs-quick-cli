(ns quick-cli.app.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [quick-cli.app.events :as events]
            [quick-cli.app.views :as views]
            [quick-cli.app.config :as config])
  
  (:require [quick-cli.app.requests :refer [get-users get-pages]])
 
  )

(defn dev-setup []
  (get-users)
  (get-pages)
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [_root (.getElementById js/document "root")]
    (rdom/unmount-component-at-node _root)
    (rdom/render [views/main-page] _root)))

;; (defn render []
;;   (rdom/render [views/main-page] (.getElementById js/document "root")))

(defn ^:export main []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))

(defn ^:dev/after-load reload! []
  ;; todo: refactor to reframe
  (dev-setup)
  (mount-root))
