(ns chaat.fixture
  (:require [chaat.config :as config]
            [com.stuartsierra.component :as component]
            [next.jdbc.connection :as conn]
            [chaat.app :as app]
            [ragtime.jdbc]
            [ragtime.repl]
            [chaat.migrations :as migrations])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn new-test-system [config]
  (component/system-map
   :server (component/using (app/new-http-server config) {:db :db})
   :db (conn/component HikariDataSource (:pg-test-dbspec config))))

(def test-system-config {:options {:port (config/get-local-test-port)
                                   :join? false}
                         :pg-test-dbspec (config/get-pg-test-dbspec)})

(def test-system (new-test-system test-system-config))

(defn start-test-system []
  (alter-var-root #'test-system component/start))

(defn stop-test-system []
  (alter-var-root #'test-system component/stop))

;; using a full system for all tests now. this needs to be optimized.
;; fixture with test-system (server + db) and migrations run/rollback

(defn test-fixture [tests]
  (stop-test-system)
  (start-test-system)
  (migrations/rollback (config/get-pg-test-dbspec))
  (migrations/run-migrations (config/get-pg-test-dbspec))
  (tests)
  (migrations/rollback (config/get-pg-test-dbspec))
  (stop-test-system))
