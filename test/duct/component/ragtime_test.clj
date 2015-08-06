(ns duct.component.ragtime-test
  (:require [clojure.test :refer :all]
            [duct.component.ragtime :refer :all]
            [com.stuartsierra.component :as component]))

(deftest test-ragtime
  (testing "is a component"
    (let [r (ragtime {})]
      (is (satisfies? component/Lifecycle r))
      (is (satisfies? component/Lifecycle (component/start r)))
      (is (satisfies? component/Lifecycle (component/stop (component/start r)))))))
