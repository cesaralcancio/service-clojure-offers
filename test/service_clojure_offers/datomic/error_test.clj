(ns service-clojure-offers.datomic.error-test
  (:require [clojure.test :refer :all]
            [service-clojure-offers.datomic.config :as config]
            [service-clojure-offers.datomic.offers :as d.offers]
            [service-clojure-offers.controllers.offers :as c.offers]
            [service-clojure-offers.common.utils :as utils]
            [clojure.pprint :as pp]))

(defn run-before-test [f]
  ; prepare database
  (config/delete-database! config/base-uri config/db-name-offers)
  (config/create-database! config/base-uri config/db-name-offers)
  (let [conn (config/connect! config/base-uri config/db-name-offers)]
    (config/list-databases! config/base-uri)
    ; process schema
    (config/create-schema! conn d.offers/offer-schema)
    ; delete all data
    (doseq [offer (d.offers/find-all! (config/db! conn))]
      (d.offers/delete-by-id! conn (:id offer))))
  ; run tests
  (f))

(use-fixtures :once run-before-test)

(deftest create-offer-v1-test
  (let [customer-id (utils/uuid)
        first-offer (utils/dummy-offer customer-id)
        conn (config/connect! config/base-uri config/db-name-offers)
        datomic {:conn conn :db (config/db! conn)}
        _ (c.offers/create-offer-v1! datomic first-offer)
        offers (d.offers/find-by-customer-id! (config/db! conn) customer-id)]
    (is (= (count offers) 1))))

(deftest validate-if-inserts-more-than-3-test
  (let [customer-id (utils/uuid)
        conn (config/connect! config/base-uri config/db-name-offers)]
    (utils/run-threads
      100
      (let [offer (utils/dummy-offer customer-id)
            datomic {:conn conn :db (config/db! conn)}]
        (c.offers/create-offer-v1! datomic offer)))
    (Thread/sleep 1000)
    (is (> (count (d.offers/find-by-customer-id! (config/db! conn) customer-id)) 3))))
