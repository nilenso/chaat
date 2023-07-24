(ns chaat.model.user
  (:require
   [chaat.db.user :as db.user]
   [crypto.password.bcrypt :as bcrypt]
   [java-time.api :as jt]
   [next.jdbc.date-time :as dt]
   [chaat.handler.errors :refer [error-table]]
   [chaat.errors :refer [do-or-error until-err->]]
   [buddy.sign.jwt :as jwt]
   [chaat.config :as config]))

;; Model layer does validation pertaining to the representation of a 
;; user: username & password format (length, special characters etc.)

(defn min-length?
  "Check if string is >= a threshold length"
  [str threshold]
  (>= (count str) threshold))

(defn validate-username-format
  "Basic format check for username"
  [username]
  (let [min-length 2]
    (if (min-length? username min-length)
      {:result username :error nil}
      {:result nil :error (:username-format error-table)})))

(defn validate-password-format
  "Basic format check for password"
  [password]
  (let [min-length 8]
    (if (min-length? password min-length)
      {:result password :error nil}
      {:result nil :error (:password-format error-table)})))

(defn validate-credentials-format
  "Basic format check for signup details: username & password"
  [username password]
  (let [result (until-err-> (validate-username-format username)
                            (validate-password-format password))]
    result))

(defn get-time-instant
  "Returns the result from a call to jt/instant"
  []
  (jt/instant))

(defn gen-new-user
  "Generate user info map for new user"
  [username password]
  (let [work-factor 11
        password-hash (bcrypt/encrypt password work-factor)]
    {:username username
     :password_hash password-hash
     :creation_timestamp (get-time-instant)}))

(defn create
  "Create a user and add user info to db"
  [db username password]
  (let [result (validate-credentials-format username password)
        user-info (do-or-error result gen-new-user-map username password)
        result (do-or-error result db.user/insert db user-info)]
    result))

(defn authenticate
  "Authenticate user and issue signed JWT"
  [retrieved-password-hash username password]
  (let [correct-password? (bcrypt/check password retrieved-password-hash)]
    (if correct-password?
      (let [payload {:sub nil
                     :username username
                     :iat nil
                     :eat nil}
            token (jwt/sign payload (config/get-secret))]
        {:result {:jwt token} :error nil})
      {:result nil :error "Username or password is incorrect"})))

;; (defn login
;;   "Return signed JWT for username if credentials are authenticated"
;;   [db username password]
;;   (let [result (validate-credentials-format username password)
;;         result (do-or-error result db.user/get-password-hash db username)
;;         retrieved-password-hash (get-in result [:result :password-hash])
;;         result (do-or-error result authenticate username password retrieved-password-hash)]
;;     result))

(defn login
  "Return signed JWT for username if credentials are authenticated"
  [db username password]
  (let [result (until-err-> (validate-credentials-format username password)
                            (db.user/get-password-hash db username)
                            (authenticate :result username password))]
    result))

(defn delete
  "Delete user account: remove user info from db"
  [db username]
  (let [result (validate-username-format username)
        result (do-or-error result db.user/delete db username)]
    result))
