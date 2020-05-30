(ns clj-slgr.core
  (:require [etaoin.keys :as k]
            [hickory.core :as hc])
  (:use etaoin.api))

(defonce driver (firefox {:headless false}))

(def default-delay 2)

(defn get-first-result-page [location]
  (prn "Getting first page...")
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
             (click {:css ".containerRight .txt_rechercher"}))
  (wait default-delay)
  (get-source driver))

(defn get-next-result-page []
  (prn "Getting next page...")
  (if (visible? driver {:css ".next"})
    (do (click driver {:css ".next"})
        (wait driver default-delay)
        (get-source driver))))

(defn get-houses-pages-seq [location]
  (cons (get-first-result-page location)
        (repeatedly get-next-result-page)))

(defn get-houses-pages [location]
  (take-while some? (get-houses-pages-seq location)))

(defn prn-ret [obj]
  (prn obj)
  obj)

(comment
  (use '[clj-slgr.core :as slgr])
  (slgr/hello)
  (slgr/count-houses "montigny sur loing")
  (slgr/get-houses-pages "montigny-sur-loing")
  (get-first-result-page "cachan")
  (get-next-result-page)
  (get-houses-pages-seq "cachan")
  (->> "val-de-marne"
       slgr/get-houses-pages
       (map hc/parse)
       (map hc/as-hiccup)
       (count)))