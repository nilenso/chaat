(ns chaat.app
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.adapter.jetty :as jetty]
            [chaat.routes :as routes]
            [bidi.ring :refer [make-handler]]
            [chaat.migrations :as migrations]
            [chaat.config :as config]
            [next.jdbc.connection :as conn]
            [com.stuartsierra.component :as component]
            [ragtime.core])
  (:import (com.zaxxer.hikari HikariDataSource))
  (:gen-class))

;; handler + middleware
(defn make-app [db]
  (-> (routes/routes-fn db)
      make-handler
      wrap-keyword-params
      wrap-json-params
      wrap-params
      wrap-reload))

;; defining HttpServer component
(defrecord HttpServer [config db]
  component/Lifecycle
  (start [component]
    (if (:server component)
      component
      (let [options (:options config)
            server (jetty/run-jetty (make-app db) options)]
        (assoc component :server server))))
  (stop [component]
    (if-let [server (:server component)]
      (do (.stop server)
          (.join server)
          (dissoc component :server))
      component)))

(defn new-http-server [config]
  (map->HttpServer {:config config}))

(defn new-system [config]
  (component/system-map
   :server (component/using (new-http-server config) {:db :db})
   :db (conn/component HikariDataSource (:pg-dbspec config))))

(def chaat-system-config {:options {:port (config/get-local-port)
                                    :join? false}
                          :pg-dbspec (config/get-pg-dbspec)})

(def chaat-system (new-system chaat-system-config))

(defn start []
  ;; can add logging and other setup
  ;; (component/start chaat-system)
  (alter-var-root #'chaat-system component/start))

(defn stop []
  ;; can add other cleanup 
  (alter-var-root #'chaat-system component/stop))

(defn restart []
  (stop)
  (start))

(defn -main
  [& args]
  (start))
