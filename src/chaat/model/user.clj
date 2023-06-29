(ns chaat.model.user
  (:require
   [chaat.config :as config :refer [pg-db]]
   [crypto.password.bcrypt :as pwd]
   [crypto.random :as random]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.date-time]
   [java-time.api :as jt]))

;; what sort of values should functions in my model layer return to the handlers
;; this will be for the handler to decide whether to send a good/bad response

;; 2 ways to organize
;; by mechanism (db operation etc.)
;; by property (uniqueness is a property of a user (username))

(defn new-user?
  "Check if username does not exist in username column of user table.
   If username does not exist, return true, else false."
  [username]
  (empty? (sql/find-by-keys pg-db :users {:username username})))

(defn gen-new-user-map
  [username password]
  (let [static-salt config/static-salt
        dynamic-salt (crypto.random/base64 8)
        salted-password (str password static-salt dynamic-salt)]
    {:username username
     :dynamic_salt dynamic-salt
     :password_hash (pwd/encrypt salted-password)
     :creation_timestamp (jt/instant)
     :display_picture nil}))

;; create a separate db layer.
;; db layer will be responsible for making sense of the map received
;; can have another translation layer which the db layer will call

;; repeated, will make something like a helper.clj for these functions
(defn build-result-map
  [success message description]
  {:success success
   :message message
   :description description})

;; Can use :pre and :post for validation with assertion errors
(defn create-user
  "Create a user and add user info to db"
  [username password]
  (try
    (jdbc/with-transaction [tx pg-db]
      (if (new-user? username)
        (->> (sql/insert! tx :users (gen-new-user-map username password))
             (build-result-map true nil))
        (build-result-map false (str username " already exists") :username)))
    ;; potentially only catch known exceptions here
    (catch Exception e
      (build-result-map false "PostgreSQL Exception" (str e)))))

(defn delete-user
  "Delete user account: remove user info from db"
  [username]
  (if (= 1 (:next.jdbc/update-count (sql/delete! pg-db :users {:username username})))
    (build-result-map true (str "Successfully deleted " username) nil)
    (build-result-map false (str "Error deleting " username) nil)))

;; Repl testing code
;; (sql/insert! pg-db :users (gen-new-user-map "uditm" "12345678"))

;; (defn gen-test-users
;;   []
;;   (create-user "sezal" "12345678")
;;   (create-user "udit" "12345678")
;;   (create-user "shivam" "12345678"))
