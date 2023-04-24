(ns portfolio.fulcro
  (:require [portfolio.adapter :as adapter]
            [dumdom.component :as component])
  (:require-macros [portfolio.fulcro]
                   [com.fulcrologic.fulcro.components]))

(defn mount-or-get-root [component ^js el]
  (when-not (.-reactRoot el)
    (let [app (-> (com.fulcrologic.rad.application/fulcro-rad-app {:initialize-state? true})
                  (com.fulcrologic.fulcro.react.version18/with-react18))]
      (com.fulcrologic.fulcro.application/mount! app component el)
      (set! (.-reactRoot el) @(:com.fulcrologic.fulcro.react.version18/reactRoot app))))
  (.-reactRoot el))

(def component-impl
  {`adapter/render-component
   (fn [{:keys [component]} ^js el]
     (assert (some? el) "Asked to render component into null container.")
     (when-let [f (some-> el .-unmount)]
       (f))
     (let [root (mount-or-get-root component el)]
       (set! (.-unmount el) (fn []
                              (.unmount root)
                              (set! (.-reactRoot el) nil)
                              (set! (.-innerHTML el) "")
                              (set! (.-unmount el) nil)))
       (set! (.-unmountLib el) "react18")))})

(defn create-scene [scene]
  (adapter/prepare-scene scene component-impl))