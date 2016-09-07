(ns user
  (:require [clojure.tools.namespace.repl :as tnr]
            [proto-repl.saved-values]
            [clojure.repl :as repl]
            [clojure-analytics.app.view :refer :all]
            [clojure-analytics.app.main :refer :all]))

(defn start
  []
  (require '[clojure.repl :as repl])
  (require '[clojure-analytics.app.view :refer :all])
  (require '[clojure-analytics.app.main :refer :all])
  (.start server))
  ; (.stop server)

(defn reset []
  (.stop server)
  (tnr/refresh :after 'user/start))
