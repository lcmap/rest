(ns lcmap.rest.util
  (:require [clojure.string :as string]
            [digest])
  (:import [java.security.MessageDigest]
           [java.math.BigInteger]))

(defn serialize [args]
  (cond (list? args)
          (string/join (sort (map #'str args)))
        (map? args)
          (str (into (sorted-map) args))
        :else
          (str args)))

(defn add-shutdown-handler [func]
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. func)))

(defn in?
  "This function returns true if the provided seqenuce contains the given
  elment."
  [sequence elm]
  (some #(= elm %) sequence))


(defn make-bool
  ""
  [input]
  (case input
    0 false
    "0" false
    false false
    "false" false
    :false false
    nil false
    "nil" false
    :nil false
    true))

(defn make-flag
  "There are three cases we want to handle for command line options:
  * a flag that takes a value
  * a flag which should be passed, since a value was given
  * a flag which should not be passed, since no value was given"
  [flag value & {:keys [unary?] :or {unary? false}}]
  (cond
    unary? (when (make-bool value) flag)
    (nil? value) nil
    :else (format "%s %s" flag value)))

(defn get-local-ip
  "Get the IP address of the local machine."
  []
  (.getHostAddress (java.net.InetAddress/getLocalHost)))
