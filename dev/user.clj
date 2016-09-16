(ns user
  (:require [clojure.tools.namespace.repl :as tnr]
            [proto-repl.saved-values]
            [clojure.repl :as repl]))

(defn start
  []
  ; (require '[clojure.repl :as repl])
  (require '[clojure.data.json :as json])
  (require '[clj-http.client :as client])
  (use 'clojure-analytics.core))

(defn reset []
  (tnr/refresh :after 'user/start))
