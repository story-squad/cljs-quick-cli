(ns quick-cli.app.db)

(def default-db
  {:name "quick-cli" ;; application name
   
   ;; md-mini-blog
   :users (sorted-map) ;; indexed by uuid
   :page-text "" ;; markdown
   :page-id "" ;; uuid
   :pages (sorted-map) ;; indexed by timestamp
   :editing? false ;; true or false
   :sort-order "+" ;; "+" or "-"
   :strike-through-pages (hash-map) ;; index
   
   ;; global game-state
   :card-table-id "" ;; identity of shared game-state
   :rules [] ;; rules of the game.
   :draw-pile [] ;; probably won't be returning this to the client
   :discard-pile [] ;; cards out of play. 
   :goal [] ;; the current goal or goals; rules-permitting
   :players {} ;; players at the table
   :deck [] ;; all the cards in this deck

   ;; local game-state
   :keepers [] ;; when you play a keeper, place it here
   :goals [] ;; when you play a goal, place it here   
   :player-name "" ;; unique player name
   :my-hand (sorted-map) ;; cards
   :my-selection (sorted-map)  ;; currently selected card(s)
   :joined-game? false ;; have we joined the game?
   })
