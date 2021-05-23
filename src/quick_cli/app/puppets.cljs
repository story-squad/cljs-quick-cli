(ns quick-cli.app.puppets)
(def puppeteer (js/require "puppeteer"))
(set! *warn-on-infer* false)

(defn launch-puppeteer []
  (-> (.launch puppeteer)
      (.then (fn [browser]
               (-> (.newPage browser)
                   (.then (fn [page]
                            (-> (.goto page "https://clojure.org")
                                (.then #(.screenshot page #js{:path "screenshot.png"}))
                                (.catch #(js/console.log %))
                                (.then #(.close browser))))))))))
