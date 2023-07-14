(ns chaat.handler.validation
  (:require [chaat.errors :refer [do-or-error]]))

;; Handler layer does very basic validation: are parameters present/absent/nil. 
;; It doesn't pay attention to making things conform to a certain model (for example, user). 
;; It just makes sure that the data it needs to pass along is present.

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
