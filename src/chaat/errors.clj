(ns chaat.errors)

(defn do-or-error
  " Checks the result of a previous operation before performing the next
operation. Will be used in a let block. If there is an error, the error will
cascade down the remaining expressions in the let block without performing
the remaining operations.

In situations where function f does not return a result map, it is usually
when the operation is not a validation or db query. Here, one may not be
concerned about checking that particular output of f before continuing, and
we use the result map of the previous validation/query to continue the chain. "
  [{:keys [result error]} f & args]
  (if-not error
    (apply f args)
    {:result result :error error}))

;; work-in-progress macro to improve do-or-error flow
;; next step: allow :result to be placed anywhere in args. currently can only be 
;; place as the first arg.
;; how: find position of :result in args and insert result at that location in args
;; and then apply args to f
(defn do-result-or-error
  " Same as above but allows for the placement of an optional :result key
to pass the result value from the map returned by the previous expression "
  [{:keys [result error]} f & args]
  (if-not error
    (if (= (first args) :result)
      (apply f result (rest args))
      (apply f args))
    {:result result :error error}))

(defmacro until-err-> [val & fns]
  (let [fns (for [f fns] `(do-result-or-error ~@f))]
    `(-> ~val
         ~@fns)))

;; (defmacro until-err-> [val & fns]
;;   (let [fns (for [f fns] `(do-result-or-error ~@f))]
;;     `(-> {:result ~val :error nil}
;;          ~@fns)))

;; consider creating standard error messages. maybe in a map structure.
;; (def invalid-username " Wrong username format ")
;; (def invalid-password " Wrong password format ")"
