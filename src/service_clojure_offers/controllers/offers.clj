(ns service-clojure-offers.controllers.offers
  (:require [service-clojure-offers.datomic.offers :as d.offers]
            [service-clojure-offers.business.offers :as b.offers]))

(defn create-offer-v1!
  "Version that emulates the race condition error"
  [datomic offer]
  (let [customer-id (:customer-id offer)
        offers (d.offers/find-by-customer-id! (:db datomic) customer-id)
        offers+offer (conj offers offer)
        exceed? (b.offers/exceed-maximum-available? offers+offer)]
    (if exceed?
      "There is already the maximum of offers"
      (d.offers/upsert-one! (:conn datomic) offer))))

(defn create-offer-v2!
  "Version that uses the transaction function to avoid race condition error"
  [datomic offer]
  (d.offers/upsert-one-with-validation! (:conn datomic) offer))

(defn create-offer-v3!
  "Version that uses the db-after to avoid race condition error"
  [datomic offer]
  (let [conn (:conn datomic)
        result (d.offers/upsert-one! conn offer)
        offers (d.offers/find-by-customer-id! (:db-after @result) (:customer-id offer))
        exceed? (b.offers/exceed-maximum-available? offers)]
    (if exceed?
      (do (d.offers/delete-by-id! conn (:id offer))
          "There is already the maximum of offers")
      result)))
