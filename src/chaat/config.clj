(ns chaat.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (java.io PushbackReader)))

;; change defn's -> def's, not much of an advantage here

(defn get-app-config []
  (with-open [f (io/reader "./resources/config.edn")]
    (->> f
         (PushbackReader.)
         (edn/read))))

(defn get-pg-dbspec []
  (:pg-dbspec (get-app-config)))

(defn get-pg-test-dbspec []
  (:pg-test-dbspec (get-app-config)))

(defn get-local-port []
  (:local-port (:app (get-app-config))))

(defn get-local-test-port []
  (:local-test-port (:app (get-app-config))))

(comment
  (edn/read (PushbackReader. (io/reader "./resources/config.edn")))
  (new PushbackReader (io/reader "./resources/config.edn")))
