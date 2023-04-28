(ns portfolio.fulcro
  (:require [portfolio.core :as portfolio]))

(defmacro defscene [id & opts]
  (when (portfolio/portfolio-active?)
    `(portfolio.data/register-scene!
      (portfolio.fulcro/create-scene
       ~(portfolio/get-options-map id (:line &env) opts)))))

(defmacro configure-scenes [& opts]
  (when (portfolio/portfolio-active?)
    `(portfolio.data/register-collection!
      ~@(portfolio/get-collection-options opts))))

(defmacro defscene-fulcro [id & opts]
  (when (portfolio/portfolio-active?)
    (let [Root-sym (gensym "Root")
          option-map (portfolio/get-options-map id (:line &env) (butlast opts))]
      `(let [~'component-fn (:component-fn ~option-map)
             ~'Root-component
             (com.fulcrologic.fulcro.components/defsc ~Root-sym
               [~'this ~'props ~'computed-props]
               ~(:fulcro-opts option-map)
               ~(last opts))
             ~'option-map (assoc ~option-map :component-fn (fn [& ~'args] ~Root-sym))]
         (portfolio.data/register-scene!
          (portfolio.fulcro/create-scene ~'option-map))))))