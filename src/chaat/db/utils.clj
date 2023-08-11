(ns chaat.db.utils
  (:require
   [next.jdbc.sql :as sql]))

;; Failure case is an exception which ring will catch and return status 500
(defn health-check
  "Check db status"
  [db]
  (let [query-result (sql/query (db) ["SELECT 1"])]
    {:result query-result :error nil}))
