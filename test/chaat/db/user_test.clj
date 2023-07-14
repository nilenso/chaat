(ns chaat.db.user-test
  (:require [chaat.fixture :as fixture]
            [clojure.test :refer :all]
            [chaat.db.user :as db.user]
            [chaat.model.user :as model.user]))

(use-fixtures :each fixture/test-fixture)

;; no need to test this?
(deftest new-user?-test
  (testing ""))

;; no need to test this?
(deftest user-exists?-test
  (testing ""))

;; add tests for query results when testing db operations
;; solve uuid mismatch by dissoc'ing these keys from the map before comparing
;; to an expected-result map

(deftest insert-test
  (let [username "john"
        password "12345678"
        user-info (model.user/gen-new-user-map username password)
        datasource (:db fixture/test-system)]
    (testing "Insert new user into user table succeeds"
      (let [_ (db.user/insert datasource user-info)]
        (is (= true (db.user/user-exists? (datasource) "john")))))

    (testing "Insert existing user into user table fails"
      (let [actual-result (db.user/insert datasource user-info)
            expected-result {:result nil :error "Username already exists"}]
        (is (= expected-result actual-result))))))

(deftest delete-test
  (let [username "john"
        password "12345678"
        user-info (model.user/gen-new-user-map username password)
        datasource (:db fixture/test-system)
        _ (db.user/insert datasource user-info)]
    (testing "Remove existing user from user table"
      (let [expected-result {:result username :error nil}
            actual-result (db.user/delete datasource username)]
        (is (= expected-result actual-result))
        (is (= false (db.user/user-exists? (datasource) username)))))

    (testing "Remove non-existent user from user table"
      (let [expected-result {:result nil :error "Error deleting user"}
            actual-result (db.user/delete datasource username)]
        (is (= expected-result actual-result))))))
