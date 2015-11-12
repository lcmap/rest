;;;; Event LCMAP REST Service system component
;;;; For more information, see the module-level code comments in
;;;; lcmap-rest.components.
(ns lcmap-rest.components.config
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [lcmap-rest.util :as util]))

(defrecord Configuration []
  component/Lifecycle

  (start [component]
    (log/info "Setting up LCMAP configuration ...")
    (let [cfg (util/get-config)]
      (log/info "Using lein profile:" (:active-profile cfg))
      (log/debug "Successfully generated LCMAP configuration.")
      cfg))

  (stop [component]
    (log/info "Tearing down LCMAP configuration ...")
    (log/debug "Component keys" (keys component))
    (assoc component :cfg nil)))

(defn new-configuration []
  (->Configuration))