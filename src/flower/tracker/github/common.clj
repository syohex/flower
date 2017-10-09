(ns flower.tracker.github.common
  (:require [flower.macros :as macros]
            [flower.tracker.proto :as proto])
  (:import (java.net URL)
           (org.eclipse.egit.github.core.client GitHubClient)
           (org.eclipse.egit.github.core.service RepositoryService
                                                 IssueService
                                                 MilestoneService)))


;;
;; Public definitions
;;

(macros/public-definition get-github-conn-inner cached)
(macros/public-definition get-github-project-inner cached)
(macros/public-definition get-github-projects-inner cached)
(macros/public-definition get-github-workitems-inner cached)
(macros/public-definition get-github-iterations-inner cached)
(macros/public-definition get-github-capacity-inner cached)


;;
;; Private definitions
;;

(defn- private-get-github-conn-inner
  ([tracker] (let [auth (get-in (proto/get-tracker-component tracker)
                                [:auth]
                                {})
                   login (get auth :github-login)
                   password (get auth :github-password)
                   token (get auth :github-token)]
               (cond
                 (and login password) (private-get-github-conn-inner tracker login password)
                 token (private-get-github-conn-inner tracker token)
                 :else (GitHubClient/createClient (proto/get-tracker-url tracker)))))
  ([tracker token] (doto (GitHubClient/createClient (proto/get-tracker-url tracker))
                     (.setOAuth2Token token)))
  ([tracker login password] (doto (GitHubClient/createClient (proto/get-tracker-url tracker))
                              (.setCredentials login password))))


(defn- private-get-github-projects-inner [tracker]
  (let [organization-name (-> (URL. (proto/get-tracker-url tracker))
                              (.getPath)
                              (rest)
                              (clojure.string/join))
        conn-inner (get-github-conn-inner tracker)
        repository-service (RepositoryService. conn-inner)]
    (.getOrgRepositories repository-service organization-name)))


(defn- private-get-github-project-inner [tracker]
  (let [project-name (proto/get-project-name tracker)]
    (-> (filter (fn [project]
                  (= (.getName project)
                     project-name))
                (private-get-github-projects-inner tracker))
        (first))))


(defn- private-get-github-workitems-inner
  ([tracker] (let [conn-inner (get-github-conn-inner tracker)
                   project-inner (get-github-project-inner tracker)
                   issue-service (IssueService. conn-inner)]
               (.getIssues issue-service project-inner nil)))
  ([tracker task-ids] (let [task-ids-list (into [] (map #(Integer. %) task-ids))]
                        (into [] (filter (fn [issue]
                                           (.contains task-ids-list (.getNumber issue)))
                                         (get-github-workitems-inner tracker))))))


(defn- private-get-github-iterations-inner [tracker]
  (let [conn-inner (get-github-conn-inner tracker)
        repository-inner (get-github-project-inner tracker)
        milestone-service (MilestoneService. conn-inner)]
    (.getMilestones milestone-service repository-inner nil)))


(defn- private-get-github-capacity-inner [tracker iteration]
  nil)
