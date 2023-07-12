(ns chaat.model.user
  (:require
   [chaat.db.user :as db]
   [crypto.password.bcrypt :as bcrypt]
   [java-time.api :as jt]
   [next.jdbc.date-time :as dt]
   [chaat.errors :refer [do-or-error]]))

(defn min-length?
  "Check if string is >= a threshold length"
  [str threshold]
  (>= (count str) threshold))

;; pertains to the representation of a user, hence this is in the model layer
;; add more restrictions: special characters not allowed, only alphanumeric
(defn validate-username-format
  "Basic format check for username"
  [username]
  (let [min-length 2]
    (if (min-length? username min-length)
      {:result username :error nil}
      {:result nil :error "Wrong username format"})))

(defn validate-password-format
  "Basic format check for password"
  [password]
  (let [min-length 8]
    (if (min-length? password min-length)
      {:result password :error nil}
      {:result nil :error "Wrong password format"})))

(defn validate-signup-details
  "Basic format check for signup details: username & password"
  [username password]
  (let [result (validate-username-format username)
        result (do-or-error result validate-password-format password)]
    result))
;; result here will only contain password if validation passes

(defn gen-new-user-map
  "Generate user info map for new user"
  [username password]
  (let [work-factor 11
        password-hash (bcrypt/encrypt password work-factor)]
    {:username username
     :password_hash password-hash
     :creation_timestamp (jt/instant)
     :display_picture nil}))

(defn create
  "Create a user and add user info to db"
  [db username password]
  (let [result (validate-signup-details username password)
        user-info (do-or-error result gen-new-user-map username password)
        result (do-or-error result db/insert db user-info)]
    result))

(defn delete
  "Delete user account: remove user info from db"
  [db username]
  (let [result (validate-username-format username)
        result (do-or-error result db/delete db username)]
    result))

;; Repl testing code
;; (sql/insert! pg-db :users (gen-new-user-map "udit" "12345678"))
;; (sql/delete! pg-dbspec :users {:username "udit"})
;; (sql/insert! pg-dbspec :users (gen-new-user-map "udit" "12345678"))

;; (defn gen-test-users
;;   []
;;   (create-user "udit" "12345678")
;;   (create-user "shivam" "12345678"))
