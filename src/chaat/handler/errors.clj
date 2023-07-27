(ns chaat.handler.errors)

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
                         :status-code 404}
   :health-check-error {:msg "Health check error"
                        :status-code 500}
   :unauthorized-action {:msg "Unauthorized action"
                         :status-code 401}})
