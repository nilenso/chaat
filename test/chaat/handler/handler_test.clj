(ns chaat.handler.handler-test
  (:require [clojure.test :refer :all]
            [chaat.handler.handler :as handler]
            [java-time.api :as jt]
            [chaat.model.user :as model.user]
            [chaat.fixture :as fixture]
            [cheshire.core :as json]
            [chaat.config :as config]
            [buddy.sign.jwt :as jwt]))

(use-fixtures :each fixture/test-fixture)

(deftest health-check-test
  (testing "Request to health check endpoint should return status 200"
    (let [datasource (:db fixture/test-system)
          request {}
          response (handler/health-check datasource request)]
      (is (= 200 (:status response))))))

(deftest signup-test
  (let [datasource (:db fixture/test-system)]
    (testing "Missing username or password in request params, signup fails"
      (let [params {:password "12345678"}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 400 (:status response)))))

    (testing "Nil username or password in request params, signup fails"
      (let [params {:username "john"
                    :password nil}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 400 (:status response)))))

    (testing "Username and password both present in request params, signup succeeds"
      (let [params {:username "john"
                    :password "12345678"}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 200 (:status response)))))

    (testing "Username and password both present, 
                but user already exists, hence signup fails"
      (let [params {:username "john"
                    :password "12345678"}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 409 (:status response)))))))

(deftest login-test
  (let [datasource (:db fixture/test-system)
        username "john"
        password "12345678"
        params {:username username
                :password password}]

    (testing "Login fails when user does not exist"
      (let [request {:params params}
            response (handler/login datasource request)]
        (is (= 400 (:status response)))))

    (let [_ (handler/signup datasource {:params {:username username :password password}})]
      (testing "Login passes when user exists and credentials are correct"
        (let [request {:params params}
              response (handler/login datasource request)
              payload {:sub nil
                       :username username
                       :iat nil
                       :eat nil}
              expected-jwt (jwt/sign payload (config/get-secret))
              actual-jwt (:jwt (json/decode (:body response) true))]
          (is (= 200 (:status response)))
          (is (= expected-jwt actual-jwt)))))))

(deftest delete-user-test
  (let [datasource (:db fixture/test-system)]
    (testing "Missing username in request, delete fails with 400 bad request"
      (let [params {}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 400 (:status response)))))

    (testing "Username param in request with value nil, delete fails with 400 bad request"
      (let [params {:username nil}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 400 (:status response)))))

    (testing "Username parameter present in request,
              but user does not exist, delete fails with 404 not found"
      (let [params {:username "john"}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 404 (:status response)))))

    (let [username "john"
          password "12345678"
          params {:username username}
          identity {:username username}]

      ;; create user john
      (let [_ (handler/signup datasource {:params {:username username :password password}})]
        (testing "Unauthorized request, delete fails"
          (let [request {:params params}
                response (handler/delete-user datasource request)]
            (is (= 400 (:status response)))))
        ;; this should be 401, need to modify handler code also

        (testing "Authorized request, delete succeeds"
          (let [request {:params params
                         :identity identity}
                response (handler/delete-user datasource request)]
            (is (= 200 (:status response)))))))))
