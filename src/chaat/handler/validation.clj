(ns chaat.handler.validation)

;; consider using validation library like schema to check shape: types and structure

(defn validate-username
  [username]
  (if (not-empty username)
    [username nil]
    [nil "Please enter username"]))

(defn validate-password
  [password]
  (if (not-empty password)
    [password nil]
    [nil "Please enter password"]))

(defn validate-signup-details
  [params]
  (let [{:keys [username password]} params
        [param err] (validate-username username)
        [param err] (if (nil? err) (validate-password password) [nil err])]
    [params err]))
