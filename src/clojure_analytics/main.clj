(ns clojure-analytics.main
  (:gen-class)
  (:require [clojure-analytics.core :as core]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]))

(def cli-options
  [ ["-l" "--local LAT,LON" "Latitude,Longitude separadas por \",\""
        :default []
        :parse-fn #(string/split % #",")]

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

(defn -main
  [& args]
  (let
    [ {:keys [options arguments errors summary]}
      (cli/parse-opts args cli-options)
      lat-lon
      (:local options)
      local-ip
      (if (empty? lat-lon)
        (core/consultar-local)
        nil)
      cidade
      (if (nil? local-ip)
        (core/cidade-lat-lon (core/consultar-lat-lon lat-lon))
        (:city local-ip))
      lat-lon
      (if (nil? local-ip)
        (string/join "," lat-lon)
        (:loc local-ip))
      f-tempo-relevante (future
                          (->>
                            (core/consultar-tempo lat-lon)
                            (informacoes-relevantes)))
      f-desc (future
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
