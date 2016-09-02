(ns clojure-analytics.app.main
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]])) ; httpkit is a server

(defroutes app
  (GET "/" [] "Hello World"))

(defn -main []
  (run-server app {:port 5000}))
