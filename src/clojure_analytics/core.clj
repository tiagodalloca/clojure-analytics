(ns clojure-analytics.core
  (:require
    [clj-http.client :as client]
    [clojure.data.json :as json]))

(defn- url-args
  [args]
  (let [asdf
        (reduce #(str % (subs (str (key %2)) 1) "=" (val %2) "&") "" args)]
    (subs asdf 0 (- (count asdf) 1))))

(defn consulta
  ( [onde]
    (->>
      (str onde)
      (client/get)
      (:body)))
  ( [onde args]
    (->>
      (str onde "?" (url-args args))
      (client/get)
      (:body))))

(defn consulta-json
  ( [onde]
    (->
      (consulta onde)
      (json/read-str :key-fn keyword)))
  ( [onde args]
    (->
      (consulta onde args)
      (json/read-str :key-fn keyword))))

(defn consulta-no-futuro
  [& args]
  (future (apply consulta args)))

(defn consultar-tempo
  [lat-lon]
  (let
    [ tempo
      (consulta-json
        "http://api.openweathermap.org/data/2.5/weather"
        { :lat (get lat-lon 0)
          :lon (get lat-lon 1)
          :lang "pt"
          :units "metric"
          :appid "effecbe8e48b82f1d0aed912553d1a75"})]
    tempo))

(defn consultar-tempo-aqui
  []
  (let
    [ local
      (consulta-json "http://ipinfo.io/json")
      lat-lon
      (clojure.string/split (:loc local) #",")
      tempo
      (consultar-tempo lat-lon)]
    tempo))

(defn notificar-quando-acabar
  [task msgInicio msgFim]
  (println (str msgInicio))
  (future
    (#(do
        (println (str msgFim))
        (println %))
      (time @task))))

; (let
;   [ local
;     (consulta-json "http://ipinfo.io/json")
;     tempo
;     (consulta-json
;       (str
;         "https://api.forecast.io/forecast/caa72c49303deca6b27c77d3feb9d27d/"
;         (:loc local)))]
;   tempo)

; (let
;   [ local
;     (consulta-json "http://ipinfo.io/json")
;     lat-lon
;     (clojure.string/split (:loc local) #",")
;     tempo
;     (consulta-json
;       "http://api.openweathermap.org/data/2.5/weather"
;       { :lat (get lat-lon 0)
;         :lon (get lat-lon 1)
;         :lang "pt"
;         :units "metric"
;         :appid "effecbe8e48b82f1d0aed912553d1a75"})]
;   tempo)
