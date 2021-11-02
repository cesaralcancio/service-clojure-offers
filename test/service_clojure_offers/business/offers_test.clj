(ns service-clojure-offers.business.offers-test
  (:require [clojure.test :refer :all]
            [service-clojure-offers.business.offers :as b.offers]
            [service-clojure-offers.common.utils :as u]))

(deftest exceed-maximum-available?-test
  (testing "When offers is empty, it must return false"
    (is (not (b.offers/exceed-maximum-available? []))))
  (testing "When there are 4 offers, it must return true"
    (let [customer-id (u/uuid)
          offer (u/dummy-offer customer-id)]
      (is (b.offers/exceed-maximum-available? [offer offer offer offer]))))
  (testing "When there are 4 offers but 1 is expired, it must return false"
    (let [customer-id (u/uuid)
          offer-expired (assoc (u/dummy-offer (u/uuid)) :status :expired)
          offer (u/dummy-offer customer-id)]
      (is (not (b.offers/exceed-maximum-available? [offer-expired offer offer offer]))))))
