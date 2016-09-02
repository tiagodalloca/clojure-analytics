(ns user
  (:require [clojure.tools.namespace.repl :as tnr]
            [proto-repl.saved-values]))

(defn start
  []
  (use 'clojure.tools.namespace.repl)
  (use 'clojure.repl)
  (use 'clojure-analytics.app.main))

(defn reset []
  (tnr/refresh :after 'user/start))
