(ns chaat.app
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :as jetty]
            [chaat.routes :as routes]
            [bidi.ring :refer [make-handler]])
  (:gen-class))

(def handler
  (make-handler routes/routes))

;; why does this only work with a var?
;; look into how clojure reloads namespaces
(def app
  (-> #'handler
      wrap-reload))

(defonce server (atom nil))

(defn start-server
  [val]
  (jetty/run-jetty app {:port 3000
                        :join? false}))

(defn -main
  "i am main"
  [& args]
  (swap! server start-server))

;; (.stop server)
;; (.start server)
