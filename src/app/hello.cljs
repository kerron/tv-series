(ns app.hello
  (:require [reagent.core :as r]
            [reitit.core :as rt]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]
            [ajax.core :refer [GET json-response-format]]))

(defonce state-seasons (r/atom nil))

;; This function uses reduce-kv to iterate over 
;; the key-value pairs of a map, and recursively 
;; keywordizes each key and value pair using assoc 
;; and the keyword function. If the value associated 
;; with a key is another map, the function will also 
;; recursively keywordize the keys of that map.
(defn keywordize-keys [data]
  (if (map? data)
    (reduce-kv (fn [m k v] (assoc m (keyword k) (keywordize-keys v))) {} data)
    (if (sequential? data)
      (mapv keywordize-keys data)
      data)))

(defonce uri "https://api.tvmaze.com/shows/540/episodes")

(defn handler [response]
  (let [data (keywordize-keys response)
        grouped-by-season (group-by :season data)]
    (reset! state-seasons grouped-by-season)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "ERROR: " status " " status-text)))

(defn query []
  (GET uri
    {:error-handler error-handler
     :handler handler
     :response-format :json
     :keywords? true}))

(def colors
  ["#ffc266" "#ffdb4d" "#e6ff99" "#a3cc33" "#ccff33" "#33cc33"])

(defn color-scale [val]
  (let [range-size (- 10 6)
        index (min (max (int (/ (* (- val 6) (count colors)) range-size)) 0) (- (count colors) 1))]
    (nth colors index)))

(defn ui-episodes [eps]
  [:div (for [ep eps]
          [:a {:href "https://google.com"}
           (js/console.log (str ep))
           [:div.rounded-md.my-1 {:style {:background-color (color-scale (:average  (:rating ep)))}}
            [:p.text-center.p-4 (:average  (:rating ep))]]])])

(defn ui-seasons [seasons]
  (for [[season eps] seasons]
    [:div
     [:h2.font-bold.text-center {:style {:background-color "#f9fafb"}} (str "S:" season)]
     (ui-episodes eps)]))

(defn ui-main [seasons]
  (if (= 0 (count seasons))
    [:div.my-10 "No data yet..."]
    [:div.grid.grid-cols-12.gap-x-2
     (ui-seasons seasons)]))

(defn query-btn []
  [:input.bg-blue-500.text-white.font-bold.py-2.px-4.border.border-blue-700.rounded
   {:type "button"
    :value "Query Seasons"
    :on-click #(query)}])

(defn hello []
  [:div.my-10
   [:div.my-4.mx-4
    [query-btn]]
   [ui-main @state-seasons]])

