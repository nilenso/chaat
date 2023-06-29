(ns chaat.handler
  (:require [ring.util.response :as res]
            [chaat.model.user :as user]
            [java-time.api :as jt]))

;; Missing parameter and null check should be done in the controller layer
;; Make handler folder with user.clj when this grows.

(defn home
  [request]
  (res/response "Welcome to chaat"))

(defn health-check
  [request]
  (->> (str "Service is running: " (jt/local-time))
       res/response))

;; Move these functions to different namespaces: helper/validation/builder

(defn min-length?
  [str threshold]
  (>= (count str) threshold))

;; add more restrictions: special characters not allowed, only alphanumeric
(defn validate-username
  [username]
  (let [min-length 2]
    (min-length? username min-length)))

;; pertains to the representation of a user, could move this to the model layer
;; add more restrictions: one uppercase and one special character required
(defn validate-password
  [password]
  (let [min-length 8]
    (min-length? password min-length)))

;; status codes: handler layer concern
;; put in builder namespace?
(defn build-result-map
  [success message description]
  {:success success
   :message message
   :description description})

;; writing build-result-map too many times, figure out how to fix this
;; collect parameters and apply once to build-result-map
;; (->> `(true nil nil)
;;      (apply build-result-map))
(defn validate-signup-details
  [username password]
  (if (validate-username username)
    (if (validate-password password)
      (build-result-map true "Valid username and password" nil)
      (build-result-map false "Password error" :password))
    (build-result-map false "Username error" :username)))

(defn send-response
  "Send a good/bad response based on :success key in result-map"
  [result-map]
  (let [{:keys [success message]} result-map]
    (if success
      (res/response message)
      (res/bad-request message))))

;; check that required parameters exist, and are not nil/null
(defn signup
  [request]
  (let [params (:params request)
        {:keys [username password]} params
        validation-result (validate-signup-details username password)]
    (if (:success validation-result)
      (->> (user/create-user username password)
           (send-response))
      (send-response validation-result))))

(defn delete-account
  [request]
  (let [params (:params request)
        username (:user params)]
    (if (validate-username username)
      (->> (user/delete-user username)
           (send-response))
      (res/bad-request "Invalid username format"))))

(defn test-page
  [request]
  (res/response (str request)))

(defn not-found
  [request]
  (res/not-found "Resource does not exist"))
