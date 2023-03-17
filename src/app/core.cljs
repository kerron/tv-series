(ns app.core
  "This namespace contains your application and is the entrypoint for 'yarn start'."
  (:require [reagent.core :as r]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]
            [reitit.coercion.spec :as rss]
            [app.ui-episode-details :refer [ui-episode-details]]
            [ajax.core :refer [GET json-response-format]]
            [app.hello :refer [hello]]))

(defonce state-routes (r/atom nil))
(defonce state-episode (r/atom nil))
(defonce state-loading (r/atom false))

(defn page-home []
  (hello))

(defonce uri "https://api.tvmaze.com/episodes/")

(defn handler [response]
  (js/console.log "episode response " response)
  (reset! state-loading false)
  (reset! state-episode response))

(defn error-handler [{:keys [status status-text]}]
  (reset! state-loading false)
  (.log js/console (str "ERROR: " status " " status-text)))

(defn fetch [slug]
  (reset! state-loading true)
  (GET (str uri slug)
    {:error-handler error-handler
     :handler handler
     :response-format :json
     :keywords? true}))

(defn page-episode []
  (if @state-loading
    [:div "is loading"]
    (if (seq @state-episode)
      (ui-episode-details @state-episode)
      [:p "episode not found"])))

(def routes
  [["/" {:name :routes/home
         :view #'page-home
         :controllers
         [{:start (js/console.log "home 1")
           :stop (js/console.log "home 2")}]}]

   ["/episode/:slug" {:name :routes/episode
                      :view #'page-episode
                      :parameters
                      {:path {:slug int?}}
                      :controllers
                      [{:params (fn [match]
                                  (-> match :parameters :path))
                        :start (fn [{:keys [slug]}]
                                 (fetch slug)
                                 (js/console.log "slug of " slug))
                        :stop #(js/console.log "episode 2")}]}]])

(def router
  (rf/router routes {:data
                     {:coercion rss/coercion
                      :controllers
                      [{:start #(println "root start")
                        :stop #(println "root end")}]}}))

(defn start-router! []
  (rfe/start!
   router
   (fn [new-route]
     (swap! state-routes
            (fn [old-route]
              (when new-route
                (assoc new-route :controllers
                       (rfc/apply-controllers
                        (:controllers old-route) new-route))))))
   ;; set to false to enable historyApi
   {:use-fragment false}))

;; automatically get view depending on path
(defn app []
  [:div.bg-gray-100
   (let [current-view (-> @state-routes :data :view)]
     [current-view @state-routes])])

(defn ^:dev/after-load render
  "Render the toplevel component for this app."
  []
  (r/render [app] (.getElementById js/document "app")))

(defn ^:export main
  "Run application startup logic."
  []
  (start-router!)
  (render))
