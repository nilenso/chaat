(ns chaat.handler.validation
  (:require [chaat.errors :refer [do-or-error]]))

;; handler level validation needs to be improved
;; consider using validation library like schema to check shape: types and structure

(defn validate-username
  [username]
  (if (not-empty username)
    {:result username :error nil}
    {:result nil :error "Please enter username"}))

(defn validate-password
  [password]
  (if (not-empty password)
    {:result password :error nil}
    {:result nil :error "Please enter password"}))

(defn validate-signup-details
  [username password]
  (let [result (validate-username username)
        result (do-or-error result validate-password password)]
    result))
