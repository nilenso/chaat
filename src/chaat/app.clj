(ns chaat.app
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.adapter.jetty :as jetty]
            [chaat.routes :as routes]
            [bidi.ring :refer [make-handler]])
  (:gen-class))

(def handler
  (make-handler routes/routes))

(def app
  (-> #'handler
      wrap-params
      wrap-keyword-params
      wrap-json-params
      wrap-reload))

(defonce server (atom nil))

(defn start-server
  [val]
  (jetty/run-jetty app {:port 3000
                        :join? false}))

(defn -main
  [& args]
  (swap! server start-server))

(comment
  (str "Use these cmds to start and stop server from repl.")
  (.start @server)
  (.stop @server))
