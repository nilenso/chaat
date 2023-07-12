(ns chaat.routes
  (:require [chaat.handler.handler :as handler]))

(defn routes-fn [db]
  ["/" [["" {:get handler/home}]
        ["health-check" {:get handler/health-check}]
        ["users" {:post #(handler/signup db %)
                  :delete #(handler/delete-user db %)}]
        ["test-page" {:get handler/test-page}]
        [true handler/not-found]]])
