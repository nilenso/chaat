(ns chaat.db.user
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.connection :as connection]
   [chaat.handler.errors :refer [error-table]]))

(defn new-user?
  "Return true if username does not exist in db"
  [connection username]
  (empty? (sql/find-by-keys connection :users {:username username})))

(defn user-exists?
  "Return true if username exists in db"
  [connection username]
  (not (new-user? connection username)))

(defn insert
  "Insert new user into user table"
  [db user-info]
  (jdbc/with-transaction [tx (db)]
    (if (new-user? tx (:username user-info))
      (let [query-result (sql/insert! tx :users user-info)]
        {:result query-result :error nil})
      {:result nil :error (:username-exists error-table)})))

(defn delete
  "Remove user from user table"
  [db username]
  (let [query-result (sql/delete! (db) :users {:username username})
        update-count (:next.jdbc/update-count query-result)]
    (if (= 1 update-count)
      {:result username :error nil}
      {:result nil :error (:username-not-exists error-table)})))

(defn get-user-details
  "Retrieve user details for username"
  [db username]
  (jdbc/with-transaction [tx (db)]
    (if (user-exists? tx username)
      (let [query ["SELECT * FROM users WHERE username = ?" username]
            query-result (jdbc/execute-one! tx query)]
        {:result query-result :error nil})
      {:result nil :error (:username-not-exists error-table)})))

(comment
  (def db (:db chaat.app/chaat-system))
  (new-user? ((:db chaat.app/chaat-system)) "neena")
  (user-exists? ((:db chaat.app/chaat-system)) "udit")
  (get-user-details db "neena"))
