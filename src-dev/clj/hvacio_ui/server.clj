(ns hvacio-ui.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as resources]
            [compojure.core :as c]
            [hiccup.page :as h]
            [hiccup.util :as util]
            [hvacio-ui.test-api :as api]
            [hvacio-ui.middleware :as m]
            [hvacio-ui.page :as p]))

(defn render-app []
  (h/html5 {:lang "en"}
           [:head 
            [:title "UI"]
            (h/include-css "//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"
                           "//netdna.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css")
            (h/include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"
                          "//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js")

            (p/include-hvacio-ui)
            (h/include-js "js/cljs.js")]
           [:body ;{:style "background-color:grey;"}
            [:div#my-div "test div"]
            ;[:script {:src "js/cljs.js"}]
            [:script "hvacio_ui.client.run('fake-project-id', 'en')"]
            ]))

(c/defroutes app-routes
  (c/GET "/" req (render-app))
  api/api-routes)

(def app
  (-> app-routes
    (resources/wrap-resource "public")
    (m/wrap-hvacio-ui-resources)))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000}))
