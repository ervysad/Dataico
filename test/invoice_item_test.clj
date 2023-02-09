(ns invoice-item_test)
(use 'clojure.test)


(defn- discount-factor [{:invoice-item/keys [discount-rate]
                         :or                {discount-rate 0}}]
  (- 1 (/ discount-rate 100.0)))

(defn subtotal
  [{:invoice-item/keys [precise-quantity precise-price discount-rate]
    :as                item
    :or                {discount-rate 0}}]
  (* precise-price precise-quantity (discount-factor item)))

;Solution3
;new function to define a test Item with its quantity, price and discount rate
(defn Item [precise-quantity precise-price discount-rate]
  {:invoice-item/precise-quantity precise-quantity
   :invoice-item/precise-price    precise-price
   :invoice-item/discount-rate    discount-rate})


;function to calculate the final price applying discounts
(defn final-price [precise-quantity precise-price discount-rate]
  (double (* precise-quantity precise-price (- 1 (/ discount-rate 100.0)))))




(deftest no-discount-price
  ;Test item definition with its values and discount.
  (let [item1 (Item 3 19.99 0)
        item2 (Item 56 15 0)
        item3 (Item 999 1.99 0)

        expected-price1 (final-price 3 19.99 0)
        expected-price2 (final-price 56 15 0)
        expected-price3 (final-price 999 1.99 0)]
    ;Apply subtotal to all the items and see if it is the same expected-price on each.
    (is (= expected-price1 (subtotal item1)))
    (is (= expected-price2 (subtotal item2)))
    (is (= expected-price3 (subtotal item3)))
    ))

(deftest discount-price
  ;Test item definition with its values and discount.
  (let [item1 (Item 3 19.99 30)
        item2 (Item 50 7.99 50)
        item3 (Item 1 99.99 99)
        expected-price1 (final-price 3 19.99 30)
        expected-price2 (final-price 50 7.99 50)
        expected-price3 (final-price 1 99.99 99)]
    ;Apply subtotal to item1, item2 and item3; and see if it is the same expected-price.
    (is (= expected-price1 (subtotal item1)))

    (is (= expected-price2 (subtotal item2)))

    (is (= expected-price3 (subtotal item3)))
    ))

(deftest multi-discount-and-price-same
  ;Test items definition with its values and discount all the same.
  (let [item1 (Item 3 19.99 30)
        item2 item1
        item3 item2
        expected-price (final-price 3 19.99 30)
        ]
    ;Apply subtotal to all items and see if the expected-prices are the same.
    (is (= expected-price (subtotal item1) (subtotal item2) (subtotal item3))
        )))

(deftest multi-discount-and-price-diff
  ;Test items definition with its values and discount all different discounts.
  (let [item1 (Item 3 19.99 30)
        item2 (Item 15 79.99 10)
        item3 (Item 8 0.99 0)
        expected-price1 (final-price 3 19.99 30)
        expected-price2 (final-price 15 79.99 10)
        expected-price3 (final-price 8 0.99 0)
        ]
    ;Apply subtotal to all items and see if the expected-prices are the same.
    (is (= expected-price1 (subtotal item1) ))
    (is (= expected-price2 (subtotal item2) ))
    (is (= expected-price3 (subtotal item3) ))
    ))

(deftest items-full-discount
  ;Test items definition with its values and discount all different discounts.
  (let [item1 (Item 35 19.99 100)
        item2 (Item 0 79.99 100)
        item3 (Item 100 0.99 100)
        expected-price1 (final-price 35 19.99 100)
        expected-price2 (final-price 0 79.99 100)
        expected-price3 (final-price 100 0.99 100)
        ]
    ;Apply subtotal to all items and see if the expected-prices are the same even with amount 0.
    (is (= expected-price1 (subtotal item1) ))
    (is (= expected-price2 (subtotal item2) ))
    (is (= expected-price3 (subtotal item3) ))
    ))
