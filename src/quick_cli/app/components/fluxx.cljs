(ns quick-cli.app.components.fluxx
  (:require [cljs.core] ;; ClojureScript
            [re-frame.core :as re-frame] ;; Re-Frame
            [quick-cli.app.subs :as subs]) ;; ^^ Subscriptions
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

(defn load-new-game
  "load game-state into r/atom"
  [new-game]
  (when (> (count (new-game :rules)) 0)
    (let [rules (new-game :rules)
          draw-pile (new-game :draw-pile)
          discard-pile (new-game :discard-pile)
          goal (new-game :goal)
          players (new-game :players)
          deck (new-game :deck)]
      (re-frame/dispatch [:load-rules rules])
      (re-frame/dispatch [:load-draw-pile draw-pile])
      (re-frame/dispatch [:load-discard-pile discard-pile])
      (re-frame/dispatch [:reset-goal goal])
      (re-frame/dispatch [:load-players players])
      (re-frame/dispatch [:load-deck deck])
      (re-frame/dispatch [:reset-my-hand])
      (re-frame/dispatch [:reset-my-selection])
      (re-frame/dispatch [:set-joined-game false]))))

(defn into-hand
  [c]
  (go (let [c_name (c :name)]
        (when (> (count c_name) 0)
          (re-frame/dispatch [:card-into-hand c])))))


;; ---- Request ----
(defn new-game
  "reset/load game with ID"
  [& id]
  (go (let [_ (when id (re-frame/dispatch [:set-card-table-id id]))
            ;; _ (when id (reset! card_table_id {:text id}))
            id (re-frame/subscribe [::subs/game-table])
            url (str API_BASE_URL "/game/" @id "/new-game")
            response (<! (http/get url CORS_ALLOW_ORIGIN_ALL))]
        (load-new-game (response :body)))))

(defn draw-cards
  "take cards from the draw-pile and place them in your hand"
  [n]
  (go (let [number_of_cards n
            id (re-frame/subscribe [::subs/game-table])
            url (str API_BASE_URL "/game/" @id "/draw/" number_of_cards)
            response (<! (http/get url CORS_ALLOW_ORIGIN_ALL))
            new-cards (response :body)]
        (when (> (count new-cards) 0)
          (doseq [c new-cards] (into-hand c))))))

(defn join-game
  "register player with game"
  []
  (go (let [player-name (re-frame/subscribe [::subs/game-player])
            id (re-frame/subscribe [::subs/game-table])
            url (str API_BASE_URL "/game/" @id "/join")
            response (<! (http/post url (wrap-headers {:name @player-name})))]
        (load-new-game (response :body))
        (re-frame/dispatch [:set-joined-game true]))))

(defn select-a-card [c]
  (println "select " (c :name)))

;; ---- React ----
(defn fluxx-card [c]
  (let [_name (first c)
        selection (re-frame/subscribe [::subs/game-selected])        
        v (last c)
        is-selected? (= (v :name) @selection)
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
  (let [rules (re-frame/subscribe [::subs/game-rules])
        r (first @rules)]
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
  (let [hand (re-frame/subscribe [::subs/game-hand])
        selection (re-frame/subscribe [::subs/game-selected])        
        s-card (last (find @hand @selection))
        s-card-name (s-card :name)
        s-card-type (s-card :type)
        s-card-details (s-card :detail)]
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
        player-name (re-frame/subscribe [::subs/game-player])
        card-table-id (re-frame/subscribe [::subs/game-table])
        hand (re-frame/subscribe [::subs/game-hand])
        selection (re-frame/subscribe [::subs/game-selected])
        joined-game? (re-frame/subscribe [::subs/game-joined?])]
     ;; buttons
    (fn []
      [:div
       [:div {:class "controller-list"}
        [button/button {:size "small" :danger true
                        :on-click
                        (fn [] (println "reset game. please re-join.") (new-game))} "factory reset"]

        [typography/typography-paragraph "rules: draw " draw_rule " play " play_rule]
        [:div {:class "input-game-info"}
         [input/input {:size "large" :placeholder "player name"
                       :value @player-name
                       :disabled @joined-game?
                       :on-change
                       (fn [e]
                         (re-frame/dispatch [:set-player-name (-> e .-target .-value)]))}]
         [input/input {:size "large" :placeholder "game id"
                       :value @card-table-id
                       :disabled @joined-game?
                       :on-change
                       (fn [e]
                         (re-frame/dispatch [:set-card-table-id (-> e .-target .-value)]))}]
         [:div {:hidden @joined-game?}
          [button/button {:on-click
                          (fn [] (join-game))} "join"]]]
      ;; button to register player, button to list players
        [:div {:hidden (not @joined-game?)}
         [button/button {:on-click (fn [] (draw-cards draw_rule))} (str "draw " draw_rule)]]]
       (when (> (count @selection) 2)
         [button/button {:class "play-button"
                         :on-click (fn [] (play-card))} (str "play \"" @selection "\"")])
       [:div {:class "container wide"}
        [:div {:class "card-table"}
      ;; cards 
         (when (> (count @hand) 0) (card-list @hand))]]])))
