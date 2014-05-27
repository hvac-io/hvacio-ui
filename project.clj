(defproject hvacio/hvacio-ui "0.1.0-SNAPSHOT"
  :description "A common UI for multiple HVAC.IO applications."
  :url "https://hvac.io"
  :license {:name "GNU General Public License V3"
            :url "http://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2173" :scope "provided"]

                 ;; cljs
                 [reagent "0.4.2"]
                 [org.clojars.frozenlock/query "0.2.3"]
                 [historian "1.0.1"]
                 ;[org.clojars.franks42/cljs-uuid-utils "0.1.3"]
                 [cljs-ajax "0.2.3"]
                 
                 ;; internationalization
                 [com.taoensso/tower "2.1.0-RC1"]
                 ]

  :profiles {:dev {:dependencies [[ring "1.2.1"]
                                  [compojure "1.1.6"]
                                  
                                  ;; for the cljs REPL
                                  [com.cemerick/piggieback "0.1.3"]
                                  
                                  ;; test API
                                  [liberator "0.11.0"]

                                  ;;cljs
                                        ; [alandipert/storage-atom "1.2.3"]
                                  ;[org.clojars.franks42/cljs-uuid-utils "0.1.3"]
                                  ]

                   :main hvacio-ui.server
                   :ring {:handler hvacio-ui.server/app}
                   :source-paths ["src/clj"]
                   ;; :injections [(ns user)
                   ;;              (require '[cljs.repl.browser :as brepl]
                   ;;                       '[cemerick.piggieback :as pb])
                   ;;              (defn browser-repl []
                   ;;                (pb/cljs-repl :repl-env
                   ;;                              (brepl/repl-env :port 9000)))]
                   ;; :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
             }}

;  :resource-paths ["resources/dict"]

  :plugins [[lein-cljsbuild "1.0.2"]
            [lein-ring "0.8.10"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/cljs"] ;; don't include the ring/server code
  :cljsbuild { 
              :builds {
                       :main {
                              :source-paths ["src/cljs"]
                              :compiler {:output-to "resources/public/js/cljs.js"
                                         :optimizations :simple
                                         :pretty-print true
                                         :preamble ["reagent/react.min.js"]}
                              ;:jar true
}
                       :dev {:source-paths ["src/cljs" "src-dev/cljs"]
                             :compiler {:output-to "resources/public/js/cljs.js"
                                        :optimizations :whitespace
                                        :pretty-print true
                                        :preamble ["reagent/react.js"]}}
                       :test {:source-paths ["src/cljs" "test/cljs"]
                              :compiler {:output-to "resources/public/js/cljs.js"
                                         :optimizations :whitespace
                                         :pretty-print true
                                         :preamble ["reagent/react.js"]}}
                       }}
  :aliases {"browser-test" ["do" ["cljsbuild" "clean"] ["cljsbuild" "once" "test"] ["ring" "server" "3001"]]
            "cljsbuild-dev" ["do" ["cljsbuild" "clean"] ["cljsbuild" "auto" "dev"]]})

