(ns chaat.app-test
  (:require [chaat.fixture :as fixture]
            [clojure.test :refer :all]
            [clj-http.client :as client]))

(use-fixtures :each fixture/test-fixture)

;; end-to-end test
(deftest health-check-test
  (testing "get request to health check returns status 200"
    (let [address "http://0.0.0.0:3010/health-check"
          response (client/get address)]
      (is (= 200 (:status response))))))
