(ns chaat.handler
  (:require [bidi.ring :refer (make-handler)]
            [ring.util.response :as res]
            [clj-time.local :as l]))

(defn home-handler
  [request]
  (res/response "Welcome to chaat"))

(defn health-check-handler
  [request]
  (->> (l/local-now)
       (str "Service is up and running: ")
       res/response
       str
       res/response))
;; I wanted to see display the response contents

(defn test-page-handler
  [request]
  (res/response (str request)))

(def handler
  (make-handler ["/" {"" home-handler
                      "health-check" health-check-handler
                      "test-page" test-page-handler}]))
