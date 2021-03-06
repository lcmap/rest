(ns lcmap.rest.api.v0.jobs.ccdc
  (:require [ring.util.response :as ring]
            [compojure.core :refer [GET HEAD POST PUT context defroutes]]
            [lcmap.client.jobs.ccdc]
            [lcmap.client.status-codes :as status]
            [lcmap.rest.components.httpd :as httpd]
            [lcmap.see.job.db :as db]))

;;; Supporting Constants ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def result-table "XXX")

;;; Supporting Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-result-path
  [result-id]
  (format "%s/%s"
          lcmap.client.jobs.ccdc/context
          result-id))

(defn get-job-resources [request]
  "job resources tbd")

(defn create-job [arg1 arg2]
  "job creation tbd")

(defn get-job-status [job-id]
  "job status tbd")

(defn get-job-result
  ([component job-id]
    (get-job-result (db/get-conn component) job-id result-table #'get-job-status))
  ([db-conn job-id table func]
    (-> (db/get-job-result db-conn job-id table func)
        (ring/response)
        (ring/status status/ok))))

(defn update-job [job-id]
  "job update tbd")

(defn get-info [job-id]
  "job info tbd")

;;; Routes ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes routes
  (context lcmap.client.jobs.ccdc/context []
    (GET "/" request
      (get-job-resources (:uri request)))
    (GET "/:job-id" [job-id :as request]
      (get-job-result (:component request) job-id))
    (PUT "/:job-id" [job-id :as request]
      (update-job job-id))
    (HEAD "/:job-id" [job-id :as request]
      (get-info job-id))
    (GET "/status/:job-id" [job-id :as request]
      (get-job-status job-id))))

;;; Exception Handling ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; XXX TBD
