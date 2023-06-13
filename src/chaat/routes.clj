(ns chaat.routes
  (:require [chaat.handler :as handler]))

(def routes ["/" {"" handler/home
                  "health-check" handler/health-check
                  "test-page" handler/test-page
                  true handler/not-found}])
