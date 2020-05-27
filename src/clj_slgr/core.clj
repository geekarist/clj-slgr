(ns clj-slgr.core
  (:require [etaoin.keys :as k]
            [hickory.core :as hc])
  (:use etaoin.api))

(defonce driver (firefox {:headless false}))

(def default-delay 1.5)

(defn navigate-to-results [location]
  (doto-wait default-delay driver
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
             (click {:css ".containerRight .txt_rechercher"})))

(defn get-result-pages []
  (loop [previous-pages []]
    (let [current-page (get-source driver)
          prev-and-current-pages (conj previous-pages current-page)
          last-page? (invisible? driver {:css ".next"})]
      (if last-page? prev-and-current-pages
                     (do (click driver {:css ".next"})
                         (wait driver default-delay)
                         (recur prev-and-current-pages))))))

(defn get-houses-pages [location]
  (navigate-to-results location)
  (get-result-pages))

(defn prn-ret [obj]
  (prn obj)
  obj)

(comment
  (use '[clj-slgr.core :as slgr])
  (slgr/hello)
  (slgr/count-houses "montigny sur loing")
  (slgr/get-houses-pages "montigny-sur-loing")
  (->> "montigny-sur-loing"
       slgr/get-houses-pages
       (map hc/parse)
       (map hc/as-hiccup)))