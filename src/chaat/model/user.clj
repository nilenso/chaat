(ns chaat.model.user
  (:require [clojure.java.jdbc :as jdbc]
            [chaat.config :as config :refer [pg-db]]
            [crypto.password.bcrypt :as pwd]
            [next.jdbc.sql :as next.jdbc.sql]
            [next.jdbc.result-set :as rs]
            [clj-time.local :as l]
            [clojure.java.jdbc :as sql]))

(defn user-exists?
  "Check if username exists in username column of users table"
  [username]
  (let [query ["SELECT exists (SELECT 1 FROM users WHERE username = ? LIMIT 1);" username]
        result (jdbc/query pg-db query)]
    (:exists (first result))))

;; Note: validation is different from authentication
;; Add more checks later: special characters etc.
(defn validate-username
  "Check length and uniqueness of username"
  [username]
  (if (and (not user-exists?) (>= (count username) 2))
    true false))

;; Add more restrictions later: Caps, special characters etc.
(defn validate-password
  "Check length of password"
  [password]
  (>= (count password) 8))

(defn create-user
  "Create a user and add user info to db"
  [username password]
  (let [valid-username (validate-username username)
        valid-password (validate-password password)
        dynamic-salt nil
        password-hash (pwd/encrypt password)
        join-date nil
        display-picture nil]
    (try
      (jdbc/insert! (:pg-db config/app-config) :users {:username username
                                                       :dynamic_salt dynamic-salt
                                                       :pwd_hash password-hash
                                                       :join_date join-date
                                                       :display_picture display-picture})
      (catch Exception e [false (str e)]))))

(defn delete-user
  "Delete user account and remove user info from db"
  [username]
  (jdbc/delete! pg-db :users ["username = ?" username]))

;; (jdbc/query pg-db ["SELECT exists (SELECT 1 FROM users WHERE username = ? LIMIT 1);" "shahn"])
;; (jdbc/query pg-db ["SELECT exists (SELECT 1 FROM ? WHERE ? = ? LIMIT 1);" "users" "username" "shahn"])
;; (jdbc/execute! pg-db ["INSERT INTO ? (?) VALUES (?)" "shivam"])
;; (jdbc/query pg-db ["SELECT ? FROM ? WHERE ? = ? LIMIT (?)" 1 "users" "username" "shahn" 1])

;; what sort of values should functions in my model layer return to the handlers
;; this will be for the handler to decide whether to send a good/bad response

