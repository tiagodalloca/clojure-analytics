(set-env!
  :source-paths #{"src"}
  :target #{"target"}
  :dependencies    '[ [org.clojure/clojure "1.8.0"]
                      [compojure "1.5.1"]
                      [ring "1.5.0"]
                      [enlive "1.1.6"]
                      [clj-http "3.2.0"]
                      [org.clojure/data.json "0.2.6"]
                      [org.clojure/tools.cli "0.3.5"]])

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

(deftask build
  "This is used for creating an optimized uberjar "
  []
  (comp
   (aot :all true)
   (uber :exclude #{#"(?i)^META-INF/[^/]*\.(MF|SF|RSA|DSA)$"
                    #"(?i)^META-INF\\[^/]*\.(MF|SF|RSA|DSA)$"
                    #"(?i)^META-INF/INDEX.LIST$"
                    #"(?i)^META-INF\\INDEX.LIST$"})
   (jar :main 'clojure-analytics.main)
   (target)))
