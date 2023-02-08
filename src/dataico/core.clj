(ns dataico.core
  (:gen-class)
  )


;Solution1
(def invoice (clojure.edn/read-string (slurp "src/dataico/invoice.edn"))) ;reading the invoice.edn file

;;Solution 1
(defn -filtering
  [entry-invoice]
  (do (println entry-invoice)
      )
  )

(defn -main
  [& args]
  (-filtering invoice)
  )
