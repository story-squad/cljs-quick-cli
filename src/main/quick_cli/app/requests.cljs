(ns quick-cli.app.requests
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]])
  (:require [quick-cli.app.state :refer [add-page-to-state add-user-to-state page-state]]))

(defonce API_BASE_URL "http://localhost:8000")

;; ---- HEADERS ----

(defonce CORS_ALLOW_ORIGIN_ALL {:with-credentials? false
                                :headers {"Access-Control-Allow-Origin" "*"}})

(defn wrap-headers [edn-params]
  (let [with {:with-credentials? false :headers {"Access-Control-Allow-Origin" "*"}}]
    (->> (assoc with :edn-params edn-params))))

;; ---- POST ----

;; (defn create-user
;;   [{:keys [name email]}]
;;   (go (let [url (str API_BASE_URL "/users")
;;             response (<! (http/post url (wrap-headers {:name name :email email})))]
;;         (doseq [[_ p] (response :body)]
;;           (add-user-to-state (p :id) (p :name) (p :email))))))

(defn create-page
  "post new page, await response, add id to page-state"
  [s]
  (go (let [url (str API_BASE_URL "/pages")
            response (<! (http/post url (wrap-headers {:page-data s})))
            body (response :body)
            text (body :text)
            id (body :id)]
        {:id id :text text})))

;; ---- PUT ----

;; (defn update-page
;;   [id text]
;;   (go (let [url (str API_BASE_URL "/pages/" id)
;;             response (<! (http/put url (wrap-headers {:text text})))]
;;         (doseq [[_ p] (response :body)]
;;           (println "UPDATE PAGE" p)))))

;; ---- GET ----

(defn get-pages
  [& _]
  (go (let [url (str API_BASE_URL "/pages")
            response (<! (http/get url CORS_ALLOW_ORIGIN_ALL))]
        (doseq [p (response :body)]
          (add-page-to-state (p :id) (p :text) (p :ts))))))

(defn get-users []
  (go (let [url (str API_BASE_URL "/users")
            response (<! (http/get url CORS_ALLOW_ORIGIN_ALL))]
        (doseq [p (response :body)]
          (add-user-to-state (p :id) (p :name) (p :email))))))

;; ---- DELETE ----
(defn delete-page [id]
  (go (let [url (str API_BASE_URL "/pages/" id)
            _ (<! (http/delete url CORS_ALLOW_ORIGIN_ALL))]
        (reset-vals! page-state {:id "" :text "" :pages (sorted-map) :editing (@page-state :editing)})
        (get-pages))))

