(ns chaat.db.user
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]))

;; these 2 functions do the same thing, will remove one of them
(defn new-user?
  "Check if username exists in username column of user table."
  [datasource username]
  (empty? (sql/find-by-keys datasource :users {:username username})))

(defn user-exists?
  [datasource username]
  (let [result (sql/find-by-keys datasource :users {:username username})]
    (if (seq result)
      true false)))

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

;; future: add authentication/authorization for deleting
;; users need to be authenticated as username to gain authorization to delete username
(defn delete
  "Remove user from user table"
  [db username]
  (let [query-result (sql/delete! (db) :users {:username username})
        update-count (:next.jdbc/update-count query-result)]
    (if (= 1 update-count)
      {:result username :error nil}
      {:result nil :error "Error deleting user"})))
