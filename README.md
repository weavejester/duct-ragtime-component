# Duct-Ragtime-Component

[![Build Status](https://travis-ci.org/weavejester/duct-ragtime-component.svg?branch=master)](https://travis-ci.org/weavejester/duct-ragtime-component)

A [component][] for the [Ragtime][] migration library, designed to be
used in the [Duct][] framework (but can be used in any component-based
system).

[component]: https://github.com/stuartsierra/component
[ragtime]:   https://github.com/weavejester/ragtime
[duct]:      https://github.com/weavejester/duct

## Installation

Add the following dependency to your `project.clj`:

    [duct/ragtime-component "0.1.0"]

## Usage

Start by requiring the library and the Component library:

```clojure
(require '[duct.component.ragtime :refer [ragtime migrate rollback]]
         '[com.stuartsierra.component :as component])
```

You will also need a database component that contains a `:spec`
key. In this example, we'll just use a map:

```clojure
(def system
  (-> (component/system-map
       :db {:spec {:connection-uri "jdbc:h2:mem:test"}}
       :ragtime (ragtime {:resource-path "migrations"}))
      (component/system-using
       {:ragtime [:db]})))
```

When we start the system, the Ragtime component loads the migrations
from the supplied resource path, and connects to the dependent
database under the `:db` key.

```clojure
(alter-var-root #'system component/start)
```

We can then migrate and rollback the started Ragtime component. The
`migrate` function will update the database to the latest migration:

```clojure
(migrate (:ragtime system))
```

The `rollback` function can rollback the database a fixed number of
migrations, or to a specific migration:

```clojure
(rollback (:ragtime system))
(rollback (:ragtime system) 2)
(rollback (:ragtime system) "005-create-foo")
```

## License

Copyright © 2015 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
