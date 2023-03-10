(ns app.hello
  (:require
   [reagent.core :as r]
   [ajax.core :refer [GET json-response-format]]))

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

(defonce state-seasons (r/atom nil))

(defonce uri "https://api.tvmaze.com/shows/540/episodes")

(defn handler [response]
  (let [data (keywordize-keys response)
        grouped-by-season (group-by :season data)]
    (doseq [[season eps] grouped-by-season]
      (js/console.log (str "Season " season))
      (doseq [ep eps]
        (js/console.log (str "Ep " (:name ep)))))
    (reset! state-seasons grouped-by-season)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "ERROR: " status " " status-text)))

(defn query []
  (GET "https://api.tvmaze.com/shows/540/episodes"
    {:error-handler error-handler
     :handler handler
     :response-format :json
     :keywords? true}))

(defn show-seasons [items]
  (if (= 0 (count items))
    [:div.my-10 "No data yet..."]
    [:div
     [:ul.list-disc.px-6
      (for [item items]
        [:li.my-4.bg-green-200.py-2.px-2.rounded
         [:div  (str item) " - "]])]]))

(defn query-btn []
  [:input.bg-blue-500.text-white.font-bold.py-2.px-4.border.border-blue-700.rounded
   {:type "button"
    :value "Query Seasons"
    :on-click #(query)}])

(defn hello []
  [:<>
   [:div.my-4.mx-4
    [query-btn]]
   [:div.my-10
    [show-seasons @state-seasons]]])

