(ns chaat.app
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.adapter.jetty :as jetty]
            [chaat.routes :as routes]
            [bidi.ring :refer [make-handler]]
            [chaat.migrations :as migrations]
            [chaat.config :as config])
  (:gen-class))

(def handler
  (make-handler routes/routes))

(def app
  (-> #'handler
      wrap-keyword-params
      wrap-json-params
      wrap-params
      wrap-reload))

;; look into making this server a component
(defonce server (atom nil))

(defn start-server
  [val]
  (jetty/run-jetty app {:port (config/get-local-port)
                        :join? false}))

;; start/stop flow can be taken over by component later
(defn start []
  ;; can add logging and other setup
  (migrations/run-migrations)
  (swap! server start-server))

(defn stop []
  ;; can add other cleanup
  (.stop @server))

;; for repl-based development
(defn restart []
  (stop)
  (start))

(defn -main
  [& args]
  (start))

(comment
  (str "Use these cmds to start and stop server from repl.")
  (.start @server)
  (.stop @server))
