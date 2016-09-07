(ns clojure-analytics.app.view
  (require [net.cgrand.enlive-html :as html]))

(defmacro template-pronto
  [file-name vec & forms]
  (let [template `(html/template ~file-name ~vec ~@forms)]
    `(fn [& ~'args2]
      (->>
        (apply ~template ~'args2)
        (reduce str)))))

(def index
  (template-pronto "index.html" []))

(def alt-index
  (template-pronto "index.html" [h1]
    [:h1] (html/content h1)))
