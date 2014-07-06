(ns hvacio-ui.test-api
  (:require [compojure.core :as comp]
            [liberator.core :as lib]
            [clojure.edn :as edn]))

(def test-api-values
  (-> (slurp "api-test-values.edn")
      (edn/read-string)))

(lib/defresource devices-list [project-id]
  :available-media-types ["application/edn"]
  :handle-ok (fn [_] (:devices-list test-api-values))
  :handle-not-found "Resource not found")
;; [["10100" "System A1"] ["10200" "System A2"]]


(lib/defresource objects [project-id device-id]
  :available-media-types ["application/edn"]
  :handle-ok (fn [_] (:objects test-api-values))
  :handle-not-found "Resource not found")
;; [{value 24.5 :description "" :object-instance "1" :object-type "12" :object-id "12.1" ...}
;;  {value 33.1 :description "" :object-instance "2" :object-type "12" :object-id "12.2"...}]

(lib/defresource device-summary [project-id device-id]
  :available-media-types ["application/edn"]
  :handle-ok (fn [_] (:device-summary test-api-values))
  :handle-not-found "Resource not found")
;; {:objects ["12.1" "12.2"...] :scan-duration 5000 :update 1399919564512 :name "System A1"}


(comp/defroutes api-routes
  
  (comp/GET "/api/v1/devices-list/:project-id" [project-id] 
    (devices-list project-id))

  (comp/GET "/api/v1/objects/:project-id/:device-id" [project-id device-id] 
    (objects project-id device-id))

  (comp/GET "/api/v1/device-summary/:project-id/:device-id" [project-id device-id] 
    (device-summary project-id device-id))

)
