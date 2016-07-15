(ns lcmap.rest.api.auth
  (:import [java.lang Runtime])
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [GET HEAD POST PUT context defroutes]]
            [ring.util.response :as ring-resp]
            [lcmap.client.auth]
            [lcmap.client.status-codes :as status]
            [lcmap.rest.auth.usgs :as usgs]
            [lcmap.rest.errors :as errors]
            [lcmap.rest.middleware.http-util :as http]))

;;; Supporting Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;; API Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-resources [context]
  (log/info (str "get-resources: " context))
  {:links (map #(str context %) ["login" "logout"])})

;;; Routes ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes routes
  (context lcmap.client.auth/context []
    (GET "/" request
         (->> (get-resources (:uri request))
              (ring-resp/response)))
    (POST "/login" [username password :as request]
          (->> (usgs/login (:component request) username password)
               (ring-resp/response)))
    (POST "/logout" [token :as request]
          (->> (usgs/logout (:component request) token)
               (ring-resp/response)))))

;;; Exception Handling ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(http/add-error-handler
  #'usgs/login
  java.net.ConnectException
  errors/no-auth-conn
  status/bad-gateway)

;; If we want to use our own exceptions, we can catch those by checking the
;; key we used to define our error types (see lcmap.rest.exceptions).

(http/add-error-handler
  #'usgs/login
  [:type 'Auth-Error]
  errors/bad-creds
  status/unauthorized)

;; HTTP error status codes returned as exceptions from clj-http

(http/add-error-handler
  #'usgs/login
  [:status status/server-error]
  errors/auth-server-error
  status/server-error)

(http/add-error-handler
  #'usgs/login
  [:status status/unauthorized]
  errors/bad-creds
  status/unauthorized)

(http/add-error-handler
  #'usgs/login
  [:status status/no-resource]
  errors/auth-not-found
  status/no-resource)
