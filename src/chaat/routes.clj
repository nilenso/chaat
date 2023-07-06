(ns chaat.routes
  (:require [chaat.handler.handler :as handler]))

(def routes ["/" [["" {:get handler/home}]
                  ["health-check" {:get handler/health-check}]
                  ["users" {:post handler/signup
                            :delete handler/delete-user}]
                  ["test-page" {:get handler/test-page}]
                  [true handler/not-found]]])
