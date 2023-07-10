(ns chaat.handler.handler
  (:require [ring.util.response :as res]
            [chaat.model.user :as user]
            [java-time.api :as jt]
            [chaat.handler.validation :as validation]
            [chaat.errors :refer [do-or-error]]))

(defn home
  [request]
  (res/response "Welcome to chaat"))

(defn health-check
  [request]
  (res/response
   (str "Service is running: " (jt/local-time))))

(defn signup
  [request]
  (let [params (:params request)
        {:keys [username password]} params
        result (validation/validate-signup-details username password)
        result (do-or-error result user/create username password)]
    (if (nil? (:error result))
      (res/response "Signup successful")
      (res/bad-request (str (:error result))))))

(defn delete-user
  [request]
  (let [params (:params request)
        username (:username params)
        result (validation/validate-username username)
        result (do-or-error result user/delete username)]
    (if (nil? (:error result))
      (res/response "Successfully deleted user")
      (res/bad-request (str (:error result))))))

(defn test-page
  [request]
  (res/response (str request)))

(defn not-found
  [request]
  (res/not-found "Resource does not exist"))
