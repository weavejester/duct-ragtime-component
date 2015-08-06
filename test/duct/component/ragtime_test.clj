(ns duct.component.ragtime-test
  (:require [clojure.test :refer :all]
            [duct.component.ragtime :refer :all]
            [com.stuartsierra.component :as component]))

(deftest test-ragtime
  (testing "satisfies component/Lifecycle"
    (let [spec {:connection-uri "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1"}
          cpnt (-> (ragtime {:resource-path "migrations"})
                   (assoc-in [:db :spec] spec))]
      (is (satisfies? component/Lifecycle cpnt))
      (is (satisfies? component/Lifecycle (component/start cpnt)))
      (is (satisfies? component/Lifecycle (component/stop (component/start cpnt))))))

  (testing "loads migrations from resource"
    (let [spec {:connection-uri "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1"}
          cpnt (-> (ragtime {:resource-path "migrations"})
                   (assoc-in [:db :spec] spec)
                   (component/start))]
      (is (= (count (:migrations cpnt)) 1))
      (is (= (-> cpnt :migrations first :up)
             ["CREATE TABLE foo (id int);\n"]))
      (is (= (-> cpnt :migrations first :down)
             ["DROP TABLE foo;\n"])))))
