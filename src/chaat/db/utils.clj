(ns chaat.db.utils
  (:require
   [next.jdbc.sql :as sql]))

;; no try-catch here, ring will catch the exception and return status 500
(defn health-check
  "Check db status"
  [db]
  (let [query-result (sql/query (db) ["SELECT 1"])]
    {:result query-result :error nil}))
