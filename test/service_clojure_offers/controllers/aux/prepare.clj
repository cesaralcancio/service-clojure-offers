(ns service-clojure-offers.controllers.aux.prepare
  (:require [clojure.test :refer :all]
            [service-clojure-offers.datomic.config :as config]
            [service-clojure-offers.datomic.offers :as d.offers]))

(defn database! [f]
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
