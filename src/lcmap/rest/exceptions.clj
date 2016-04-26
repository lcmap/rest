(ns lcmap.rest.exceptions)

(defn error
  ([data]
    (ex-info (format "%s: %s" (:error-type data) (:msg data)) data))
  ([type msg]
    (error {:error-type type :msg msg})))

(defn lcmap-error [msg]
  (error 'LCMAP-Error msg))

(defn auth-error [msg]
  (error 'Auth-Error msg))

(defn type-error [ & {:keys [msg schema-msg sub-type input-schema input-value
                             trace]
                      :as data}]
  (error (assoc data :error-type 'Type-Error)))
