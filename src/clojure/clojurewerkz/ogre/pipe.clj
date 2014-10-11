(ns clojurewerkz.ogre.pipe
  (:refer-clojure :exclude [iterate])
  (:import (com.tinkerpop.gremlin.process.graph GraphTraversal)
           (com.tinkerpop.gremlin.structure Vertex)
           ;(com.tinkerpop.pipes.util.structures Row)
           )
  (:use clojurewerkz.ogre.util))

(defn back-to
  [t ^String s]
  (conj t #(.back ^GraphTraversal % s)))

(defn iterate!
  [t]
  (.iterate ^GraphTraversal (compile-query t)))

(defn next!
  ([t]
   (.next ^GraphTraversal t))
  ([t i]
   (.next ^GraphTraversal (compile-query t) i)))

;; (defn step [^GremlinPipeline p e]
;;   (.step p e))

(defmacro ^{:private true}
  to-java-list!
  [t]
  `(.toList ~t))

(defn into-vec!
  [t]
  (into [] (to-java-list! t)))

(defn into-set!
  [t]
  (into #{} (to-java-list! t)))

(defn into-list!
  [t]
  (into '() (to-java-list! t)))

(defn into-lazy-seq!
  [t]
  (let [traversal (compile-query t)
        f (fn [_] (first (.next traversal 1)))]
    (clojure.core/iterate f (f nil))))

;;Inspiried by gather, these take the first element in the object
;;returned and convert it to something useful for clojure.
(defmulti convert-to-map class)

(defmethod convert-to-map java.util.HashMap
  [m]
  (into {} (for [[k v] m] [(keyword k) v])))

;(defmethod convert-to-map Row
;  [^Row m]
;  (into {} (for [^String k (.getColumnNames m)]
;             [(keyword k) (.getColumn m k)])))

(defn first-of!
  [t]
  (next! t))

(defn first-into-vec!
  [t]
  (vec (first-of! t)))

(defn first-into-set!
  [t]
  (set (first-of! t)))

(defn first-into-map!
  [t]
  (convert-to-map (first-of! t)))

(defn all-into-vecs!
  [t]
  (map vec (into-vec! t)))

(defn all-into-sets!
  [t]
  (map set (into-vec! t)))

(defn all-into-maps!
  [t]
  (map convert-to-map (into-vec! t)))

(defn count!
  [t]
  (next! (.count ^GraphTraversal t)))
;; Reversed property accessors

(defn prop
  [k]
  (fn [^Vertex v]
    (-> v (.property (name k)) (.value))))
