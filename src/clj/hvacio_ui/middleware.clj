(ns hvacio-ui.middleware
  (:use compojure.core)
  (:require [compojure.route :as route]))

(defn wrap-hvacio-ui-resources
  "Add hvacio-ui resources to the handler."
  [handler]
  (routes
   (route/resources "/hvacio-ui" {:root "hvacio-ui/public"})
   handler))
