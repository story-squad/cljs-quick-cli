(ns quick-cli.app.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::users
 (fn [db]
   (:users db)))

(re-frame/reg-sub
 ::users-sorted-by-name
 (fn [_]
   (let [users (re-frame/subscribe [::users])]
     (sort-by :name > @users))))

(re-frame/reg-sub
 ::page-text
 (fn [db]
   (:page-text db)))

(re-frame/reg-sub
 ::page-id
 (fn [db]
   (:page-id db)))

(re-frame/reg-sub
 ::pages
 (fn [db _]
   (vals (:pages db))))

;; (re-frame/reg-sub
;;  ::page-by-id
;;  (fn [db uuid]
;;    (let [pages (re-frame/subscribe [::pages])]
;;      (filter #(= (% :id) uuid) @pages))))

(re-frame/reg-sub
 ::editing?
 (fn [db]
   (:editing? db)))

(re-frame/reg-sub
 ::sort-order
 (fn [db]
   (:sort-order db)))

(re-frame/reg-sub
 ::strike-through-pages
 (fn [db]
   (:strike-through-pages db)))

(re-frame/reg-sub
 ::sorted-pages
 (fn [_]
   (let [pages (re-frame/subscribe [::pages])
         sort-order (re-frame/subscribe [::sort-order])
         sort-function (if (= "+" @sort-order) #(< %1 %2) #(> %1 %2))]
     (sort-by
      #(get-in % [:ts])
      sort-function
      @pages))))

;; game state

(re-frame/reg-sub
 ::game-rules
 (fn [db]
   (:rules db)))

(re-frame/reg-sub
 ::game-draw-pile
 (fn [db]
   (:draw-pile db)))

(re-frame/reg-sub
 ::game-keepers
 (fn [db]
   (:keepers db)))

(re-frame/reg-sub
 ::game-goals
 (fn [db]
   (:goals db)))

(re-frame/reg-sub
 ::shared-game-state
 (fn [_]
   (let [rules (re-frame/subscribe [::game-rules])
         draw-pile (re-frame/subscribe [::game-draw-pile])
         keepers (re-frame/subscribe [::game-keepers])
         goals (re-frame/subscribe [::game-goals])]
     {:rules @rules
      :draw-pile @draw-pile
      :keepers @keepers
      :goals @goals})))

;; local game state

(re-frame/reg-sub
 ::game-player
 (fn [db]
   (:player-name db)))

(re-frame/reg-sub
 ::game-table
 (fn [db]
   (:card-table-id db)))

(re-frame/reg-sub
 ::game-hand
 (fn [db]
   (:my-hand db)))

(re-frame/reg-sub
 ::game-selected
 (fn [db]
   (:my-selection db)))

(re-frame/reg-sub
 ::game-joined?
 (fn [db]
   (:joined-game? db)))
