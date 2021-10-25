(ns service-clojure-offers.datomic.force-error-offers
  (:require [clojure.test :refer :all]
            [service-clojure-offers.datomic.config :as config]
            [service-clojure-offers.datomic.offers :as d.offers]
            [service-clojure-offers.controllers.offers :as c.offers]
            [service-clojure-offers.common.utils :as utils]
            [clojure.pprint :as pp]))

; prepare database
(config/delete-database! config/base-uri config/db-name-offers)
(config/create-database! config/base-uri config/db-name-offers)
(def conn (config/connect! config/base-uri config/db-name-offers))
(config/list-databases! config/base-uri)

; process schema
(config/create-schema! conn d.offers/offer-schema)

; insert one
(def customer-id (utils/uuid))
(pp/pprint customer-id)
(def first-offer
  {:id          (utils/uuid)
   :customer-id customer-id
   :status      :available
   :amount      10M
   :created-at  (utils/now)})
(pp/pprint first-offer)

(def datomic {:conn conn :db (config/db! conn)})
(c.offers/create-offer-v2! datomic first-offer)
(def offers (d.offers/find-by-customer-id! (config/db! conn) customer-id))
(pp/pprint offers)

; delete all
(doseq [offer (d.offers/find-by-customer-id! (config/db! conn) customer-id)]
  (d.offers/delete-by-id! conn (:id offer)))

; insert many
(dotimes [i 100]
  (.start
    (Thread.
      (fn []
        (let [offer {:id          (utils/uuid)
                     :customer-id customer-id
                     :status      :available
                     :amount      10M
                     :created-at  (utils/now)}
              datomic (assoc datomic :db (config/db! conn))]
          (c.offers/create-offer-v1! datomic offer))))))
(count (d.offers/find-by-customer-id! (config/db! conn) customer-id))

; insert many with validation inside the datomic transactor
(dotimes [i 100]
  (.start
    (Thread.
      (fn []
        (let [offer {:id          (utils/uuid)
                     :customer-id customer-id
                     :status      :available
                     :amount      10M
                     :created-at  (utils/now)}]
          (c.offers/create-offer-v2! datomic offer))))))
(count (d.offers/find-by-customer-id! (config/db! conn) customer-id))

; insert many with validation using db-after
(dotimes [i 100]
  (.start
    (Thread.
      (fn []
        (let [offer {:id          (utils/uuid)
                     :customer-id customer-id
                     :status      :available
                     :amount      10M
                     :created-at  (utils/now)}]
          (c.offers/create-offer-v3! datomic offer))))))
(count (d.offers/find-by-customer-id! (config/db! conn) customer-id))
