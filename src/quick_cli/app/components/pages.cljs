(ns quick-cli.app.components.pages
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame]
            [quick-cli.app.subs :as subs]
            [clojure.string :as str])
  (:require [markdown-to-hiccup.core :as m])
  (:require [quick-cli.app.state :refer [set-strike-value get-strike-value]])
  (:require [quick-cli.app.requests :refer [create-page get-pages]])
  (:require [cljs.core.async :refer [<!]])
  (:require [syn-antd.input :as input]
            [syn-antd.button :as button]
            [syn-antd.checkbox :as checkbox]))
;; ---- utils ---- 
(defn strikethrough-md [text]
  (let [lines (str/split text #"\n")
        add-strikes (map (fn [item] (str "~~" item "~~")) lines)]
    (str/join "\n" add-strikes)))

(defn sanitize-input
  "removes the element tags from a string"
  [s]
  (str/replace (str/replace s "<" "") ">" ""))

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

(defn save-page [_]
  (go (let [id (re-frame/subscribe [::subs/page-id])
            text (re-frame/subscribe [::subs/page-text])]
        (when (== (count @id) 0)
          (<! (create-page @text))
          (get-pages)
          (re-frame/dispatch [:set-page-text "" :set-page-id ""])))))

;; ---- component ---- 
(defn pages []
  (let [page-text (re-frame/subscribe [::subs/page-text])
        page-id (re-frame/subscribe [::subs/page-id])]
    ^{:key (. (. js/document -location) -pathname)}
    [:div {:class "render-html"}
     [:div.rendered-from-markdown
      [render-markdown {:text @page-text :id @page-id}]]
     [:div.markdown-editor
      [button/button {:type "primary" :on-click (fn [] (save-page false))} "save"]
      [input/input-text-area
       {:class "page-edit"
        :value @page-text
        :on-change
        (fn [e]
          (re-frame/dispatch [:set-page-text (-> e .-target .-value)]))}]]]))