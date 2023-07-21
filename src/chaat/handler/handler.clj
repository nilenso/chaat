(ns chaat.handler.handler
  (:require [ring.util.response :as res]
            [chaat.model.user :as model.user]
            [java-time.api :as jt]
            [chaat.handler.validation :as handler.validation]
            [chaat.db.utils :as db.utils]
            [chaat.errors :refer [do-or-error until-err->]]
            [cheshire.core :as json]))

(defn send-response
  [{:keys [result error]}]
  (if-not error
    (res/response (json/encode result))
    (-> (res/response (json/encode (:msg error)))
        (res/status (:status-code error)))))

(defn home
  "Respond with a greeting/welcome"
  [request]
  (res/response "Welcome to chaat"))

(defn health-check
  "Respond with application status: checks server and db status"
  [db request]
  (let [result (db.utils/health-check db)]
    (send-response result)))

(defn signup
  "Create a user account with supplied parameters"
  [db request]
  (let [params (:params request)
        {:keys [username password]} params
        result (handler.validation/validate-credentials username password)
        result (do-or-error result model.user/create db username password)]
    (send-response result)))

;; (defn login
;;   "Authenticate username and password, and return JWT if credentials are correct"
;;   [db request]
;;   (let [params (:params request)
;;         {:keys [username password]} params
;;         result (handler.validation/validate-credentials username password)
;;         result (do-or-error result model.user/login db username password)]
;;     (if (nil? (:error result))
;;       (let [token (get-in result [:result :jwt])]
;;         (res/response (str token)))
;;       (res/bad-request (str (:error result))))))

;; what did neena say about getting the db direct from the system? so not passed as a parameter then?
(defn login
  "Authenticate username and password, and return JWT if credentials are correct"
  [db {:keys [params]}]
  (let [{:keys [username password]} params
        result (until-err-> (handler.validation/validate-credentials username password)
                            (model.user/login db username password))]
    (send-response result)))

(defn delete-user
  "Delete a user account"
  [db request]
  (let [params (:params request)
        username (:username params)
        result (handler.validation/validate-username username)
        result (do-or-error result model.user/delete db username)]
    (send-response result)))

(defn test-page
  "Display the request made to this endpoint for debugging purposes"
  [request]
  (res/response (str request)))

(defn not-found
  "Catch-all not-found page"
  [request]
  (res/not-found "Resource does not exist"))
