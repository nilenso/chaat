(ns chaat.errors)

(defn do-or-error
  " Used to check the result of a previous operation before performing the next
operation. To be used in a let block. If there is an error, the error will
cascade down the remaining expressions in the let block without executing the
remaining operations.

fn f is required to return a result map with structure {:result res :error err}

In situations where function f does not return a result map, it is usually
when the operation is not a validation or db query. Here, you may not be
concerned about checking that particular output of f before continuing, and
we use the result map of the previous validation/query to continue the chain. "
  [{:keys [result error]} f & args]
  (if-not error
    (apply f args)
    {:result result :error error}))

;; This is a work-in-progress macro to improve the do-or-error flow.
;; You can place a :result key in the args lists to an expression to pass in the 
;; :result key value from the result map of the previous expression.

(defn do-result-or-error
  " Same as do-or-error but allows for the placement of an optional :result key
to pass the :result key value from the result map of the previous expression "
  [{:keys [result error]} f & args]
  (if-not error
    (apply f (map #(if (= % :result) result %) args))
    {:result result :error error}))

(defmacro until-err-> [val & fns]
  (let [fns (for [f fns] `(do-result-or-error ~@f))]
    `(-> ~val
         ~@fns)))

;; (defmacro until-err-> [val & fns]
;;   (let [fns (for [f fns] `(do-result-or-error ~@f))]
;;     `(-> {:result ~val :error nil}
;;          ~@fns)))"
