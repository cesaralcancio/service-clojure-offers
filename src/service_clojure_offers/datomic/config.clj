(ns service-clojure-offers.datomic.config
  (:require [datomic.api :as d]))

(def base-uri "datomic:dev://localhost:4334/")
(def db-name-offers "offers")

(defn list-databases! [uri]
  "List all databases according to the base uri"
  (d/get-database-names (str uri "*")))

(defn create-database! [uri db-name]
  "Create the database"
  (d/create-database (str uri db-name)))

(defn delete-database! [uri db-name]
  "Delete the database"
  (d/delete-database (str uri db-name)))

(defn connect!
  "Connect to the database and returns the connection"
  [uri db-name]
  (d/connect (str uri db-name)))

(defn db!
  "Creates the database snapshot to perform consults"
  [conn]
  (d/db conn))

(defn create-schema!
  "Transact the schema of the database"
  [conn schema]
  (d/transact conn schema))
