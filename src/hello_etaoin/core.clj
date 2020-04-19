(ns hello-etaoin.core
  (:require [etaoin.keys :as k])
  (:use etaoin.api))

(defn hello []

  (def driver (firefox {:headless false}))

  ;; let's perform a quick Wiki session
  (go driver "https://en.wikipedia.org/")
  (wait-visible driver [{:id :simpleSearch} {:tag :input :name :search}])

  ;; search for something
  (fill driver {:tag :input :name :search} "Clojure programming language")
  (fill driver {:tag :input :name :search} k/enter)
  (wait-visible driver {:class :mw-search-results})

  ;; I'm sure the first link is what I was looking for
  (click driver [{:class :mw-search-results} {:class :mw-search-result-heading} {:tag :a}])
  (wait-visible driver {:id :firstHeading})

  ;; let's ensure
  (println "Page URL:" (get-url driver))                    ;; "https://en.wikipedia.org/wiki/Clojure"

  (println "Page title:" (get-title driver))                ;; "Clojure - Wikipedia"

  (println "Has text: 'Clojure':" (has-text? driver "Clojure")) ;; true

  ;; navigate on history
  (println "Navigate in history...")
  (back driver)
  (forward driver)
  (refresh driver)
  (println "Page title:" (get-title driver))                ;; "Clojure - Wikipedia"

  (quit driver)
  )

(defn count-houses [location]

  (def driver (firefox {:headless false}))

  (go driver "https://www.seloger.com/recherche-avancee.html")
  (wait-has-text driver {:css ".search_panel_footer .count"} "annonces")

  (get-element-inner-html driver {:css ".search_panel_footer .count strong"})

  (quit driver))

(defn find-property-ads [driver location]
  (doto-wait 1 driver
             (go "https://www.seloger.com/recherche-avancee.html")
             (wait-has-text {:css ".search_panel_footer .count"} "annonces")
             (click {:css ".c-displayPlaces .containerList"})
             (click {:css ".c-places input[type=\"text\"]"})
             (fill {:css ".c-places input[type=\"text\"]"} location)
             (fill-active k/enter)
             (click {:css ".c-places .slam-aui-tags-close"})
             (click {:css ".c-places input[type=\"text\"]"})
             (fill-active k/tab)
             (fill-active k/enter)
             (click {:css ".containerRight .txt_rechercher"}))
  (wait 10))

(comment
  (hello)
  (count-houses "montigny sur loing")
  (with-firefox {:headless false} drv (find-property-ads drv "montigny-sur-loing"))
  )