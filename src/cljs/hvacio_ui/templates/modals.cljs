(ns hvacio-ui.templates.modals
  (:require [reagent.core :as r :refer [atom]]
            [goog.dom :as dom]
            [goog.events :as events])
  (:import [goog.events EventType]))

;;; Make sure to create the modal-window element somewhere in the dom.
;;; Recommended: at the start of the document.


(def modal-id "modal")

(def modal-content (atom {:content [:div] :shown-callback nil}))

(defn get-modal []
  (dom/getElement modal-id))

(defn modal-window* []
  (let [content (:content @modal-content)]
    [:div.modal.fade {:id modal-id :tab-index -1 :role "dialog"}
     [:div.modal-dialog.modal-lg
      [:div.modal-content
       content]]]))

;; now we make sure the modals is cleared when hidden
(def modal-window
  (with-meta
    modal-window*
    {:component-did-mount (fn [e] (let [m (js/jQuery (get-modal))]
                                    (.call (aget m "on") m "hidden.bs.modal"
                                           #(do (reset! modal-content [:div])))
                                    (.call (aget m "on") m "shown.bs.modal" 
                                           #(when-let [shown-fn (:shown-callback @modal-content)]
                                              (shown-fn)))))}))

(defn show-modal []
  (let [m (js/jQuery (get-modal))]
    (.call (aget m "modal") m #js {:keyboard true})
    (.call (aget m "modal") m "show")
    m))


;;; main function

(defn modal 
  ([reagent-content] (modal reagent-content nil))
  ([reagent-content callbacks-map]
     (reset! modal-content (merge {:content reagent-content} callbacks-map))
     (show-modal)))
