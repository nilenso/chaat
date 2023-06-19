(defproject chaat "0.1.0-SNAPSHOT"
  :description "chaat: cli chat app"
  :url "https://github.com/nilenso/chaat"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [ring/ring-devel "1.10.0"]
                 [bidi "2.1.6"]
                 [clj-time "0.15.2"]
                 [ragtime "0.8.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.6.0"]
                 [crypto-password "0.3.0"]
                 [seancorfield/next.jdbc "1.2.659"]
                 [com.github.seancorfield/next.jdbc "1.3.874"]
                 [com.github.seancorfield/honeysql "2.4.1033"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler chaat.handler/handler}
  :main ^:skip-aot chaat.app
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
