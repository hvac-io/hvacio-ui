(ns hvacio-ui.client
  (:require [reagent.core :as r]
            [goog.dom :as dom]
            [reagent-modals.modals :as modal]
            [hvacio-ui.controllers :as ctrls]))

(enable-console-print!)

(prn "Running dev environment")

(defn test-btn [obj]
  [:button.btn.btn-default.btn-sm
   {:on-click #(modal/modal! 
                [:div
                 [:iframe 
                  {:style {:height "500px" :width "100%" :border "none"}
                   :src "https://hvac.io/vigilia/embed/g/5371147be4b0222b740851a2?bc%5B%5D=%3Aa10122..0.7"}]])}
   [:i.fa.fa-bar-chart-o]])

(def test-configs
  {:device-table-btns test-btn})

(defn ^:export run [project-id locale]
  (r/render-component
   [:div.container
    [modal/modal-window]
    [:div {:style {:height "50px" :background-color "red"}}]
    [ctrls/controllers-view "5371147be4b0222b740851a2"
     ;test-configs
     ]]
   (dom/getElement "my-div")))
