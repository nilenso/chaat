(ns chaat.handler.handler-test
  (:require [clojure.test :refer :all]
            [chaat.handler.handler :as handler]
            [java-time.api :as jt]
            [chaat.model.user :as model.user]
            [chaat.fixture :as fixture]))

(use-fixtures :each fixture/test-fixture)

(defn- time-stub
  "Return a fixed timestamp"
  []
  (jt/instant "2023-06-13T10:07:03.172Z"))

(defn- encrypt-stub
  "Return a fixed password hash"
  [_ _]
  "$2a$11$DoWjFwnL5glpyGqBRgdA3uqoy1glTFVoXP.wesem27g2SL3XFXOHW")

(deftest health-check-test
  (testing "Request to health check endpoint should return status 200"
    (let [request {}
          response (handler/health-check request)]
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

      ;; perhaps no need to test this here? been tested at other layers already.
      ;; depends on previous state, move to different block?
    (testing "Username and password both present, 
                but user already exists, hence signup fails"
      (let [params {:username "john"
                    :password "12345678"}
            request {:params params}
            response (handler/signup datasource request)]
        (is (= 400 (:status response)))))))

(deftest delete-user-test
  (let [datasource (:db fixture/test-system)]
    (testing "Missing username in request, delete fails"
      (let [params {}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 400 (:status response)))))

    (testing "Username param in request with value nil, delete fails"
      (let [params {:username nil}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 400 (:status response)))))

    ;; perhaps no need, already tested in other layers?
    (testing "Username param in request, but user does not exist, delete fails"
      (let [params {:username "john"}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 400 (:status response)))))

    (testing "Username param in request, and user exists, delete succeeds"
      (let [username "john"
            password "12345678"
            _ (handler/signup datasource {:params {:username username :password password}})
            params {:username "john"}
            request {:params params}
            response (handler/delete-user datasource request)]
        (is (= 200 (:status response)))))))
