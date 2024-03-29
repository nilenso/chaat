(ns chaat.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (java.io PushbackReader)))

(def config (atom nil))

(defn reload-config []
  (with-open [f (io/reader "./resources/config.edn")]
    (->> f
         (PushbackReader.)
         (edn/read)
         (reset! config))))

(defn get-config []
  (if @config
    @config
    (reload-config)))

(defn get-pg-dbspec []
  (:pg-dbspec (get-config)))

(defn get-pg-test-dbspec []
  (:pg-test-dbspec (get-config)))

(defn get-local-port []
  (get-in (get-config) (:app :local-port)))

(defn get-local-test-port []
  (get-in (get-config) [:app :local-test-port]))

(defn get-secret []
  (get-in (get-config) [:app :secret]))

(comment
  (edn/read (PushbackReader. (io/reader "./resources/config.edn")))
  (edn/read (new PushbackReader (io/reader "./resources/config.edn"))))
