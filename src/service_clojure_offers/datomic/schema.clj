(ns service-clojure-offers.datomic.schema)

(def offers
  [{:db/ident       :offer/id
    :db/unique      :db.unique/identity
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Offer ID"}
   {:db/ident       :offer/customer-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Customer ID"}
   {:db/ident       :offer/status
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "Offer Status"}
   {:db/ident       :offer/amount
    :db/valueType   :db.type/bigdec
    :db/cardinality :db.cardinality/one
    :db/doc         "Offer amount"}
   {:db/ident       :offer/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Offer created"}])
