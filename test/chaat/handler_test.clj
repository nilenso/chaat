;; re-organize test folder structure to match src/chaat

(ns chaat.core-test
  (:require [clojure.test :refer :all]
            [chaat.handler :as handler]
            [clj-time.local :as l]
            [java-time.api :as jt]))

(defn time-stub
  "Return a fixed timestamp"
  []
  "2023-06-13T10:07:03.172Z")

(deftest health-check-test
  (testing "Test to make sure health-check returns 200 OK"
    (with-redefs [jt/instant time-stub]
      (is (= {:status 200
              :headers {}
              :body (str "Service is running: " (jt/instant))}
             (handler/health-check {}))))))
