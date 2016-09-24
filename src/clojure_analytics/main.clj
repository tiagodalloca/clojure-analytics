(ns clojure-analytics.main
  (:gen-class)
  (:require [clojure-analytics.core :as core]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.spec :as s]))


(def cli-options
  [ ["-l" "--local LAT,LON" "Latitude,Longitude separadas por \",\""
        :default []
        :parse-fn #(string/split % #",")]
    ["-c" "--city CITY_NAME" "O nome da cidade em inglês"
        :default nil]
    ["-h" "--help" "Prints help"]])

(defn informacoes-relevantes
  [weather]
  { "Mínima" (str (:temp_min (:main weather)) "ºC")
    "Máxima" (str (:temp_max (:main weather)) "ºC")
    "Temperatura atual" (str (:temp (:main weather)) "ºC")
    "Humidade" (str (:humidity (:main weather) ) "%")
    "Descrição" (string/capitalize (:description (first (:weather weather))))})

(defn formatar
  [mapao]
  (reduce
    (fn [acc [k v]]
      (str acc k ": " v "\n"))
    "" mapao))

(defn print-informacoes
  [& {:keys [cidade lat-lon]}]
  (let
    [ f-tempo-relevante
        (future
          (let [consulta-do-tempo
                (if (nil? lat-lon)
                  (core/consultar-tempo :cidade cidade)
                  (core/consultar-tempo :lat-lon lat-lon))]
            (informacoes-relevantes consulta-do-tempo)))
      f-desc
      (future
        (when-not (nil? cidade)
          (->>
            (core/consultar-wiki cidade)
            (array-map "Mais informações"))))
      cidade
      (if (nil? cidade)
        "(não identificada)"
        cidade)]
    (->
      (conj @f-tempo-relevante {"Cidade" cidade} @f-desc)
      (formatar)
      (println))))

(defn -main
  [& args]
  (let
    [ {:keys [options arguments errors summary]}
      (parse-opts args cli-options)
      cidade
      (:city options)
      lat-lon
      (:local options)]
    (if (nil? cidade)
      (let
        [ local-ip
          (if (empty? lat-lon)
            (core/consultar-local)
            nil)
          lat-lon
          (if (nil? local-ip)
            lat-lon
            (string/split (:loc local-ip) #","))
          cidade
            (->
              (core/consultar-lat-lon lat-lon)
              (core/cidade-lat-lon))]
        (print-informacoes :lat-lon lat-lon :cidade cidade))
      (if (empty? lat-lon)
        (print-informacoes :cidade cidade)
        (print-informacoes :lat-lon lat-lon :cidade cidade)))))

(print-informacoes :cidade "New York")
