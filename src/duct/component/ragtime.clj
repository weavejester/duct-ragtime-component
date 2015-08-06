(ns duct.component.ragtime
  (:require [com.stuartsierra.component :as component]
            [ragtime.core :as ragtime]))

(defrecord Ragtime []
  component/LifeCycle
  (start [component]
    component)
  (stop [component]
    component))

(defn ragtime [options]
  (map->Ragtime options))
