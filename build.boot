(set-env!
  :source-paths #{"src"}
  :target #{"target"}
  :dependencies    '[ [org.clojure/clojure "1.8.0"]
                      [compojure "1.1.8"]
                      [http-kit "2.1.16"]])

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
