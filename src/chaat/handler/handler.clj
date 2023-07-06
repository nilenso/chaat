(ns chaat.handler.handler
  (:require [ring.util.response :as res]
            [chaat.model.user :as user]
            [java-time.api :as jt]
            [chaat.handler.validation :as validation]))

;; in validation.clj:
;; consider using validation library like schema to check shape: types and structure

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
        [params err] (validation/validate-signup-details params)
        [params err] (if (nil? err) (user/create username password) [nil err])]
    (if (nil? err)
      (res/response "Signup successful")
      (res/bad-request err))))

(defn delete-user
  [request]
  (let [params (:params request)
        username (:username params)
        [param err] (validation/validate-username username)
        [param err] (if (nil? err) (user/delete username) [nil err])]
    (if (nil? err)
      (res/response "Successfully deleted user")
      (res/bad-request err))))

(defn test-page
  [request]
  (res/response (str request)))

(defn not-found
  [request]
  (res/not-found "Resource does not exist"))
