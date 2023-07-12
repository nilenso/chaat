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
            [com.stuartsierra.component :as component])
  (:gen-class))

;; handler + middleware
(defn app []
  (-> routes/routes
      make-handler
      wrap-keyword-params
      wrap-json-params
      wrap-params
      wrap-reload))

;; defining HttpServer component
(defrecord HttpServer [config]
  component/Lifecycle
  (start [component]
    (if (:server component)
      component
      (let [options (:options config)
            server (jetty/run-jetty (app) options)]
        (assoc component :server server))))
  (stop [component]
    (if-let [server (:server component)]
      (do (.stop server)
          (.join server)
          (dissoc component :server))
      component)))

;; constructor for HttpServer
(defn new-http-server [config]
  (map->HttpServer {:config config}))

(defn new-system [config]
  (component/system-map
   :server (new-http-server config)))

(def chaat-system-config {:options {:port (config/get-local-port)
                                    :join? false}})

(def chaat-system (new-system chaat-system-config))

(defn start []
  ;; can add logging and other setup
  ;; (migrations/run-migrations)
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
