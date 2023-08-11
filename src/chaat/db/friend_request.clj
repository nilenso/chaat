(ns chaat.db.friend-request
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [chaat.handler.errors :refer [error-table]]
   [chaat.db.user :as user]
   [java-time.api :as jt]))

;; Will be more logical to have the user-exists? and exists? check in the model layer.
;; Same for the friend request exists? check
(defn exists?
  "Check if friend request id exists in friend_requests table"
  [connection id]
  (not (empty? (sql/find-by-keys connection :friend_requests {:id id}))))

(defn insert
  "Insert a friend request into the db"
  [db {:keys [sender_username recipient_username] :as content}]
  (jdbc/with-transaction [tx (db)]
    (if (and (user/user-exists? tx sender_username)
             (user/user-exists? tx recipient_username))
      (let [query-result (sql/insert! tx :friend_requests content)]
        {:result query-result :error nil})
      {:result nil :error (:username-not-exists error-table)})))

(defn accept
  [db id]
  (jdbc/with-transaction [tx (db)]
    (if (exists? tx id)
      (let [query-result (sql/update! (db) :friend_requests {:request_state "accepted"} {:id id})
            update-count (:next.jdbc/update-count query-result)]
        (if (= 1 update-count)
          {:result id :error nil}
          {:result nil :error (:db-state-error error-table)}))
      {:result nil :error (:friend-request-not-exists error-table)})))

(defn reject
  [db id]
  (jdbc/with-transaction [tx (db)]
    (if (exists? tx id)
      (let [query-result (sql/update! (db) :friend_requests {:request_state "rejected"} {:id id})
            update-count (:next.jdbc/update-count query-result)]
        (if (= 1 update-count)
          {:result id :error nil}
          {:result nil :error (:db-state-error error-table)}))
      {:result nil :error (:friend-request-not-exists error-table)})))

(comment
  (def db (:db chaat.app/chaat-system))
  (chaat.model.user/create db "shahn" "12345678")
  (chaat.model.user/create db "neena" "12345678")
  (def sample {:sender_username "shahn"
               :recipient_username "neena"
               :request_state "pending"
               :msg "hola"
               :creation_timestamp (jt/instant)})
  (insert db sample)
  (sql/update! (db) :friend_requests {:request_state "accepted"} {:id 0})
  (accept db 1)
  (reject db 1))  
