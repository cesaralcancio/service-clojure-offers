(ns service-clojure-offers.common.utils
  (:import (java.util Date UUID)))

(defn now
  [] (new Date))

(defn uuid
  [] (UUID/randomUUID))

(defmacro run-threads
  [t f]
  `(dotimes [i# ~t]
     (.start
       (Thread. (fn [] ~f)))))

(defn dummy-offer
  [customer-id]
  {:id          (uuid)
   :customer-id customer-id
   :status      :available
   :amount      10M
   :created-at  (now)})
