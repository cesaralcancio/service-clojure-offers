(ns service-clojure-offers.datomic.config
  (:require [datomic.api :as d]))

(def base-uri "datomic:dev://localhost:4334/")
(def db-name-offers "offers")

(defn list-databases! [uri]
  (d/get-database-names (str uri "*")))

(defn create-database! [uri db-name]
  (d/create-database (str uri db-name)))

(defn delete-database! [uri db-name]
  (d/delete-database (str uri db-name)))

(defn connect!
  [uri db-name]
  (d/connect (str uri db-name)))

(defn db!
  [conn]
  (d/db conn))

(defn create-schema!
  [conn schema]
  (d/transact conn schema))
