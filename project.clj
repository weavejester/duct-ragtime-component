(defproject duct/ragtime-component "0.1.4"
  :description "A component for managing migrations with Ragtime"
  :url "https://github.com/weavejester/duct-ragtime-component"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [ragtime "0.6.0"]]
  :profiles
  {:dev {:dependencies [[com.h2database/h2 "1.4.191"]
                        [org.clojure/java.jdbc "0.6.1"]]}})
