(ns invoice-spec
  (:require
    [clojure.spec.alpha :as s]
    [clojure.data.json :as json]                            ;Add [org.clojure/data.json "0.2.6"] into the project.clj :dependencies.
    )
  (:import (java.text SimpleDateFormat)))

(use 'clojure.walk)

(s/def :customer/name string?)
(s/def :customer/email string?)
(s/def :invoice/customer (s/keys :req [:customer/name
                                       :customer/email]))

(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue-date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue-date
                :invoice/customer
                :invoice/items]))

;Solution2
;This function is to change the format of the dates from"dd/MM/yyyy" to string
(def date (SimpleDateFormat. "dd/MM/yyyy"))
(defn new-date [str-date]
  (try
    (.parse date str-date)
    (catch Exception e
      (println "Error: " e))))

;This function reads the Json and evaluate if a date change is necessary.
(defn json_reader [key value]
  (case key
    :tax_category (if (= value "IVA") :iva value)           ;If it is the IVA don't change the value.
    :issue_date (new-date value)                            ;parse the date
    :tax_rate (double value)                                ;parse the date
    :payment_date (new-date value)                          ;parse the date
    value)
  )

;With the dates in the right format we can format the json file and replace the keys
(defn json-new-formater [invoice]
  (postwalk-replace
    {:issue_date   :invoice/issue-date
     :customer     :invoice/customer
     :items        :invoice/items
     :company_name :customer/name
     :email        :customer/email
     :price        :invoice-item/price
     :quantity     :invoice-item/quantity
     :sku          :invoice-item/sku
     :taxes        :invoice-item/taxes
     :tax_category :tax/category
     :tax_rate     :tax/rate} invoice))


;using json-new-formater and json_reader we call the parse in the next function.
(defn parse-json-invoice [file]
  (let [json-invoice (json/read-str (slurp file) :key-fn keyword :value-fn json_reader)]
    (get (json-new-formater json-invoice) :invoice)
    ))


;Testing and printing the parse-json-invoice function
(s/valid? ::invoice (parse-json-invoice "invoice.json"))

;PRINT (We should get True)
(print (s/valid? ::invoice (parse-json-invoice "invoice.json")))