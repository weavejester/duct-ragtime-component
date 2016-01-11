(defproject duct/ragtime-component "0.1.3"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.stuartsierra/component "0.3.0"]
                 [ragtime "0.5.2"]]
  :profiles
  {:dev {:dependencies [[com.h2database/h2 "1.3.160"]
                        [org.clojure/java.jdbc "0.3.7"]]}})
