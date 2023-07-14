(ns chaat.handler.handler-test
  (:require [clojure.test :refer :all]
            [chaat.handler.handler :as handler]
            [java-time.api :as jt]
            [chaat.model.user :as model.user]))

;; no need to have the system running when testing handler layer logic if we're
;; using stub functions. but we now plan to add integration tests, so I'll need an
;; active db connection.
;; (use-fixtures :once fixture/test-fixture)

;; no need for real datasource
(def datasource {})
;; (def datasource (:db fixture/test-system))

(defn- time-stub
  "Return a fixed timestamp"
  []
  "2023-06-13T10:07:03.172Z")

(defn- create-stub
  "Used as a stub function for user/create
   Mimics a successful result from user/create"
  [_ username _]
  {:result #:users{:id #uuid "5af37abe-491b-4056-a6fe-69a9499bedf2",
                   :username username,
                   :password_hash "$2a$11$6L2cHH.9M8LZO9vWL9n6VeSggEvxCCDm5tqUhFis48kvsQ/DR7aDe",
                   :creation_timestamp #inst "2023-07-13T07:30:29.401634000-00:00",
                   :display_picture nil},
   :error nil})

(defn- delete-stub
  "Used as a stub function for user/delete
   Mimics a successful result from user/delete"
  [_ username]
  {:result username :error nil})

(deftest health-check-test
  (testing "Request to health check endpoint should return status 200"
    (with-redefs [jt/instant time-stub]
      (let [request {}
            response (handler/health-check request)]
        (is (= 200 (:status response)))))))

(deftest signup-test
  (with-redefs [model.user/create create-stub]
    (testing "Username and password both present in request params.
              Proceed to call model.user/create and respond with status 200"
      (let [params {:username "john"
                    :password "12345678"}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 200 (:status response)))))

    (testing "Missing username or password in request params,
              Do not call model.user/create, and respond with status 400"
      (let [params {:password "12345678"}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 400 (:status response)))))

    (testing "Nil username or password in request params,
              Do not call model.user/create, and respond with status 400"
      (let [params {:username "john"
                    :password nil}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 400 (:status response)))))))

(deftest delete-user-test
  (with-redefs [model.user/delete delete-stub]
    (testing "Username present in request params, proceed to
              call model.user/delete and respond with status 200"
      (let [params {:username "john"}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 200 (:status response)))))

    (testing "Missing username in request, do not call model.user/delete and
              respond with status 400"
      (let [params {}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 400 (:status response)))))))
