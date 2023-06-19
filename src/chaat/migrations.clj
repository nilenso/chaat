(ns chaat.migrations
  (:require [ragtime.jdbc :as ragtime.jdbc]
            [ragtime.repl :as repl]
            [chaat.config :as config]))

;; changed to def from defn
(def config {:datastore (ragtime.jdbc/sql-database (:pg-db config/app-config))
             :migrations (ragtime.jdbc/load-resources "migrations")})

(repl/rollback config)
(repl/migrate config)

(comment
  {:connection-uri
   "jdbc:postgresql://127.0.0.1:8001/chaat-db?user=chaat-dev&password=password"})
