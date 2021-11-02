(ns service-clojure-offers.business.offers)

(defn exceed-maximum-available?
  "Validates if the offers exceeds the maximum quantity that is defined as three
  It takes into consideration that the new offer is inside the list of offers
  It doesn't validate the customer id"
  [offers]
  (let [offers-available (filter (fn [offer] (= :available (:status offer))) offers)
        count-offers-available (count offers-available)]
    (> count-offers-available 3)))
