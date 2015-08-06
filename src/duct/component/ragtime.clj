(ns duct.component.ragtime
  (:require [com.stuartsierra.component :as component]
            [ragtime.core :as core]
            [ragtime.jdbc :as jdbc]))

(defrecord Ragtime [resource-path]
  component/Lifecycle
  (start [component]
    (assoc component
           :datastore  (-> component :db :spec jdbc/sql-database)
           :migrations (jdbc/load-resources resource-path)))
  (stop [component]
    (dissoc component :datastore :migrations)))

(defn ragtime [options]
  (map->Ragtime options))
