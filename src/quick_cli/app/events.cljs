(ns quick-cli.app.events
  (:require
   [re-frame.core :as re-frame :refer [reg-event-db]] ;; Re-Frame
  ;;  [quick-cli.app.subs :as subs]
   [quick-cli.app.db :as db]))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;; Events that purely update the database
(reg-event-db :set-page-text
              (fn [db [_ text]] ;; [old-db-value event-vector] -> new-db-value
                (assoc db :page-text text)))

(reg-event-db :set-page-id
              (fn [db [_ id]]
                (assoc db :page-id id)))

(reg-event-db :set-sort-order
              (fn [db [_ v]]
                (assoc db :sort-order v)))

(reg-event-db :toggle-editing?
              (fn [db [_ editing?]]
                (assoc db :editing? editing?)))

(reg-event-db :add-page-to-db
              (fn [db [_ [id text ts]]]
                (update-in db [:pages] assoc ts {:id id :text text :ts ts})))

(reg-event-db :add-user-to-db
              (fn [db [_ [id name email]]]
                (update-in db [:users] assoc id {:id id :name name :email email})))

(reg-event-db :set-strike-value
              (fn [db [_ [id checked]]]
                (update-in db [:strike-through-pages] assoc id checked)))

(reg-event-db :clear-pages
              (fn [db [_ _]]
                (assoc db :pages (sorted-map))))

;; game

(reg-event-db :set-joined-game
              (fn [db [_ joined?]]
                (assoc db :joined-game? joined?)))

(reg-event-db :reset-my-hand
              (fn [db [_ _]]
                (assoc db :my-hand (sorted-map))))

(reg-event-db :reset-my-selection
              (fn [db [_ _]]
                (assoc db :my-selection (sorted-map))))

(reg-event-db :card-into-hand
              (fn [db [_ c]]
                (let [c_name (c :name)]
                  (update-in db [:my-hand] assoc c_name c))))

(reg-event-db :set-card-table-id
              (fn [db [_ card-table-id]]
                (assoc db :card-table-id card-table-id)))

(reg-event-db :set-player-name
              (fn [db [_ player-name]]
                (assoc db :player-name player-name)))

(reg-event-db :load-rules
              (fn [db [_ new-rules]]
                (assoc db :rules new-rules)))

(reg-event-db :load-draw-pile
              (fn [db [_ cards]]
                (assoc db :draw-pile cards)))

(reg-event-db :load-discard-pile
              (fn [db [_ cards]]
                (assoc db :discard-pile cards)))

(reg-event-db :reset-goal
              (fn [db [_ goal-card]]
                (assoc db :goal goal-card)))

(reg-event-db :load-players
              (fn [db [_ players]]
                (assoc db :players players)))

(reg-event-db :load-deck
              (fn [db [_ deck-of-cards]]
                (assoc db :deck deck-of-cards)))

