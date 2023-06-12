;; re-organize test folder structure to match src/chaat

(ns chaat.core-test
  (:require [clojure.test :refer :all]
            [chaat.handler :as handler]
            [clj-time.local :as l]))

(defn time-stub
  []
  "2023-06-13T10:07:03.172Z")

(deftest health-check-test
  (testing "Test to make sure health-check returns 200 OK"
    (with-redefs [l/local-now time-stub]
      (is (= {:status 200
              :headers {}
              :body (str "Service is running: " (l/local-now))}
             (handler/health-check {}))))))
