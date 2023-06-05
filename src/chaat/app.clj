(ns chaat.app
  (:require [chaat.handler :refer [handler]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :as jetty]
            [clojure.pprint :as pprint])
  (:gen-class))

(def app
  (-> #'handler
      wrap-reload))


(defn -main
  "i am main"
  [& args]
  (defonce server (jetty/run-jetty app {:port 8080
                                        :join? false})))
;; using def in a function is not good, will fix this

;; (.stop server)
;; (.start server)
