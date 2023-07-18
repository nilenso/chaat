(ns chaat.db.utils
  (:require
   [next.jdbc.sql :as sql]))

(defn health-check
  "Check db status"
  [db]
  (try
    (let [query-result (sql/query (db) ["SELECT 1"])]
      {:result query-result :error nil})
    (catch Exception e
      {:result nil :error (str "Database exception:" e)})))
