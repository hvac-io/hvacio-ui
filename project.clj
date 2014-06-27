(defproject hvacio/hvacio-ui "0.1.2"
  :description "A common UI for multiple HVAC.IO applications."
  :url "https://hvac.io"
  :license {:name "GNU General Public License V3"
            :url "http://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]

                 ;; cljs
                 [reagent "0.4.2"]
                 [org.clojars.frozenlock/query "0.2.3"]
                 [historian "1.0.1"]
                 [cljs-ajax "0.2.3"]
                 
                 ;; internationalization
                 [com.taoensso/tower "2.1.0-RC1"]]

  :profiles {:dev {:dependencies [[ring "1.2.1"]
                                  [compojure "1.1.6"]
                                  
                                  ;; test API
                                  [liberator "0.11.0"]

                                  [org.clojure/clojurescript "0.0-2173" :scope "provided"]]

                   :main hvacio-ui.server
                   :ring {:handler hvacio-ui.server/app}
                   :source-paths ["src/clj"]
                   }}

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
                              ;:jar true ;---> no need for it, source-paths already configured
}
                       :dev {:source-paths ["src/cljs" 
                                            "src-dev/cljs"]
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

