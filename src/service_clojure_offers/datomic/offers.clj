(ns service-clojure-offers.datomic.offers
  (:require [datomic.api :as d]
            [service-clojure-offers.datomic.schema :as schema]))

(def add-offer
  {:db/ident :add-offer
   :db/fn    #db/fn {:lang   :clojure
                     :params [db offer]
                     :code   (let [customer-id (:customer-id offer)
                                   all (d/q '[:find ?id
                                              :keys id
                                              :in $ ?customer-id-in
                                              :where
                                              [?e :offer/id ?id]
                                              [?e :offer/customer-id ?customer-id-in]
                                              [?e :offer/status :available]]
                                            db customer-id)
                                   offers (conj all offer)]
                               (if (<= (count offers) 3)
                                 [{:offer/id          (:id offer)
                                   :offer/customer-id customer-id
                                   :offer/status      (:status offer)
                                   :offer/amount      (bigdec (:amount offer))
                                   :offer/created-at  (:created-at offer)}]
                                 (datomic.api/cancel {:cognitect.anomalies/category :cognitect.anomalies/incorrect
                                                      :cognitect.anomalies/message  "There is already the maximum of offers"})))}
   :db/doc   "Add offer with maximum validation"})

(def offer-schema
  (conj schema/offers add-offer))

(defn upsert-one-with-validation!
  "Update or insert one record using the transaction function"
  [conn offer]
  (d/transact conn [[:add-offer offer]]))

(defn upsert-one!
  "Update or insert one record"
  [conn {:keys [id customer-id status amount created-at]}]
  (d/transact conn
              [[:db/add "temporary-new-db-id" :offer/id id]
               [:db/add "temporary-new-db-id" :offer/customer-id customer-id]
               [:db/add "temporary-new-db-id" :offer/status status]
               [:db/add "temporary-new-db-id" :offer/amount (bigdec amount)]
               [:db/add "temporary-new-db-id" :offer/created-at created-at]]))

(defn find-all!
  "Find all offers"
  [db]
  (d/q '[:find ?id ?customer-id ?status ?amount ?created-at
         :keys id customer-id status amount created-at
         :where
         [?e :offer/id ?id]
         [?e :offer/customer-id ?customer-id]
         [?e :offer/status ?status]
         [?e :offer/amount ?amount]
         [?e :offer/created-at ?created-at]] db))

(defn find-by-customer-id!
  "Find all offers by customer id"
  [db customer-id]
  (d/q '[:find ?id ?customer-id-in ?status ?amount ?created-at
         :keys id customer-id status amount created-at
         :in $ ?customer-id-in
         :where
         [?e :offer/id ?id]
         [?e :offer/customer-id ?customer-id-in]
         [?e :offer/status ?status]
         [?e :offer/amount ?amount]
         [?e :offer/created-at ?created-at]] db customer-id))

(defn delete-by-id!
  "Delete offers by offer id"
  [conn id]
  (d/transact conn [[:db/retract [:offer/id id] :offer/id]
                    [:db/retract [:offer/id id] :offer/customer-id]
                    [:db/retract [:offer/id id] :offer/status]
                    [:db/retract [:offer/id id] :offer/amount]
                    [:db/retract [:offer/id id] :offer/created-at]]))
