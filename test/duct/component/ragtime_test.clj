(ns duct.component.ragtime-test
  (:require [clojure.test :refer :all]
            [duct.component.ragtime :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as sql]))

(defn table-names [db-spec]
  (set (sql/query db-spec ["SHOW TABLES"] :row-fn :table_name)))

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
             ["DROP TABLE foo;\n"]))))

  (testing "migrate and rollback"
    (let [spec {:connection-uri "jdbc:h2:mem:test3;DB_CLOSE_DELAY=-1"}
          cpnt (-> (ragtime {:resource-path "migrations"})
                   (assoc-in [:db :spec] spec)
                   (component/start))]
      (is (= (with-out-str (migrate cpnt)) "Applying 001-test\n"))
      (is (= (table-names spec) #{"RAGTIME_MIGRATIONS" "FOO"}))
      (is (= (with-out-str (rollback cpnt)) "Rolling back 001-test\n"))
      (is (= (table-names spec) #{"RAGTIME_MIGRATIONS"})))))
