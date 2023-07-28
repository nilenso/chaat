(ns chaat.handler.handler
  (:require [ring.util.response :as res]
            [chaat.model.user :as model.user]
            [chaat.model.friend-request :as friend-request]
            [java-time.api :as jt]
            [chaat.handler.validation :as handler.validation]
            [chaat.db.utils :as db.utils]
            [chaat.errors :refer [do-or-error until-err->]]
            [cheshire.core :as json]
            [chaat.handler.errors :refer [error-table]]))

(defn send-response
  "Construct a response with the correct body and status code"
  [{:keys [result error]}]
  (if-not error
    (res/response (json/encode result))
    (-> (res/response (json/encode (:msg error)))
        (res/status (:status-code error)))))

(defn is-auth-user
  "Check if username and authenticated identity match"
  [request username]
  (let [auth-user (get-in request [:identity :username])]
    (if (= auth-user username)
      {:result username :error nil}
      {:result nil :error (:unauthorized-action error-table)})))

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

(defn login
  "Authenticate username and password, and return JWT if credentials are correct"
  [db {:keys [params]}]
  (let [{:keys [username password]} params
        result (until-err-> (handler.validation/validate-credentials username password)
                            (model.user/login db username password))]
    (send-response result)))

(defn delete-user
  "Delete username if authenticated as username"
  [db {:keys [params] :as request}]
  (let [username (:username params)
        result (until-err-> (handler.validation/validate-username username)
                            (is-auth-user request username)
                            (model.user/delete db username))]
    (send-response result)))

;; need handler layer validation
(defn create-friend-request
  [db {:keys [params] :as request}]
  (let [username (:username params)
        content (select-keys params [:username :recipient :message])
        result (until-err-> (is-auth-user request username)
                            (friend-request/create db content))]
    (send-response result)))

;; need authentication
(defn accept-friend-request
  [db {:keys [route-params]}]
  (let [id (Integer/parseInt (:id route-params))
        result (friend-request/accept db id)]
    (send-response result)))

;; need authentication
(defn reject-friend-request
  [db {:keys [route-params]}]
  (let [id (Integer/parseInt (:id route-params))
        result (friend-request/reject db id)]
    (send-response result)))

(defn test-page
  "Display the request made to this endpoint for debugging purposes"
  [request]
  (res/response (str request)))

(defn not-found
  "Catch-all not-found page"
  [request]
  (res/not-found "Resource does not exist"))

(comment
  (def db (:db chaat.app/chaat-system))
  (def params {:username "shahn"
               :recipient "neena"
               :message "hola"})
  (def request {:params params})
  (create-friend-request db request))
