(ns lcmap.rest.api.auth
  (:import [java.lang Runtime])
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [GET HEAD POST PUT context defroutes]]
            [dire.core :refer [with-handler!]]
            [ring.util.response :as ring]
            [lcmap.client.auth]
            [lcmap.client.status-codes :as status]
            [lcmap.rest.auth.usgs :as usgs]
            [lcmap.rest.components.httpd :as httpd]
            [lcmap.rest.middleware.http-util :as http]))

;;; Supporting Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; XXX add db-connection as parameter
(defn login [auth-cfg username password]
  (http/response
    {:result (usgs/login auth-cfg username password)
      :errors []}))

(defn logout [auth-cfg db-conn token]
  (ring/response
    (usgs/logout auth-cfg db-conn token)))

;;; Routes ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes routes
  (context lcmap.client.auth/context []
    (POST "/login" [username password :as request]
      (login (httpd/authcfg-key request) username password))
    ;; XXX once we've got user data being saved in the db, we need to come
    ;; back to this and add a logout function which destroys the ephemeral
    ;; user data (such as token association)
    ))

;;; Exception Handling ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(with-handler! #'login
  java.net.ConnectException
  (fn [e & args]
    (log/error "Cannot connect to remote server")
    (http/response :errors [e])))

;; If we want to use our own exceptions, we can catch those in the following
;; manner:
(with-handler! #'login
  [:error-type 'Auth-Error]
  (fn [e & args]
    (log/error e)
    (http/response :errors [e])))

;; HTTP error status codes returned as exceptions from clj-http
(with-handler! #'login
  [:status status/server-error]
  (fn [e & args]
    (log/error "Authentication server error")
    (http/response :errors [e])))

(with-handler! #'login
  [:status status/forbidden]
  (fn [e & args]
    (log/error "Bad username or password")
    (http/response :errors [e])))

(with-handler! #'login
  [:status status/no-resource]
  (fn [e & args]
    (log/error "Authentication resource not found")
    (http/response :errors [e])))
