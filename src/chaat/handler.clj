(ns chaat.handler
  (:require [ring.util.response :as res]
            [clj-time.local :as l]
            [chaat.model.user :as user]))

;; validation for not null can be done here
;; Check for get/post/put
;; controller layer -> handler

(defn home
  [request]
  (res/response "Welcome to chaat"))

(defn health-check
  [request]
  (->> (str "Service is running: " (l/local-now))
       res/response))

(defn signup
  [request]
  (let [request-method (:request-method request)
        params (:params request)
        username (:user params)
        password (:password params)]
    (if (and username password (user/create-user username password))
      (res/response (str "Successfully created " username " account."))
      (res/bad-request (str "Error creating " username " account.")))))

(defn delete-account
  [request]
  (let [request-method (:request-method request)
        params (:params request)
        username (:user params)]
    (if (and username (user/delete-user username))
      (res/response (str "Account deleted"))
      (res/bad-request (str "Account does not exist")))))

(defn test-page
  [request]
  (res/response (str request)))

(defn not-found
  [request]
  (res/not-found "Resource does not exist"))
