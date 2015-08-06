(ns duct.component.ragtime
  (:require [com.stuartsierra.component :as component]
            [ragtime.core :as core]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [ragtime.strategy :as strategy]))

(defrecord Ragtime [resource-path]
  component/Lifecycle
  (start [component]
    (assoc component
           :datastore  (-> component :db :spec jdbc/sql-database)
           :migrations (jdbc/load-resources resource-path)))
  (stop [component]
    (dissoc component :datastore :migrations)))

(def default-options
  {:strategy strategy/raise-error
   :reporter repl/default-reporter})

(defn ragtime [options]
  (map->Ragtime (merge default-options options)))

(defn migrate [{:keys [datastore migrations strategy reporter]}]
  (let [migrations (map #(repl/wrap-reporting % reporter) migrations)]
    (core/migrate-all datastore {} migrations strategy)))

(defn rollback [{:keys [datastore migrations reporter]}]
  (let [migrations (map #(repl/wrap-reporting % reporter) migrations)]
    (core/rollback-last datastore (core/into-index migrations) 1)))
