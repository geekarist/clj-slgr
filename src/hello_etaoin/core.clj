(ns hello-etaoin.core
  (:require [etaoin.keys :as k])
  (:use etaoin.api))

(defn hello []
  (def driver (firefox {:headless true}))                   ;; here, a Firefox window should appear

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

  ;; stops Firefox and HTTP server
  (quit driver))

(defn count-houses [location]
  (def driver (firefox {:headless true}))

  (go driver "https://www.seloger.com/recherche-avancee.html")
  (wait-has-text driver {:css ".search_panel_footer .count"} "annonces")

  (get-element-inner-html driver {:css ".search_panel_footer .count strong"}))

(comment
  (hello)
  (count-houses "montigny sur loing"))