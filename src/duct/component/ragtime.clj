(ns duct.component.ragtime
  "A component for handling migrations on a SQL database."
  (:require [com.stuartsierra.component :as component]
            [ragtime.core :as core]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [ragtime.strategy :as strategy]))

(defn reload
  "Reload the migrations of a Ragtime component and return a new component."
  [{:keys [resource-path] :as component}]
  (assoc component :migrations (jdbc/load-resources resource-path)))

(defrecord Ragtime [resource-path]
  component/Lifecycle
  (start [component]
    (-> component
        (assoc :datastore (-> component :db :spec jdbc/sql-database))
        (reload)))
  (stop [component]
    (dissoc component :datastore :migrations)))

(def default-options
  {:strategy strategy/raise-error
   :reporter repl/default-reporter})

(defn ragtime
  "Create a Ragtime component for handling migrations. Expects a dependency :db
  that has a key :spec containing a clojure.java.jdbc compatible db-spec map.

  Takes the following options:

    :resource-path - the resource path to find migration files
    :strategy      - the Ragtime strategy
                     (defaults to ragtime.strategy/raise-error)
    :reporter      - the reporter function
                     (defaults to ragtime.repl/default-reporter)"
  [options]
  {:pre [(contains? options :resource-path)]}
  (map->Ragtime (merge default-options options)))

(defn migrate
  "Migrates the dependent database to the latest migration."
  [{:keys [datastore migrations strategy reporter]}]
  (let [migrations (map #(repl/wrap-reporting % reporter) migrations)]
    (core/migrate-all datastore {} migrations strategy)))

(defn rollback
  "Rolls the dependent database back to a specific migration ID, or by a fixed
  number of migrations. If supplied with only one argument, the database is
  rolled back by only one migration."
  ([component]
   (rollback component 1))
  ([{:keys [datastore migrations reporter]} amount-or-id]
   (let [migrations (map #(repl/wrap-reporting % reporter) migrations)
         index      (core/into-index migrations)]
     (if (integer? amount-or-id)
       (core/rollback-last datastore index amount-or-id)
       (core/rollback-to datastore index amount-or-id)))))
