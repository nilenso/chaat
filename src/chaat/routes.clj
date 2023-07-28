(ns chaat.routes
  (:require [chaat.handler.handler :as handler]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [chaat.config :as config]))

(def backend (backends/jws {:secret (config/get-secret)
                            :token-name "Bearer"}))

(defn routes-fn [db]
  ["/" [["" {:get handler/home}]
        ["health-check" {:get #(handler/health-check db %)}]
        ["users" {:post #(handler/signup db %)
                  :delete (wrap-authentication #(handler/delete-user db %) backend)}]
        ["login" {:post #(handler/login db %)}]
        ["friend-requests/" {:post {"" (wrap-authentication #(handler/create-friend-request db %) backend)
                                    [:id "/accept"] (wrap-authentication #(handler/accept-friend-request db %) backend)
                                    [:id "/reject"] (wrap-authentication #(handler/reject-friend-request db %) backend)}}]
        ["test-page" {:get handler/test-page}]
        [true handler/not-found]]])
