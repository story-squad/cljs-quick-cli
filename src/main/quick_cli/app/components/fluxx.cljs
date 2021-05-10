(ns quick-cli.app.components.fluxx
  (:require [reagent.core :as r])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]])
  (:require [quick-cli.app.requests :refer [API_BASE_URL CORS_ALLOW_ORIGIN_ALL wrap-headers]])
  (:require [syn-antd.card :as card]
            [syn-antd.button :as button]
            [syn-antd.typography :as typography]
            [syn-antd.input :as input]
            ;; [cljs.pprint :as pp]
            ))

;; ---- State ----

;; -- global --
(defonce fluxx-state (r/atom {:rules [] ;; rules of the game.
                              :draw-pile [] ;; probably won't be returning this to the client
                              :keepers [] ;; when you play a keeper, place it here
                              :goals []})) ;; when you play a goal, place it here
                              
(defonce card_table_id (r/atom {:text ""})) ;; identity of shared game-state
(defonce player_name (r/atom {:text ""})) ;; unique player name

;; (add-watch player_name :player_name
;;            (fn [key _atom _old-state new-state]
;;              (println "---" key "atom changed ---")
;;              (pp/pprint new-state)))

;; -- local --
(defonce my_hand (r/atom (sorted-map))) ;; holds my cards
(defonce my_selection (r/atom (sorted-map))) ;; currently selected card
(defonce joined_game (r/atom false)) ;; have we joined the game?

(defn load-new-game
  "load game-state into r/atom"
  [new-game]
  (when (> (count (new-game :rules)) 0)
    (reset! fluxx-state new-game)
    (reset! my_hand (sorted-map))
    (reset! my_selection (sorted-map))
    (reset! joined_game false)))

(defn into-hand
  [c]
  (go (let [c_name (c :name)
            c_type (c :type)
            c_detail (c :detail)]
        (when (> (count c_name) 0)
          (swap! my_hand assoc c_name {:name c_name :type c_type :detail c_detail})))))


;; ---- Request ----
(defn new-game
  "reset/load game with ID"
  [& id]
  (go (let [_ (when id (reset! card_table_id {:text id}))
            url (str API_BASE_URL "/game/" (@card_table_id :text) "/new-game")
            response (<! (http/get url CORS_ALLOW_ORIGIN_ALL))]
        (load-new-game (response :body)))))

(defn draw-cards
  "take cards from the draw-pile and place them in your hand"
  [n]
  (go (let [number_of_cards n
            id (@card_table_id :text)
            url (str API_BASE_URL "/game/" id "/draw/" number_of_cards)
            response (<! (http/get url CORS_ALLOW_ORIGIN_ALL))
            new-cards (response :body)]
        (when (> (count new-cards) 0)
          (doseq [c new-cards] (into-hand c))))))

(defn join-game
  "register player with game"
  []
  (go (let [name (@player_name :text)
            id (@card_table_id :text)
            url (str API_BASE_URL "/game/" id "/join")
            response (<! (http/post url (wrap-headers {:name name})))]
        (load-new-game (response :body))
        (reset! joined_game true))))

;; (defn play-card-confirmed 
;;   "make that move"
;;   [card]
;;   (go (let [player])))

(defn select-a-card [c]
  (println "select " (c :name))
  (reset! my_selection (c :name)))

;; ---- React ----
(defn fluxx-card [c]
  (let [_name (first c)
        v (last c)
        is-selected? (= (v :name) @my_selection)
        class-name (str "CardFace " (v :type) (when is-selected? " is-selected"))]
    [card/card {:class class-name
                :key (v :name)
                :hoverable true
                :bordered true
                :on-click (fn [_] (select-a-card v))}
     [:div.CardContents
      [:div.CardSideWays
       (case (v :type)
         "keeper" [:span {:role "img" :aria-label "keeper"} "🗹"]
         "rule" [:span {:role "img" :aria-label "rule"} "✷"]
         "goal" [:span {:role "img" :aria-label "goal"} "⧉"]
         "action" [:span {:role "img" :aria-label "action"} "⤳"]
         "" [:span {:role "img" :aria-label "?"} "?"])
       (v :name)
       [:section {:class "hacker"} "👽"]]
      [:div.inner-card
       [:h5 (v :type)]
       [:hr {:width "100%"}]
       [:div.container
        (when (= "keeper" (v :type))
          [:span {:role "img" :aria-label (v :name)} [:h2 (v :detail)]])
        [:h4 (v :name)]]]]]))


(defn card-list [cards]
  (doall (for [c cards]    
    (fluxx-card c))))

(defn read-rules
  "reduce the list of rules"
  []
  (let [r (first (@fluxx-state :rules))]
    (if (not r) {:draw 1 :play 1} r)))

(defn play-keeper [name desc]
  (println "place " name " on the table in front of this player")
  (println desc))
(defn play-goal [name desc]
  (println "changes the goal to " name)
  (println desc))
(defn play-rule [name desc]
  (println "new rule! " name)
  (println desc))
(defn play-action [name desc]
  (println "action! " name)
  (println desc))

(defn play-card []
  (let [s-card (last (find @my_hand @my_selection))
        s-card-name (s-card :name)
        s-card-type (s-card :type)
        s-card-details (s-card :detail)
        ]
    (case s-card-type
      "keeper" (play-keeper s-card-name s-card-details)
      "goal" (play-goal s-card-name s-card-details)
      "rule" (play-rule s-card-name s-card-details)
      "action" (play-action s-card-name s-card-details)
      "" true)))

(defn card-game []
  (let [current_rules (read-rules)
        draw_rule (current_rules :draw)
        play_rule (current_rules :play)
        hand @my_hand
        selection @my_selection]
     ;; buttons
    [:div
     [:div {:class "controller-list"}
      [button/button {:size "small" :danger true
                      :on-click
                      (fn [] (println "reset game. please re-join.") (new-game))} "factory reset"]

      [typography/typography-paragraph "rules: draw " draw_rule " play " play_rule]
      [:div {:class "input-game-info"}
       [input/input {:size "large" :placeholder "player name"
                     :value (:text @player_name)
                     :disabled @joined_game
                     :on-change
                     (fn [e]
                       (swap! player_name assoc :text (-> e .-target .-value)))}]
       [input/input {:size "large" :placeholder "game id"
                     :value (:text @card_table_id)
                     :disabled @joined_game
                     :on-change
                     (fn [e]
                       (swap! card_table_id assoc :text (-> e .-target .-value)))}]
       [:div {:hidden @joined_game}
        [button/button {:on-click
                        (fn [] (join-game))} "join"]]]
      ;; button to register player, button to list players
      [:div {:hidden (not @joined_game)}
       [button/button {:on-click (fn [] (draw-cards draw_rule))} (str "draw " draw_rule)]]
      ]
     (when (> (count selection) 2)
       [button/button {:class "play-button"
                       :on-click (fn [] (play-card))} (str "play \"" selection "\"")])
     [:div {:class "container wide"}
      [:div {:class "card-table"}
      ;; cards 
       (when (> (count hand) 0) (card-list hand))]]]))
