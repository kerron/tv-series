(ns app.core
  "This namespace contains your application and is the entrypoint for 'yarn start'."
  (:require [reagent.core :as r]
            [reitit.core :as rt]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]
            [app.hello :refer [hello]]))

(defonce state-routes (r/atom nil))

(defn page-home []
  (hello))

(defn page-login []
  [:h1 "login page"])

(def routes
  [["/" {:name ::home :view page-home}]
   ["/login" {:name ::login :view page-login}]])

(defn start-router! []
  (rfe/start!
   (rf/router routes {:data {:coercion rss/coercion}})
   (fn [m] (reset! state-routes m))
   ;; set to false to enable historyApi
   {:use-fragment false}))

(defn app []
  [:div
   (let [current-view (-> @state-routes :data :view)]
     [current-view])])

(defn ^:dev/after-load render
  "Render the toplevel component for this app."
  []
  (r/render [app] (.getElementById js/document "app")))

(defn ^:export main
  "Run application startup logic."
  []
  (start-router!)
  (render))
