(set-env!
  :source-paths #{"src"}
  :target #{"target"}
  :dependencies    '[ [org.clojure/clojure "1.8.0"]
                      [compojure "1.5.1"]
                      [ring "1.5.0"]
                      [enlive "1.1.6"]
                      [clj-http "3.2.0"]
                      [org.clojure/data.json "0.2.6"]])

(deftask dev
  "Profile setup for development."
  []
  (println "Dev running...")
  (set-env!
    :init-ns 'user
    :dependencies (conj (get-env :dependencies) '[org.clojure/tools.namespace "0.2.11"]
                                                '[proto-repl "0.3.1"])
    :source-paths #(into % ["dev"]))
  identity)
