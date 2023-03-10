(ns app.hello
  (:require [reagent.core :as r]
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
    (reset! state-seasons grouped-by-season)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "ERROR: " status " " status-text)))

(defn query []
  (GET "https://api.tvmaze.com/shows/540/episodes"
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
  [:p (for [ep eps]
        [:p {:style {:background-color (color-scale (:average  (:rating ep)))}} (str (:name ep) " - " (:average  (:rating ep)))])])

(defn ui-seasons [seasons]
  (if (= 0 (count seasons))
    [:div.my-10 "No data yet..."]
    [:div
     (for [[season eps] seasons]
       [:<>
        [:h2.font-bold (str "Season " season)]
        (ui-episodes eps)])]))

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
    [ui-seasons @state-seasons]]])

