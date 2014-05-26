(ns hvacio-ui.client
  (:require [reagent.core :as r]
            [goog.dom :as dom]
            [hvacio-ui.templates.modals :as modal]
            [hvacio-ui.controllers :as ctrls]))

(enable-console-print!)

(prn "Running dev environment")


(def test-configs
  {:device-table-btns
      [{:btn-fn #(modal/modal 
                 [:div
                  [:iframe 
                   {:style {:height "500px" :width "100%" :border "none"}
                    :src "https://hvac.io/vigilia/embed/g/5371147be4b0222b740851a2?bc%5B%5D=%3Aa10122..0.7"}]])
        :btn-title "graphs"
        :btn-symbol [:i.fa.fa-bar-chart-o]}]})

(defn ^:export run [project-id locale]
  (r/render-component
   [:div.container
    [:div {:style {:height "50px" :background-color "red"}}]
    [ctrls/controllers-view "5371147be4b0222b740851a2"
     ;test-configs
     ]]
   (dom/getElement "my-div")))
