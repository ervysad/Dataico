(ns dataico.core
  (:gen-class)
  )


;Solution1
(def invoice (clojure.edn/read-string (slurp "src/dataico/invoice.edn"))) ;reading the invoice.edn file

;;Solution 1

;this function filters and returns the items that have a tax rate of 19
(defn tax-filter-19 [invoice]
  (->> invoice
       :invoice/items
       (filter #(some (fn [%] (= 19 (:tax/rate %))) (get-in % [:taxable/taxes])))))



;this function filters and returns the items that have a retention rate of 1
(defn ret-filter-1 [invoice]
  (->> invoice
       :invoice/items
       (filter #(some (fn [%] (= 1 (:retention/rate %))) (get-in % [:retentionable/retentions])))))


;this function returns the items that have an iva tax rate of 19 or a retention rate of 1, but not both
(defn Sym-diff-tax-ret [invoice]
  (->> invoice
       :invoice/items
       (filter (fn [item]
                 (let [filter-tax (some #(and (= 19 (:tax/rate %)) (= :iva (:tax/category %))) (get-in item [:taxable/taxes])),
                       filter-ret (some #(and (= 1 (:retention/rate %)) (= :ret_fuente (:retention/category %))) (get-in item [:retentionable/retentions]))]
                   (and (or filter-tax filter-ret) (not (and filter-tax filter-ret))))))))






(defn -main
  [& args]
  (do
    (println "Items with a tax rate of 19: " (tax-filter-19 invoice))
    (println "Items with a retention rate of 1: " (ret-filter-1 invoice))
    (println "Items with a retention rate of 1 or tax of 19 but not both: " (Sym-diff-tax-ret invoice))
    )
  )
