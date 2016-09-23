(ns clojure-analytics.core
  (:require
    [clj-http.client :as client]
    [clojure.data.json :as json]
    [clojure.string :as string]
    [clojure.spec :as s]))

(s/def ::url-map-args (s/cat :map (s/map-of keyword? string?)))
(s/fdef clojure-analytics.core/url-args
  :args (s/cat :m ::url-map-args))
(defn url-args
  [m]
  (let [asdf
        (reduce #(str % (subs (str (key %2)) 1) "=" (val %2) "&") "" m)]
    (subs asdf 0 (- (count asdf) 1))))


(defn temNet?
  []
  true)

(defn consulta
  ( [onde]
    (binding
      [clj-http.core/*cookie-store*
        (clj-http.cookies/cookie-store)]
      (->
        (str onde)
        (client/get {:as :auto})
        (:body))))
  ( [onde args]
    (->
      (binding
        [clj-http.core/*cookie-store*
          (clj-http.cookies/cookie-store)]
        (->
          (str onde "?" (url-args args))
          (client/get {:as :auto})
          (:body))))))

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

(defn consultar-local
  []
  (consulta-json "http://ipinfo.io/json"))

(defn consultar-tempo
  [& {:keys [lat-lon cidade]}]
  (if (nil? lat-lon)
    (if (nil? cidade)
      nil
      (consulta-json
        "http://api.openweathermap.org/data/2.5/weather"
        { :q (str cidade)
          :lang "pt"
          :units "metric"
          :appid "effecbe8e48b82f1d0aed912553d1a75"}))
    (consulta-json
      "http://api.openweathermap.org/data/2.5/weather"
      { :lat (get lat-lon 0)
        :lon (get lat-lon 1)
        :lang "pt"
        :units "metric"
        :appid "effecbe8e48b82f1d0aed912553d1a75"})))

(defn consultar-lat-lon
  [lat-lon]
  (consulta-json
    "http://www.mapquestapi.com/geocoding/v1/reverse"
    { :location (string/join "," lat-lon)
      :key "elQO3jwiKxEGzcGkqD0hY5yMDzaRrCxd"}))

(defn cidade-lat-lon
  [lat-lon-response]
  (if (empty? (:results lat-lon-response))
    nil
    (->
      (:results lat-lon-response)
      (first)
      (:locations)
      (first)
      (:adminArea5))))

(defn consultar-tempo-aqui
  []
  (let
    [ local
      (consultar-local)
      lat-lon
      (string/split (:loc local) #",")
      tempo
      (consultar-tempo lat-lon)]
    tempo))

(defn consultar-wiki
  [topico]
  (let
    [
      args
      (->
        { :format "json"
          :action "query"
          :prop "extracts"
          :exintro ""
          :utf8 ""
          :explaintext ""
          :titles (str topico)}
        (url-args))
      feio
        (->
          (str
            "https://en.wikipedia.org/w/api.php?" args)
          (slurp)
          (string/split #"\"extract\":\"")
          (second))]
    (if (re-find #"(\\n)" feio)
      (first (string/split feio #"(\\n)"))
      (string/replace feio "\"}}}}" ""))))

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

; (:extract (:29868 (:pages (:query (consultar-wiki "São Paulo")))))
; (consultar-wiki "São Paulo")

; (let
;   [ args
;     (->
;       { :format "json"
;         :action "query"
;         :prop "extracts"
;         :exintro ""
;         :utf8 ""
;         :explaintext ""
;         :titles (str 'Campinas)}
;       (url-args))]
;   (->
;     (str
;       "https://en.wikipedia.org/w/api.php?" args)
;     (slurp)))
