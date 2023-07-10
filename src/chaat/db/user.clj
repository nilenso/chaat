(ns chaat.db.user
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.connection :as conn]
   [com.stuartsierra.component :as component]
   [chaat.config :as config])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn create-data-source
  "Builds a connection pooled data source. Returns an entity which can be
   invoked as a function with no arguments to return that datasource"
  [dbspec]
  (->> dbspec
       (conn/component HikariDataSource)
       (component/start)))

;; connection pooled data source. needs to be invoked as a fn.
(def pg-ds
  (create-data-source (config/get-pg-dbspec)))

(defn new-user?
  "Check if username exists in username column of user table."
  [datasource username]
  (empty? (sql/find-by-keys datasource :users {:username username})))

(defn insert
  "Insert new user into user table"
  [user-info]
  (try
    (jdbc/with-transaction [tx (pg-ds)]
      (if (new-user? tx (:username user-info))
        (let [query-result (sql/insert! tx :users user-info)]
          {:result query-result :error nil})
        {:result nil :error "Username already exists"}))
    (catch Exception e
      {:result nil :error (str "Postgres exception: " e)})))

;; authenticate users before deleting
(defn delete
  "Remove user from user table"
  [username]
  (let [query-result (sql/delete! (pg-ds) :users {:username username})
        update-count (:next.jdbc/update-count query-result)]
    (if (= 1 update-count)
      {:result username :error nil}
      {:result nil :error "Error deleting user"})))

;; Repl commands
;; (sql/delete! (pg-ds) :users {:username "udit"})
;; (sql/insert! (pg-ds) :users {:username "udit"
;;                            :password_hash "123"})
