(ns chaat.migrations
  (:require [ragtime.jdbc :as ragtime.jdbc]
            [ragtime.repl :as repl]
            [chaat.config :as config]))

(defn gen-config-map
  [dbspec]
  {:datastore (ragtime.jdbc/sql-database dbspec)
   :migrations (ragtime.jdbc/load-resources "migrations")})

(defn rollback []
  (repl/rollback (gen-config-map (config/get-pg-dbspec))))

(defn run-migrations []
  (repl/migrate (gen-config-map (config/get-pg-dbspec))))

(comment
  {:connection-uri
   "jdbc:postgresql://127.0.0.1:8001/chaat-db?user=chaat-dev&password=password"})


