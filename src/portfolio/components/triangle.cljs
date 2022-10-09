(ns portfolio.components.triangle
  (:require [dumdom.core :as d]))

(d/defcomponent Triangle [{:keys [color direction actions]}]
  [:span
   {:on-click actions
    :style
    {:cursor (when actions "pointer")
     :display "inline-block"
     :padding "0 6px"}}
   [:span
    {:style
     {:display "inline-block"
      :width 0
      :height 0
      :color (or color "rgba(153, 153, 153, 0.6)")
      :border-top "4px solid transparent"
      :border-bottom "4px solid transparent"
      :border-left "4px solid"
      :transform (case direction
                   :down "rotateZ(90deg)"
                   :left "rotateZ(180deg)"
                   :up "rotateZ(270deg)"
                   "none")
      :transition "transform 0.1s ease-out 0s"}}]])
