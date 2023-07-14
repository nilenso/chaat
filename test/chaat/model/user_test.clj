(ns chaat.model.user-test
  (:require [clojure.test :refer :all]
            [chaat.db.user :as db.user]
            [chaat.model.user :as model.user]))

;; no need to have the system running when testing model layer logic since
;; we're using stub functions. but this will change since we now plan to add 
;; integration tests
;; (use-fixtures :once fixture/test-fixture)

;; no need for real datasource
(def datasource {})

;; no need to test this?
(deftest gen-new-user-map-test
  (testing ""))

(defn- insert-stub
  "Used as a stub function for db.user/insert
   Mimics a successful user insertion result from db.user/insert"
  [_ {:keys [username]}]
  {:result
   #:users{:id #uuid "4961adde-fc53-4808-b8d2-f8131a9b1265",
           :username username,
           :password_hash "$2a$11$w3OyB7wW6Ma6DFmSzcDjCOQ6CHgWYpZbpQaewkePFZ8WHYEw6jbwK",
           :creation_timestamp #inst "2023-07-13T05:50:51.726902000-00:00",
           :display_picture nil},
   :error nil})

(defn- delete-stub
  "Used as a stub function for db.user/delete
   Mimics a successful user deletion result from db.user/delete"
  [_ username]
  {:result username :error nil})

(deftest create-test
  (testing "If username and password format are valid, proceed to call db.user/insert"
    (with-redefs [db.user/insert insert-stub]
      (let [username "john"
            password "12345678"
            expected-result {:result
                             #:users{:id #uuid "4961adde-fc53-4808-b8d2-f8131a9b1265",
                                     :username "john",
                                     :password_hash "$2a$11$w3OyB7wW6Ma6DFmSzcDjCOQ6CHgWYpZbpQaewkePFZ8WHYEw6jbwK",
                                     :creation_timestamp #inst "2023-07-13T05:50:51.726902000-00:00",
                                     :display_picture nil},
                             :error nil}]
        (is (= expected-result (model.user/create datasource username password))))

      (testing "If username format is invalid, return an error"
        (let [username "j"
              password "12345678"
              expected-result {:result nil :error "Wrong username format"}]
          (is (= expected-result (model.user/create datasource username password)))))

      (testing "If password format is invalid, return an error"
        (let [username "john"
              password "12345"
              expected-result {:result nil :error "Wrong password format"}]
          (is (= expected-result (model.user/create datasource username password))))))))

(deftest delete-test
  (testing "If username format is valid, proceed to call db.user/delete"
    (with-redefs [db.user/delete delete-stub]
      (let [username "john"
            expected-result {:result username :error nil}]
        (is (= expected-result (model.user/delete datasource username))))

      (testing "If username format is invalid, return an error"
        (let [username "j"
              expected-result {:result nil :error "Wrong username format"}]
          (is (= expected-result (model.user/delete datasource username))))))))
