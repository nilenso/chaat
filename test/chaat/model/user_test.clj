(ns chaat.model.user-test
  (:require [clojure.test :refer :all]
            [chaat.db.user :as db.user]
            [chaat.model.user :as model.user]
            [chaat.fixture :as fixture]
            [java-time.api :as jt]
            [crypto.password.bcrypt :as bcrypt]
            [chaat.handler.errors :refer [error-table]]))

(use-fixtures :each fixture/test-fixture)

(defn- time-stub
  "Return a fixed timestamp"
  []
  (jt/instant "2023-06-13T10:07:03.172Z"))

(defn- encrypt-stub
  "Return a fixed password hash"
  [_ _]
  "$2a$11$DoWjFwnL5glpyGqBRgdA3uqoy1glTFVoXP.wesem27g2SL3XFXOHW")

(deftest create-test
  (let [datasource (:db fixture/test-system)]
    (with-redefs [model.user/get-time-instant time-stub
                  bcrypt/encrypt encrypt-stub]

      (testing "If username format is invalid, return an error"
        (let [username "j"
              password "12345678"
              actual-result (model.user/create datasource username password)
              expected-result {:result nil :error (:username-format error-table)}]
          (is (= expected-result actual-result))))

      (testing "If password format is invalid, return an error"
        (let [username "john"
              password "12345"
              actual-result (model.user/create datasource username password)
              expected-result {:result nil :error (:password-format error-table)}]
          (is (= expected-result actual-result))))

      (testing "If username and password format are valid, insert user succeeds"
        (let [username "john"
              password "12345678"
              actual-result (model.user/create datasource username password)
              actual-result-without-uuid (update-in actual-result [:result] dissoc :users/id)
              expected-result {:result
                               #:users{:username "john",
                                       :password_hash "$2a$11$DoWjFwnL5glpyGqBRgdA3uqoy1glTFVoXP.wesem27g2SL3XFXOHW",
                                       :creation_timestamp #inst "2023-06-13T10:07:03.172000000-00:00"},
                               :error nil}]
          (is (= expected-result actual-result-without-uuid))))

      ;; depends on previous state, put in a different block?
      (testing "If user already exists, return an error"
        (let [username "john"
              password "12345678"
              actual-result (model.user/create datasource username password)
              expected-result {:result nil :error (:username-exists error-table)}]
          (is (= expected-result actual-result)))))))

(deftest delete-test
  (let [datasource (:db fixture/test-system)]
    (testing "If username format is invalid, return an error"
      (let [username "j"
            actual-result (model.user/delete datasource username)
            expected-result {:result nil :error (:username-format error-table)}]
        (is (= expected-result actual-result))))

    (testing "If username format is valid but user does not exist, delete user fails"
      (let [username "john"
            actual-result (model.user/delete datasource username)
            expected-result {:result nil :error (:username-not-exists error-table)}]
        (is (= expected-result actual-result))))

    (testing "If username format is valid and user exists, delete user succeeds"
      (let [username "john"
            password "12345678"
            _ (model.user/create datasource username password)
            actual-result (model.user/delete datasource username)
            expected-result {:result username :error nil}]
        (is (= expected-result actual-result))))))
