(ns app.ui-seasons
  (:require [app.ui-episodes :refer [ui-episodes]]))

(defn ui-seasons [seasons]
  (for [[season eps] seasons]
    [:div
     [:h2.font-bold.text-center {:style {:background-color "#f9fafb"}} (str "S:" season)]
     (ui-episodes eps)]))
