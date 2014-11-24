(ns hvacio-ui.translation
  (:require-macros [taoensso.tower :as tower :refer (dict-compile)])
  (:require [taoensso.tower :as tower]))

(def locale (atom "en"))

;;;;; translation
(def ^:private tconfig
  {:fallback-locale :en
   ;; Inlined (macro) dict => this ns needs rebuild for dict changes to reflect:
   :compiled-dictionary (tower/dict-compile "hvacio-ui-translation.edn")})

(def t (tower/make-t tconfig)) ; Create translation fn
