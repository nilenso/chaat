(ns chaat.handler
  (:require [ring.util.response :as res]
            [clj-time.local :as l]))

(defn home
  [request]
  (res/response "Welcome to chaat"))

(defn health-check
  [request]
  (->> (str "Service is running: " (l/local-now))
       res/response))

(defn test-page
  [request]
  (res/response (str request)))

(defn not-found
  [request]
  (res/not-found "Resource does not exist"))

