(ns app.hello
  (:require [app.ui-episodes :refer [ui-episodes]]
            [app.ui-seasons :refer [ui-seasons]]
            [reagent.core :as r]
            [app.ui-header :refer [ui-header]]
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
  [:<>
   (ui-header)
   [:div.my-10
    [:div.my-4.mx-4
     [query-btn]]
    [ui-main @state-seasons]]])

