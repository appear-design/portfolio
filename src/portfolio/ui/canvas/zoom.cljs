(ns portfolio.ui.canvas.zoom
  (:require [portfolio.ui.canvas.addons :as addons]
            [portfolio.ui.canvas.protocols :as protocols]
            [portfolio.ui.components.canvas :as canvas]
            [portfolio.ui.components.canvas-toolbar-buttons :refer [Button ButtonGroup]]))

(defn reset-canvas-zoom [_ el opt]
  (when-not (contains? opt :zoom/level)
    (let [body (canvas/get-iframe-body el)]
      (set! (.. body -style -transform) "")
      (set! (.. body -style -width) "100%")
      (set! (.. body -style -height) "100%"))))

(defn zoom [el opt]
  (let [^js body (canvas/get-iframe-body el)
        lvl (:zoom/level opt)]
    (cond
      lvl
      (let [size (str (/ 100 lvl) "%")]
        (set! (.. body -zoomed) true)
        (set! (.. body -style -transform) (str "scale(" lvl ")"))
        (set! (.. body -style -transformOrigin) "left top")
        (set! (.. body -style -width) size)
        (set! (.. body -style -height) size))

      (.. body -zoomed)
      (reset-canvas-zoom nil el opt))))

(defn prepare-zoom-button [tool state {:keys [pane-options pane-id]}]
  (let [level (or (:zoom/level pane-options) 1)
        increment (or (:zoom-increment tool) 0.25)]
    (with-meta
      {:title (:title tool)
       :icon (:icon tool)
       :active? (if (< 0 increment)
                  (< 1 level)
                  (< level 1))
       :actions (addons/get-set-actions state tool pane-id {:zoom/level (+ increment level)})}
      {`protocols/render-toolbar-button #'Button})))

(def impl
  {`protocols/prepare-canvas (fn [_ el opt] (zoom el opt))
   `protocols/finalize-canvas (fn [_ _ _])
   `protocols/prepare-toolbar-button #'prepare-zoom-button})

(defn create-zoom-in-tool [config]
  (with-meta
    {:id :canvas/zoom-in
     :group-id :canvas/zoom
     :title "Zoom in"
     :icon :portfolio.ui.icons/magnifying-glass-plus
     :zoom-increment (or (:zoom-increment config) 0.25)}
    impl))

(defn create-zoom-out-tool [config]
  (with-meta
    {:id :canvas/zoom-out
     :group-id :canvas/zoom
     :title "Zoom out"
     :icon :portfolio.ui.icons/magnifying-glass-minus
     :zoom-increment (or (:zoom-increment config) -0.25)}
    impl))

(defn create-reset-zoom-tool [config]
  (addons/create-action-button
   {:id :canvas/zoom-reset
    :group-id :canvas/zoom
    :title "Reset zoom"
    :icon :portfolio.ui.icons/arrow-counter-clockwise
    :prepare-canvas #'reset-canvas-zoom
    :get-actions (fn [tool state {:keys [pane-id]}]
                   (addons/get-clear-actions state tool pane-id))
    :show? (fn [_ _ {:keys [pane-options]}]
             (and (:zoom/level pane-options)
                  (not= 1 (:zoom/level pane-options))))}))

(defn prepare-button-group [tool state opt]
  (with-meta
    {:buttons (->> (:buttons tool)
                   (keep #(protocols/prepare-toolbar-button % state opt)))}
    {`protocols/render-toolbar-button #'ButtonGroup}))

(defn create-zoom-tool [config]
  (with-meta
    {:id :canvas/zoom
     :buttons [(create-zoom-out-tool config)
               (create-zoom-in-tool config)
               (create-reset-zoom-tool config)]}
    {`protocols/prepare-canvas (fn [_ el opt] (zoom el opt))
     `protocols/finalize-canvas (fn [_ _ _])
     `protocols/prepare-toolbar-button #'prepare-button-group
     `protocols/get-tool-value (fn [tool state pane-id] (addons/get-tool-value state tool pane-id))}))
