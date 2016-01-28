(require '[boot.util :refer :all])

(declare sift-tests)

(deftask call-boot
  []
  (binding [*sh-dir* "./test"]
    (dosh "boot" "integration-tests")))

(ns-unmap 'boot.user 'test)

(deftask test []
  (call-boot))

(deftask auto-test []
  (comp (watch)
        (call-boot)))
