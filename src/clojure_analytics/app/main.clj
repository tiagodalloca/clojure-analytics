(ns clojure-analytics.app.main
  (require  [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [clojure-analytics.app.view :refer :all]))

(defroutes main-routes
  (GET "/" [] (alt-index "Clojure Analytics & Threads"))
  (GET "/:expressao" [expressao] (alt-index expressao)))

(def app
  (-> (handler/site main-routes)))

(defonce server
  (jetty/run-jetty #'app {:port 8080 :join? false}))
