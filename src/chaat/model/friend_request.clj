(ns chaat.model.friend-request
  (:require
   [chaat.db.friend-request :as db.friend-request]
   [java-time.api :as jt]))


(defn gen-friend-request
  [{:keys [username recipient message]}]
  {:sender_username username
   :recipient_username recipient
   :request_state "pending"
   :msg message
   :creation_timestamp (jt/instant)})

(defn create
  [db content]
  (let [;; put model layer validation
        friend-request (gen-friend-request content)
        result (db.friend-request/insert db friend-request)]
    result))

(defn accept
  [db id]
  (let [;; put model layer validation
        result (db.friend-request/accept db id)]
    result))

(defn reject
  [db id]
  (let [;; put model layer validation
        result (db.friend-request/reject db id)]
    result))

(comment
  (def db (:db chaat.app/chaat-system))
  (def content {:username "shahn" :recipient "neena" :message "hello"})
  (create db content))