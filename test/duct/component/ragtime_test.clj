(ns duct.component.ragtime-test
  (:require [clojure.test :refer :all]
            [duct.component.ragtime :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as sql]))

(defn table-names [db-spec]
  (set (sql/query db-spec ["SHOW TABLES"] :row-fn :table_name)))

(defn new-connection []
  (sql/get-connection {:connection-uri "jdbc:h2:mem:"}))

(deftest test-ragtime
  (testing "satisfies component/Lifecycle"
    (let [spec {:connection (new-connection)}
          cpnt (-> (ragtime {:resource-path "migrations"})
                   (assoc-in [:db :spec] spec))]
      (is (satisfies? component/Lifecycle cpnt))
      (is (satisfies? component/Lifecycle (component/start cpnt)))
      (is (satisfies? component/Lifecycle (component/stop (component/start cpnt))))))

  (testing "loads migrations from resource"
    (let [spec {:connection (new-connection)}
          cpnt (-> (ragtime {:resource-path "migrations"})
                   (assoc-in [:db :spec] spec)
                   (component/start))]
      (is (= (count (:migrations cpnt)) 2))
      (is (= (-> cpnt :migrations first :up)
             ["CREATE TABLE foo (id int);\n"]))
      (is (= (-> cpnt :migrations first :down)
             ["DROP TABLE foo;\n"]))))

  (testing "migrate and rollback"
    (let [spec {:connection (new-connection)}
          cpnt (-> (ragtime {:resource-path "migrations"})
                   (assoc-in [:db :spec] spec)
                   (component/start))]
      (is (= (with-out-str (migrate cpnt))
             "Applying 001-test\nApplying 002-test\n"))
      (is (= (table-names spec) #{"RAGTIME_MIGRATIONS" "FOO" "BAR"}))
      (is (= (with-out-str (rollback cpnt)) "Rolling back 002-test\n"))
      (is (= (table-names spec) #{"RAGTIME_MIGRATIONS" "FOO"}))
      (is (= (with-out-str (migrate cpnt)) "Applying 002-test\n"))
      (is (= (table-names spec) #{"RAGTIME_MIGRATIONS" "FOO" "BAR"}))
      (is (= (with-out-str (rollback cpnt 2))
             "Rolling back 002-test\nRolling back 001-test\n"))
      (is (= (table-names spec) #{"RAGTIME_MIGRATIONS"}))))

  (testing "reload"
    (let [cpnt (ragtime {:resource-path "migrations"})]
      (is (nil? (:migrations cpnt)))
      (is (some? (-> cpnt reload :migrations)))
      (is (= (-> cpnt reload :migrations count) 2)))))

