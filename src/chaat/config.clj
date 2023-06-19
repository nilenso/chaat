(ns chaat.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (java.io PushbackReader)))

(def app-config (with-open [f (io/reader "./resources/config.edn")]
                  (->> f
                       (PushbackReader.)
                       (edn/read))))

;; defonce used to keep state between reloads. perhaps not necessary here.
(defonce pg-db (:pg-db app-config))
(def default-timezone (:default-timezone (:app app-config)))
(def static-salt (:static-salt (:app app-config)))

(comment
  (edn/read (PushbackReader. (io/reader "./resources/config.edn")))
  (new PushbackReader (io/reader "./resources/config.edn")))
