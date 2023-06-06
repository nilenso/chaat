(ns chaat.app
  (:require [chaat.handler :refer [handler]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :as jetty]
            [clojure.pprint :as pprint])
  (:gen-class))

(def app
  (-> #'handler
      wrap-reload))

;; use future/delay/atom

(defonce server (atom app))
;; (defonce server (atom (jetty/run-jetty app {:port 3000
;;                                         :join? false})))

(defn start-server
  [val]
  (jetty/run-jetty app {:port 3000
                        :join? false}))

(defn -main
  "i am main"
  [& args]
  (swap! server start-server))

  ;; (defonce server (jetty/run-jetty app {:port 3000
  ;; :join? false})))
;; using def in a function is not good, will fix this

;; (.stop server)
;; (.start server)
