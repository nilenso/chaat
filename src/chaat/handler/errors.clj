(ns chaat.handler.errors)

;; Common error codes:
;; 400 bad request
;; 401 unauthorized
;; 403 forbidden
;; 404 not found
;; 500 internal error

(def error-table
  {:username-empty {:msg "Username not present"
                    :status-code 400}
   :password-empty {:msg "Password not present"
                    :status-code 400}
   :username-format {:msg "Wrong username format"
                     :status-code 400}
   :password-format {:msg "Wrong password format"
                     :status-code 400}
   :username-error {:msg "Username or password is incorrect"
                    :status-code 400}
   :password-error {:msg "Username or password is incorrect"
                    :status-code 400}
   :username-exists {:msg "Username already exists"
                     :status-code 409}
   :username-not-exists {:msg "Username does not exist"
                         :status-code 409}
   :health-check-error {:msg "Health check error"
                        :status-code 500}})