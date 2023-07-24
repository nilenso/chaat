(ns chaat.model.user-test
  (:require [clojure.test :refer :all]
            [chaat.db.user :as db.user]
            [chaat.model.user :as model.user]
            [chaat.fixture :as fixture]
            [java-time.api :as jt]
            [crypto.password.bcrypt :as bcrypt]
            [chaat.handler.errors :refer [error-table]]
            [buddy.sign.jwt :as jwt]
            [chaat.config :as config]))

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

(deftest authenticate-test
  (let [username "john"
        password "12345678"
        work-factor 11
        retrieved-password-hash (bcrypt/encrypt password work-factor)
        payload {:sub nil
                 :username username
                 :iat nil
                 :eat nil}
        token (jwt/sign payload (config/get-secret))]

    (testing "Return map with JWT when password hashes match"
      (let [expected-result {:result {:jwt token} :error nil}
            actual-result (model.user/authenticate retrieved-password-hash username password)]
        (is (= expected-result actual-result))))

    (testing "Return map with error when password hashes do not match"
      (let [password "12312312"
            actual-result (model.user/authenticate retrieved-password-hash username password)
            expected-result {:result nil :error "Username or password is incorrect"}]
        (is (= expected-result actual-result))))))

(deftest login-test
  (let [datasource (:db fixture/test-system)
        username "john"
        password "12345678"
        payload {:sub nil
                 :username username
                 :iat nil
                 :eat nil}
        token (jwt/sign payload (config/get-secret))]

    (testing "JWT is not generated for invalid credentials"
      (let [username "j"
            actual-result (model.user/login datasource username password)
            expected-result {:result nil :error "Wrong username format"}]
        (is (= expected-result actual-result))))

    (testing "JWT is not generated for user that does not exist"
      (let [actual-result (model.user/login datasource username password)
            expected-result {:result nil :error "Username or password is incorrect"}]
        (is (= expected-result actual-result))))

    ;; create user john
    (let [_ (model.user/create datasource username password)]
      (testing "JWT is not generated for existing user with wrong password"
        (let [password "12312312"
              actual-result (model.user/login datasource username password)
              expected-result {:result nil :error "Username or password is incorrect"}]
          (is (= expected-result actual-result))))

      (testing "JWT is generated for existing user with correct password"
        (let [actual-result (model.user/login datasource username password)
              expected-result {:result {:jwt token} :error nil}]
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
