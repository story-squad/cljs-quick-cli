(ns quick-cli.app.components.pages
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [markdown-to-hiccup.core :as m])
  (:require [quick-cli.app.state :refer [set-strike-value get-strike-value]])
  (:require [quick-cli.app.requests :refer [create-page get-pages]])
  (:require [clojure.string :as s])
  (:require [cljs.core.async :refer [<!]])
  (:require [quick-cli.app.state :refer [page-state]]
            [syn-antd.input :as input]
            [syn-antd.button :as button]
            [syn-antd.checkbox :as checkbox])
  (:require [clojure.string :as str]))

;; ---- utils ---- 
(defn strikethrough-md [text]
  (let [lines (str/split text #"\n")
        add-strikes (map (fn [item] (str "~~" item "~~")) lines)]
    (str/join "\n" add-strikes)))

(defn sanitize-input
  "removes the element tags from a string"
  [s]
  (s/replace (s/replace s "<" "") ">" ""))

(defn refresh-pages []
  (get-pages))

(defn render-markdown
  "return the component(s) after converting markdown to html"
  [page]
  (let [page-id (if (not-empty (page :id)) (page :id) "current")
        checked (get-strike-value page-id)
        _ (when-not (get-strike-value page-id) (set-strike-value page-id checked))

        output (-> (if checked (strikethrough-md (page :text)) (sanitize-input (page :text)))
                   (m/md->hiccup {:encode? false})
                   (m/component))]
    [:<>
     [:div.card-checkbox
      [checkbox/checkbox {:checked checked :on-change (fn [] (set-strike-value page-id (not checked)))}]]
     output]))

(defn save-page [checked]
  (go (let [id (@page-state :id) text (@page-state :text)]
        (when (== (count id) 0)
          (<! (create-page text))
          (refresh-pages)
          (let [ps {:id "" :text "" :pages (@page-state :pages) :checked checked}]
            (reset-vals! page-state ps)
            (swap! page-state update-in [:pages] assoc id ps))))))

;; ---- component ---- 
(defn pages []
  ^{:key (. (. js/document -location) -pathname)}
  [:div {:class "render-html"}

   [:div.rendered-from-markdown
    [render-markdown @page-state]]

   [:div.markdown-editor
    [button/button {:type "primary" :on-click (fn [] (save-page false))} "save"]
    [input/input-text-area
     {:class "page-edit"
      :value (:text @page-state)
      :on-change
      (fn [e]
        (swap! page-state assoc :text (-> e .-target .-value)))}]]])
