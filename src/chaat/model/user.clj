(ns chaat.model.user
  (:require
   [chaat.config :as config]
   [chaat.db.user :as db]
   [crypto.password.bcrypt :as bcrypt]
   [crypto.random :as random]
   [java-time.api :as jt]))

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
      [username nil]
      [nil "Wrong username format"])))

(defn validate-password-format
  "Basic format check for password"
  [password]
  (let [min-length 8]
    (if (min-length? password min-length)
      [password nil]
      [nil "Wrong password format"])))

(defn validate-signup-details
  "Basic format check for signup details: username & password"
  [username password]
  (let [[param err] (validate-username-format username)
        [param err] (if (nil? err) (validate-password-format password) [nil err])]
    [param err]))
;; param here will only contain username if validation passes

(defn gen-new-user-map
  "Generate user info map for new user"
  [username password]
  (let [static-salt (config/get-static-salt)
        dynamic-salt (crypto.random/base64 8)
        salted-password (str password static-salt dynamic-salt)]
    {:username username
     :dynamic_salt dynamic-salt
     :password_hash (bcrypt/encrypt salted-password)
     :creation_timestamp (jt/instant)
     :display_picture nil}))

(defn create
  "Create a user and add user info to db"
  [username password]
  (let [[param err] (validate-signup-details username password)
        user-info  (if (nil? err) (gen-new-user-map username password) [nil err])
        [param err] (if (nil? err) (db/add-user user-info) [nil err])]
    [param err]))

(defn delete
  "Delete user account: remove user info from db"
  [username]
  (let [[param err] (validate-username-format username)
        [param err] (if (nil? err) (db/delete-user username) [nil err])]
    [param err]))

;; Repl testing code
;; (sql/insert! pg-db :users (gen-new-user-map "udit" "12345678"))
;; (sql/delete! pg-dbspec :users {:username "udit"})
;; (sql/insert! pg-dbspec :users (gen-new-user-map "udit" "12345678"))

;; (defn gen-test-users
;;   []
;;   (create-user "sezal" "12345678")
;;   (create-user "udit" "12345678")
;;   (create-user "shivam" "12345678"))
