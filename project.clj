(defproject chaat "0.1.0-SNAPSHOT"
  :description "chaat: cli chat app"
  :url "https://github.com/nilenso/chaat"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [ring/ring-devel "1.10.0"]
                 [ring/ring-json "0.5.1"]
                 [bidi "2.1.6"]
                 [ragtime "0.8.0"]
                 [org.postgresql/postgresql "42.6.0"]
                 [crypto-password "0.3.0"]
                 [crypto-random "1.2.1"]
                 [com.github.seancorfield/next.jdbc "1.3.874"]
                 [clojure.java-time "1.2.0"]
                 [com.stuartsierra/component "1.1.0"]
                 [com.zaxxer/HikariCP "5.0.1"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler chaat.handler/handler}
  :main ^:skip-aot chaat.app
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
