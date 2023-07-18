(ns chaat.handler.handler
  (:require [ring.util.response :as res]
            [chaat.model.user :as model.user]
            [java-time.api :as jt]
            [chaat.handler.validation :as handler.validation]
            [chaat.errors :refer [do-or-error]]
            [chaat.db.utils :as db.utils]))

(defn home
  "Respond with a greeting/welcome"
  [request]
  (res/response "Welcome to chaat"))

(defn health-check
  "Respond with application status: checks server and db status"
  [db request]
  (let [result (db.utils/health-check db)]
    (if (nil? (:error result))
      (res/response (str "all systems go: " (jt/instant)))
      (res/bad-request (str (:error result))))))

(defn signup
  "Create a user account with supplied parameters"
  [db request]
  (let [params (:params request)
        {:keys [username password]} params
        result (handler.validation/validate-signup-details username password)
        result (do-or-error result model.user/create db username password)]
    (if (nil? (:error result))
      (res/response "Signup successful")
      (res/bad-request (str (:error result))))))

(defn delete-user
  "Delete a user account"
  [db request]
  (let [params (:params request)
        username (:username params)
        result (handler.validation/validate-username username)
        result (do-or-error result model.user/delete db username)]
    (if (nil? (:error result))
      (res/response (str request))
      (res/bad-request (str (:error result))))))

(defn test-page
  "Display the request made to this endpoint for debugging purposes"
  [request]
  (res/response (str request)))

(defn not-found
  "Catch-all not-found page"
  [request]
  (res/not-found "Resource does not exist"))
