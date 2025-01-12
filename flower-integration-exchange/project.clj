(defproject flower/flower-integration-exchange "0.5.0"
  :description "Flower integration with Microsoft Exchange"
  :url "http://github.com/FlowerAutomation/flower"
  :scm {:dir ".."}
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :plugins [[org.clojure/core.unify "0.5.7"]
            [jonase/eastwood "0.3.11"]
            [lein-cljfmt "0.5.7"]
            [lein-bump-version "0.1.6"]]
  :cljfmt {:remove-consecutive-blank-lines? false}
  :eastwood {:exclude-namespaces [flower.messaging.exchange.async]}
  :aliases {"lint" ["do" ["cljfmt" "check"] ["eastwood"]]
            "test-all" ["with-profile" "default:+1.10" "test"]
            "lint-and-test-all" ["do" ["lint"] ["test-all"]]}
  :repositories {"atlassian-public" "https://maven.atlassian.com/content/repositories/atlassian-public/"
                 "sonatype" "https://oss.sonatype.org/content/repositories/releases"}
  :dependencies [[org.clojure/clojure "1.11.1" :scope "provided"]
                 [org.clojure/core.async "1.5.648"]
                 [com.microsoft.ews-java-api/ews-java-api "2.0"]
                 [flower/flower-common "0.5.0"]
                 [flower/flower-proto "0.5.0"]]
  :profiles {:1.10 {:dependencies [[org.clojure/clojure "1.10.1"]]}})
