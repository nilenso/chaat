(ns chaat.db.user-test
  (:require [chaat.fixture :as fixture]
            [clojure.test :refer :all]
            [chaat.db.user :as db.user]
            [chaat.model.user :as model.user]
            [crypto.password.bcrypt :as bcrypt]
            [java-time.api :as jt]
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

(deftest insert-test
  (let [username "john"
        password "12345678"
        datasource (:db fixture/test-system)]
    (testing "Insert new user into user table succeeds"
      (with-redefs [model.user/get-time-instant time-stub
                    bcrypt/encrypt encrypt-stub]
        (let [user-info (model.user/gen-new-user username password)
              actual-result (db.user/insert datasource user-info)
              actual-result-without-id (update-in actual-result [:result] dissoc :users/id)
              expected-result {:result
                               #:users{:username "john",
                                       :password_hash "$2a$11$DoWjFwnL5glpyGqBRgdA3uqoy1glTFVoXP.wesem27g2SL3XFXOHW",
                                       :creation_timestamp #inst "2023-06-13T10:07:03.172000000-00:00"},
                               :error nil}]
          (is (= true (db.user/user-exists? (datasource) "john")))
          (is (= expected-result actual-result-without-id))))

      ;; depends on prior state. put into different block?
      (testing "Insert existing user into user table fails"
        (let [user-info (model.user/gen-new-user username password)
              actual-result (db.user/insert datasource user-info)
              expected-result {:result nil :error (:username-exists error-table)}]
          (is (= expected-result actual-result)))))))

(deftest delete-test
  (let [username "john"
        password "12345678"
        user-info (model.user/gen-new-user username password)
        datasource (:db fixture/test-system)]

    (testing "Remove non-existent user from user table"
      (let [expected-result {:result nil :error (:username-not-exists error-table)}
            actual-result (db.user/delete datasource username)]
        (is (= expected-result actual-result))))

    (testing "Remove existing user from user table"
      (let [_ (db.user/insert datasource user-info)
            expected-result {:result username :error nil}
            actual-result (db.user/delete datasource username)]
        (is (= expected-result actual-result))
        (is (= false (db.user/user-exists? (datasource) username)))))))
