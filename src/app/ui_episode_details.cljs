(ns app.ui-episode-details
  (:require
   [app.ui-header :refer [ui-header]]
   [app.hello :refer [state-ratings]]))

(comment
  state-ratings)

(defn get-similar-rating [rating]
  (get @state-ratings rating))

(defn ui-similar-ratings [details]
  [:<>
   [:h3.text-3xl.font-bold.my-4 "Similar Ratings"]
   (when details
     [:div (for [detail details]
             [:div (-> detail :name) " - " (-> detail :rating :average)])])])

(defn ui-episode-details [details]
  [:<>
   (ui-header)
   [:div
    [:h2 (str (-> details :name) " - " (-> details :rating :average))]
    [:img {:src (-> details :image :original)}]
    [:div (-> details :summary)]
    (ui-similar-ratings (get-similar-rating (-> details :rating :average)))]])
