(ns chaat.migrations
  (:require [ragtime.jdbc :as ragtime.jdbc]
            [ragtime.repl :as repl]
            [chaat.config :as config]))

;; using the migration functions in the ragtime.repl namespace for now

(defn gen-config
  [dbspec]
  {:datastore (ragtime.jdbc/sql-database dbspec)
   :migrations (ragtime.jdbc/load-resources "migrations")})

(defn rollback [dbspec]
  (repl/rollback (gen-config dbspec)))

(defn run-migrations [dbspec]
  (repl/migrate (gen-config dbspec)))

(comment
  {:connection-uri
   "jdbc:postgresql://127.0.0.1:8001/chaat_db?user=chaat_dev&password=password"})
