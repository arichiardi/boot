(set-env!
 :source-paths #{"."}
 :dependencies '[[org.clojure/tools.reader "1.0.0-alpha2"]])

(require '[clojure.test :refer :all]
         '[boot.util :refer :all])

(declare sift-with-meta-tests
         sift-include-tests
         sift-add-meta-tests)

(deftask integration-tests
  []
  ;; Each test task should reset the fileset itself and leave
  ;; a clean env for the next. How? Now they are executed one at the time.
  (comp (sift-add-meta-tests)
        #_(sift-with-meta-tests)
        #_(sift-include-tests)
        ;; "Other test tasks here
        ))


(deftask with-meta-tests []
  (with-pre-wrap fileset
    (let [tmpfiles (output-files fileset)
          tmpfiles-with-meta (filter :boot-test-tag tmpfiles)
          tmpfiles-clj (by-re [#".clj$"] tmpfiles)]
      (is (= tmpfiles-with-meta tmpfiles-clj) "only .clj files should result from :with-meta #{boot-test-tag}"))
    fileset))

(deftask with-meta-invert-tests []
  (with-pre-wrap fileset
    (let [tmpfiles (output-files fileset)
          tmpfiles-without-meta (remove :boot-test-tag tmpfiles)
          tmpfiles-not-clj (not-by-re [#".clj$"] tmpfiles)]
      (is (= tmpfiles-without-meta tmpfiles-not-clj) "only non .clj files should result from :with-meta #{boot-test-tag} invert"))
    fileset))

(deftask sift-with-meta-tests []
  #_(comp (sift :add-jar {'org.clojure/tools.reader #".*"}) ;; populate
          (sift :add-meta {#".clj$" :boot-test-tag})
          (sift :with-meta #{:boot-test-tag})
          (with-meta-tests))
  ;; how to reset the fileset?
  (comp (sift :add-jar {'org.clojure/tools.reader #".*"}) ;; populate
        (sift :add-meta {#".clj$" :boot-test-tag})
        (sift :with-meta #{:boot-test-tag} :invert true)
        (with-meta-invert-tests)))

(deftask include-tests []
  (with-pre-wrap fileset
    (let [tmpfiles (output-files fileset)
          tmpfiles-clj-md (by-re [#".clj$" #".MD$"] tmpfiles)
          tmpfiles-others (not-by-re [#".clj$" #".MD$"] tmpfiles)]
      (is (not (empty? tmpfiles-clj-md)) ".clj and .MD files should return from :include #{.clj$ .MD$}")
      (is (empty? tmpfiles-others) "no other files should return from :include #{.clj$ .MD$}"))
    fileset))

(deftask include-invert-tests []
  (with-pre-wrap fileset
    (let [tmpfiles (output-files fileset)
          tmpfiles-clj-md (by-re [#".clj$" #".MD$"] tmpfiles)
          tmpfiles-others (not-by-re [#".clj$" #".MD$"] tmpfiles)]
      (is (empty? tmpfiles-clj-md) ".clj and .MD files should not return from :include #{.clj$ .MD$} :invert")
      (is (not (empty? tmpfiles-others)) "other files should return from :include #{.clj$ .MD$} :invert"))
    fileset))

(deftask sift-include-tests []
  (comp (sift :add-jar {'org.clojure/tools.reader #".*"}) ;; populate
        (sift :include #{#".clj$" #".MD$"})
        (include-tests))
  ;; how to reset the fileset?
  #_(comp (sift :add-jar {'org.clojure/tools.reader #".*"}) ;; populate
          (sift :include #{#".clj$" #".MD$"} :invert true)
          (include-invert-tests)))

(deftask add-meta-tests []
  (with-pre-wrap fileset
    (let [tmpfiles (output-files fileset)
          tmpfiles-with-meta (filter :boot-test-tag tmpfiles)]
      (is (empty? (not-by-re [#".clj$"] tmpfiles-with-meta)) "non .clj files should not have :boot-test-tag metadata")
      (is (seq (by-re [#".clj$"] tmpfiles-with-meta)) "only .clj files should have :boot-test-tag metadata"))
    fileset))

(deftask add-meta-invert-tests []
  (with-pre-wrap fileset
    (let [tmpfiles (output-files fileset)
          tmpfiles-with-meta (filter :boot-test-tag tmpfiles)]
      (is (empty? (by-re [#".clj$"] tmpfiles-with-meta)) ".clj files should not have :boot-test-tag metadata")
      (is (seq (not-by-re [#".clj$"] tmpfiles-with-meta)) "only non .clj files should have :boot-test-tag metadata"))
    fileset))

(deftask sift-add-meta-tests []
  (comp (sift :add-jar {'org.clojure/tools.reader #".*"}) ;; populate
        (sift :add-meta {#".clj$" :boot-test-tag})
        (add-meta-tests))
  ;; how to reset the fileset?
  #_(comp (sift :add-jar {'org.clojure/tools.reader #".*"}) ;; populate
          (sift :add-meta {#".clj$" :boot-test-tag} :invert true)
          (add-meta-invert-tests)))
