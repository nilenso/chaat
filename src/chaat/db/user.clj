(ns chaat.db.user
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]))

(defn new-user?
  "Check if username exists in username column of user table."
  [datasource username]
  (empty? (sql/find-by-keys datasource :users {:username username})))

(defn insert
  "Insert new user into user table"
  [db user-info]
  (try
    (jdbc/with-transaction [tx (db)]
      (if (new-user? tx (:username user-info))
        (let [query-result (sql/insert! tx :users user-info)]
          {:result query-result :error nil})
        {:result nil :error "Username already exists"}))
    (catch Exception e
      {:result nil :error (str "Postgres exception: " e)})))

;; future: authenticate users as themselves before deleting
(defn delete
  "Remove user from user table"
  [db username]
  (let [query-result (sql/delete! (db) :users {:username username})
        update-count (:next.jdbc/update-count query-result)]
    (if (= 1 update-count)
      {:result username :error nil}
      {:result nil :error "Error deleting user"})))

;; Repl commands
;; (sql/delete! (pg-ds) :users {:username "udit"})
;; (sql/insert! (pg-ds) :users {:username "udit"
;;                            :password_hash "123"})
