(ns user
  (:require [clojure.tools.namespace.repl :as tnr]
            [proto-repl.saved-values]
            [clojure.repl :as repl]))

(defn start
  []
  ; (require '[clojure.repl :as repl])
  (in-ns 'user)
  (require '[clojure.data.json :as json])
  (require '[clj-http.client :as client])
  (use 'clojure-analytics.core)
  (use 'clojure-analytics.main))

(defn reset []
  (tnr/refresh)
  (start))
