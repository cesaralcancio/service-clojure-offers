(ns service-clojure-offers.common.utils
  (:import (java.util Date UUID)))

(defn now
  [] (new Date))

(defn uuid
  [] (UUID/randomUUID))
