(ns chaat.errors)

(defn do-or-error
  "Checks the result of a previous operation before performing the next 
   operation. Will be used in a let block. If there is an error, the error will 
   cascade down the remaining expressions in the let block without performing 
   the remaining operations.

   In situations where function f does not return a result map, it is usually 
   when the operation is not a validation or db query. Here, one may not be 
   concerned about checking that particular output of f before continuing, and 
   we use the result map of the previous validation/query to continue the chain."
  [{:keys [result error]} f & args]
  (if (nil? error)
    (apply f args)
    {:result result :error error}))

;; consider creating standard error messages. maybe in a map structure.
;; (def invalid-username "Wrong username format")
;; (def invalid-password "Wrong password format")
