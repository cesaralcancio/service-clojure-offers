(ns service-clojure-offers.business.offers)

(defn exceed-maximum-available?
  [offers]
  (let [offers-available (filter (fn [offer] (= :available (:status offer))) offers)
        count-offers-available (count offers-available)]
    (> count-offers-available 3)))
