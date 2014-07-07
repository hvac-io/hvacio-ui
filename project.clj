(defproject hvacio/hvacio-ui "0.1.9"
  :description "A common UI for multiple HVAC.IO applications."
  :url "https://hvac.io"
  :license {:name "GNU General Public License V3"
            :url "http://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]


                 [compojure "1.1.8"]

                 ;; cljs
                 [reagent "0.4.2"]
                 [org.clojars.frozenlock/query "0.2.3"]
                 [historian "1.0.1"]
                 [cljs-ajax "0.2.3"]
                 
                 ;; internationalization
                 [com.taoensso/tower "2.1.0-RC1"]
                 
                 [org.clojure/clojurescript "0.0-2268" :scope "provided"]]

  :profiles {:dev {:dependencies [[ring "1.2.1"]
                                  [compojure "1.1.6"]
                                  
                                  ;; test API
                                  [liberator "0.11.0"]]

                   :main hvacio-ui.server
                   :ring {:handler hvacio-ui.server/app}
                   :source-paths ["src/clj" "src-dev/clj"]
                   }}

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ring "0.8.10"]]

  :source-paths ["src/clj" "src/cljs"]
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
                                         :pretty-print truez
                                         :preamble ["reagent/react.js"]}}
                       }}
  :aliases {"browser-test" ["do" ["cljsbuild" "clean"] ["cljsbuild" "once" "test"] ["ring" "server" "3001"]]
            "cljsbuild-dev" ["do" ["cljsbuild" "clean"] ["cljsbuild" "auto" "dev"]]})

