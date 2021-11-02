(ns service-clojure-offers.common.utils
  (:import (java.util Date UUID)))

(defn now
  "Returns the current date"
  [] (new Date))

(defn uuid
  "Returns a random UUID"
  [] (UUID/randomUUID))

(defmacro run-threads
  "Run the code defined in f,
  Several times defined in t in different threads"
  [t & f]
  `(dotimes [i# ~t]
     (.start
       (Thread. (fn [] ~@f)))))

(defn dummy-offer
  "Returns a dummy offer"
  [customer-id]
  {:id          (uuid)
   :customer-id customer-id
   :status      :available
   :amount      10M
   :created-at  (now)})
