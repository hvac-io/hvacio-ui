(ns hvacio-ui.controllers
  (:require [reagent.core :as r]
            [goog.dom :as dom]
            [hvacio-ui.templates.resize :as rz]
            [cljs.reader :as reader]
            [ajax.core :refer [GET POST]]
            [hvacio-ui.translation :as t]
            [hvacio-ui.util :as util]
            [goog.string :as gstring]
            [hvacio-ui.templates.modals :as modal]
            [query.query :as q]))


(defn get-url-device
  "Return the device ID specified in the query, if any." []
  (:device (q/get-query)))

(defn set-url-device!
  "Set the tab query to the the given tab-name"
  [device-id]
  (q/merge-in-query! {:device device-id}))


(defn api-path [api-root & args]
  (str api-root (s/join "/" args)))

;=======================================================
;================== Devices overview ===================
;=======================================================

;;; The main devices list. In this column, we show the device ID and
;;; the device name. The user can then click on a device to see its
;;; objects in details.

(defn dev-list-item []
  (fn [project-id dev-list current main configs]
    (let [cur @current
          update-fn (fn [id]
                       (do (reset! current id)
                           (set-url-device! id)
                           (GET
                            (api-path (:api-root configs) "device-summary" project-id id)
                            {:handler #(reset! main %)
                             :error-handler prn})))]

      ;; initialization
      (cond 
       ;; If we don't have a selected device, but there's one in the query
       (and dev-list (not cur) (get-url-device)) (update-fn (get-url-device))
       ;; If we don't have a selected device, select one for us.
       (and dev-list (not cur)) (let [id (ffirst (sort-by #(reader/read-string (first %)) dev-list))]
                                  (update-fn id)))
      
      ;; Rendering section
      [:div.btn-group-vertical.btn-group.btn-block 
       {:id "device-list"}
       (for [[id name] (sort-by #(reader/read-string (first %)) dev-list)]
         ^{:key id}
         [:button.btn.btn-default
          {:style {:cursor "pointer"
                    :white-space "normal"}
           :class (when (= id cur) "active")
           :on-click #(update-fn id)
           :title name}
          [:div.text-left
           [:div id]
           [:strong name]]])])))


;=======================================================
;===================== Search bar  =====================
;=======================================================


(defn find-name [name db]
  (let [regexp (re-pattern (util/make-fuzzy-regex name))]
    (->> (filter (util/where {:name regexp}) db)
         ;(sort-by :name)
         identity
         )))

(defn input [visible-objects-a objects]
  [:input {:type "text" :class "form-control" :placeholder (t/t @t/locale :vigilia/search-name)
           :on-change #(reset! visible-objects-a (find-name (-> % .-target .-value) objects))}])

(defn objects-count [visible-objects-a objects]
  (let [vis @visible-objects-a]
    [:div.text-center
     (str (count vis) " / " (count objects)" "(t/t @t/locale :vigilia/objects-visible))]))


(def input-with-focus ;;; check later why the minified version screw up this...
  (with-meta input
    {:component-did-mount #(.focus (r/dom-node %))}))

(defn search-bar [visible-objects-a objects]
  [:div 
   [:div.input-group
    [input visible-objects-a objects]
    [:span.input-group-addon [:span.glyphicon.glyphicon-search]]]
   [objects-count visible-objects-a objects]])


;=======================================================
;================= Main view (objects) =================
;=======================================================

(defn make-table [visible-objects-a objects-a p-id d-id configs]
  (let [objs (sort-by :name @visible-objects-a)]
    [:table.table.table-striped {:id "device-table"}
     [:thead [:tr 
              [:th (t/t @t/locale :vigilia/name)]
              [:th (t/t @t/locale :vigilia/description)] 
              [:th (t/t @t/locale :vigilia/value)] 
              [:th (t/t @t/locale :vigilia/unit)] [:th]]]
     [:tbody
      (for [obj objs :when (:name obj)]
        ^{:key obj}
        [:tr
         [:td (:name obj)]
         [:td (:description obj)]
         [:td (when-let [v (:value obj)] (gstring/format "%.2f" v))]
         [:td (:unit obj)]
         [:td 
          (when (:value obj)
            (let [obj* (assoc obj :project-id p-id :device-id d-id)]
              [:div {:style {:white-space "nowrap"}}
               (for [{:keys [btn-symbol btn-fn btn-title]} (:device-table-btns configs)]
                 ^{:key (str btn-title (:object-id obj*))}
                 [:button.btn.btn-default.btn-sm
                  {:on-click #(btn-fn obj*)}
                  btn-symbol])]))]])]]))


(defn device-table []
  (fn [project-id device-id objects-a configs]
    (let [objects @objects-a
          visible-objects-a (r/atom objects)]
      (when-not objects
        (GET (api-path (:api-root configs) "objects" project-id device-id)
             {:handler #(reset! objects-a %)
              :error-handler prn}))
      [:div
       [search-bar visible-objects-a objects]
       [make-table visible-objects-a objects-a project-id device-id configs]
       ])))


(defn dev-main-view [p-id d-id-a device-summary-a configs]
  (let [data @device-summary-a
        device-id @d-id-a]
    [:div
     (when data [:div.jumbotron
                 [:h3 {:style {:margin-top 0 :padding-top 20}}(:name data)]
                 ;[last-scan data]". --"
                 ;[last-scan-duration data]
                 ])
     [:a.btn.btn-default.visible-xs {:href "#device-list"} 
      (t/t @t/locale :vigilia/list) " "[:i.fa.fa-list]]
     
     (when data 
       (let [objects-a (r/atom nil)]
         [device-table p-id device-id objects-a configs]))]))



;; When we select a new device, we should scroll back to the top of
;; the main view.
(def scroll-main-view
  (with-meta dev-main-view
    {:component-did-mount #(aset (r/dom-node %) "scrollTop" 0)}))





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn devices []
  (let [device-summary-a (r/atom nil)
        device-id-a (r/atom nil)
        dev-list-a (r/atom nil)]
    (fn [project-id configs]
      (let [d @dev-list-a
            top-margin (:top-margin configs)]
        (when (nil? d)
          (GET (api-path (:api-root configs) "devices-list" project-id)
              {:handler #(reset! dev-list-a %)
               :error-handler prn}))
        [:div.row
         [:div.col-sm-2 {:style {:height (str "calc(100vh - " top-margin ")")
                                 :padding-right 0
                                 :overflow-y "auto"}}
          [dev-list-item project-id d device-id-a device-summary-a]]
         [:div.col-sm-10 {:style {:height (str "calc(100vh - " top-margin ")")
                                  :overflow-y "auto"}}
          [scroll-main-view project-id device-id-a device-summary-a configs]]]))))

  

(def default-configs
  {:top-margin "50px" ; space for the navbar, or for tabs.
   :api-root "/api/v1/"
   :device-table-btns
   [{:btn-fn #(modal/modal [:div [:h3 "Undefined"] "Undefined function for this button."])
     :btn-symbol [:i.fa.fa-bar-chart-o]
     :btn-title "Timeseries"}
    {:btn-fn #(modal/modal [:div [:h3 "Undefined"] "Undefined function for this button."])
     :btn-symbol [:i.fa.fa-briefcase]
     :btn-title "Briefcase"}]
   })


;; (modal/modal 
;;      [:div
;;       [:iframe 
;;        {:style {:height "500px" :width "100%" :border "none"}
;;         :src "https://hvac.io/vigilia/embed/g/5371147be4b0222b740851a2?bc%5B%5D=%3Aa10122..0.7"}]])

(defn controllers-view 
  ([project-id] (controllers-view project-id nil))
  ([project-id configs]
     [:div
      [modal/modal-window]
      [devices project-id (merge default-configs configs)]]))

