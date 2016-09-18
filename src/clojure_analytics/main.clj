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
  { "Cidade" (:name weather)
    "País" (:country (:sys weather))
    "Mínima" (str (:temp_min (:main weather)) "ºC")
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
      {:keys [local]}
      options]
    ; (->>
    ;   (if-not (empty? local)
    ;     (core/consultar-tempo local)
    ;     (core/consultar-tempo-aqui))
    ;   (informacoes-relevantes)
    ;   (formatar)
    ;   (println))))
    (if-not (empty? local)
      (let
        [ local (core/consultar-local)
          f-tempo-relevante (future
                              (->>
                                (core/consultar-tempo (:loc local))
                                (informacoes-relevantes)))
          f-desc (future
                  (->>
                    (core/consultar-wiki (:city local))
                    ()))]))))
