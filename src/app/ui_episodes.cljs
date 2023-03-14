(ns app.ui-episodes
  (:require
   [clojure.string :as str]
   [reitit.frontend.easy :as rfe]))

(def colors
  ["#ffc266" "#ffdb4d" "#e6ff99" "#a3cc33" "#ccff33" "#33cc33"])

(defn color-scale [val]
  (let [range-size (- 10 6)
        index (min (max (int (/ (* (- val 6) (count colors)) range-size)) 0) (- (count colors) 1))]
    (nth colors index)))

(defn get-episode-number [ep]
  (-> ep :_links :self :href
      (str/split "/")
      (last)))

(defn ui-episodes [eps]
  [:div (for [ep eps]
          [:a
           {:href (rfe/href
                   :routes/episode {:slug (get-episode-number ep)})}
           (js/console.log (str ep))
           [:div.rounded-md.my-1 {:style {:background-color (color-scale (:average  (:rating ep)))}}
            [:p.text-center.p-4 (-> ep :rating :average)]]])])
