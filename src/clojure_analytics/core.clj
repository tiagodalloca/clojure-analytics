(ns clojure-analytics.core
  (:require
    [clj-http.client :as client]
    [clojure.data.json :as json]))

(defn fazer-consulta
  [onde args]
  (let
    [json
      (->>
        (str onde "?" args)
        (str "http://api.fixer.io/")
        (client/get)
        (:body))]
    (json/read-str json :key-fn keyword)))

(defn fazer-consulta-no-futuro
  [& args]
  (future (apply fazer-consulta args)))

(defn notificar-quando-acabar
  [task msgInicio msgFim]
  (println (str msgInicio))
  (future
    (#(do
        (println (str msgFim))
        (println %))
      (time @task))))

; (->
;   (future
;       (fazer-consulta "latest" "symbols=BRL&base=USD")
;       (fazer-consulta "latest" "symbols=BRL&base=EUR")
;       (fazer-consulta "latest" "symbols=BRL&base=EUR")
;       (fazer-consulta "latest" "symbols=BRL&base=EUR")
;       (fazer-consulta "latest" "symbols=BRL&base=EUR"))
;   (notificar-usuario "Começando..." "Terminado"))
