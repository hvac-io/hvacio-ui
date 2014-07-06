(ns hvacio-ui.page
  (:use hiccup.page))

(defn include-hvacio-ui
  "Add hvacio-ui CSS and JS resources to the page."
  []
  (list
   (include-css "/hvacio-ui/css/nprogress.css")
   (include-js  "/hvacio-ui/js/nprogress.js")))
