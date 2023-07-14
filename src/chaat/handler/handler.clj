(ns chaat.handler.handler
  (:require [ring.util.response :as res]
            [chaat.model.user :as user]
            [java-time.api :as jt]
            [chaat.handler.validation :as validation]
            [chaat.errors :refer [do-or-error]]))

(defn home
  "Respond with a greeting/welcome"
  [request]
  (res/response "Welcome to chaat"))

(defn health-check
  "Respond with application status"
  [request]
  (res/response
   (str "Service is running: " (jt/local-time))))

(defn signup
  "Create a user account with supplied parameters"
  [db request]
  (let [params (:params request)
        {:keys [username password]} params
        result (validation/validate-signup-details username password)
        result (do-or-error result user/create db username password)]
    (if (nil? (:error result))
      (res/response "Signup successful")
      (res/bad-request (str (:error result))))))

(defn delete-user
  "Delete a user account"
  [db request]
  (let [params (:params request)
        username (:username params)
        result (validation/validate-username username)
        result (do-or-error result user/delete db username)]
    (if (nil? (:error result))
      (res/response "Successfully deleted user")
      (res/bad-request (str (:error result))))))

(defn test-page
  "Display the request made to this endpoint for debugging purposes"
  [request]
  (res/response (str request)))

(defn not-found
  "Catch-all not-found page"
  [request]
  (res/not-found "Resource does not exist"))
