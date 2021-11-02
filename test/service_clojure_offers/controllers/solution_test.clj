(ns service-clojure-offers.controllers.solution-test
  (:require [clojure.test :refer :all]
            [service-clojure-offers.datomic.config :as config]
            [service-clojure-offers.datomic.offers :as d.offers]
            [service-clojure-offers.controllers.offers :as c.offers]
            [service-clojure-offers.common.utils :as utils]
            [service-clojure-offers.controllers.aux.prepare :as prepare]))

(use-fixtures :once prepare/database!)

(deftest create-offer-v2-test
  (let [customer-id (utils/uuid)
        first-offer (utils/dummy-offer customer-id)
        conn (config/connect! config/base-uri config/db-name-offers)
        datomic {:conn conn :db (config/db! conn)}
        _ (c.offers/create-offer-v2! datomic first-offer)
        offers (d.offers/find-by-customer-id! (config/db! conn) customer-id)]
    (is (= (count offers) 1))))

(deftest validate-first-solution-test
  (let [customer-id (utils/uuid)
        conn (config/connect! config/base-uri config/db-name-offers)]
    (utils/run-threads
      100
      (let [offer (utils/dummy-offer customer-id)
            datomic {:conn conn :db (config/db! conn)}]
        (c.offers/create-offer-v2! datomic offer)))
    (Thread/sleep 1000)
    (is (= (count (d.offers/find-by-customer-id! (config/db! conn) customer-id)) 3))))

