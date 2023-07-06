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
  "Check if username exists in username column of user table"
  [username]
  (if (empty? (sql/find-by-keys (pg-ds) :users {:username username}))
    [username nil]
    [nil "Username already exists"]))

(defn add-user
  "Insert new user into user table"
  [user-info]
  (try
    (jdbc/with-transaction [tx (pg-ds)]
      ;; use a let for new user check.
      (if (new-user? (:username user-info))
        (let [query-result (sql/insert! tx :users user-info)]
          [query-result nil])
        [nil "Username already exists"]))
    ;; improve error catching
    (catch Exception e
      [nil (str "Postgres exception: " e)])))

(defn delete-user
  "Remove user from user table"
  [username]
  (let [query-result (sql/delete! (pg-ds) :users {:username username})
        update-count (:next.jdbc/update-count query-result)]
    (if (= 1 update-count)
      [username nil]
      [nil "Error deleting user"])))

;; Repl commands
;; (sql/delete! (pg-ds) :users {:username "udit"})
;; (sql/insert! (pg-ds) :users {:username "udit"
;;                            :password_hash "123"})
