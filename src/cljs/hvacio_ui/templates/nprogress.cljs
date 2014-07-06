(ns hvacio-ui.templates.nprogress)

(defn start []
  (.start js/NProgress))

(defn done []
  (.done js/NProgress))
