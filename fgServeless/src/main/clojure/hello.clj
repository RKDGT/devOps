(ns hello
  (:require [clojure.data.json :as json]
            [clojure.java.io   :as io]
            [clojure.string :as str])
  (:gen-class
   :methods [^:static [handler [java.util.Map, com.amazonaws.services.lambda.runtime.Context] java.util.Map]]))

(defn convert [s]
  (try
    (Long/parseLong s)
    (catch NumberFormatException _
      (symbol s))))
(def operator-precedence {'= 0, '+ 0, '- 0, '* 0, '/ 0})
(defn infix-parse [expr]
  (if-not (seq? expr)
    expr
    (loop [val-stack ()
           op-stack ()
           [opd op & expr] expr]
      (let [priority (if op (operator-precedence op) ##-Inf)
            [popped-ops unpopped-ops] (split-with
                                        #(>= (operator-precedence %) priority)
                                        op-stack)
            val-stack (reduce
                        (fn [[right left & vals] op]
                          (cons (list op left right) vals))
                        (cons (infix-parse opd) val-stack)
                        popped-ops)]
        (if-not op
          (first val-stack)
          (recur val-stack (cons op unpopped-ops) expr))))))
(defn infix [expr]
  ((infix-parse (map convert (clojure.string/split expr #" ")))))

(defn handle-request [handler]
  (fn [_ input-stream output-stream context]
    (with-open [in  (io/reader input-stream)
                out (io/writer output-stream)]
      (let [request (json/read in :key-fn keyword)]
        (-> request
            (handler context)
            (json/write out))))))

;; (defmulti handle (fn [event] (-> event :httpMethod)))

;; (defmethod handle "GET" [event]
;;   {:status 200
;;    :body   "+"})

;; (defmethod handle "POST" [event]
;;   {:status 200
;;    :body (handle-request
;;           (fn [event]
;;             (let [expr (:expr (json/read-str (:body event)
;;                                              :key-fn keyword))]
;;               {:expr expr
;;                :result (infix expr)})))})

;; (defmethod handle "PUT" [event]
;;   {:status 200
;;    :body "success"})

;; (defmethod handle "DELETE" [event]
;;   {:status 200
;;    :body "success"})

;; (defmethod handle nil [event]
;;   {:status 400
;;    :body   "Invalid method!"})

;; (defn -handler [input, context]
;;   {"statusCode" 200
;;    "body"  (str
;;             (handle
;;              (json/read-str
;;               (json/write-str input)
;;               :key-fn keyword)))
;;    "headers" {"X-Powered-By" "AWS Lambda & Serverless"}})

(def -handler
  {"statusCode" 200
   "body"  (handle-request
            (fn [input context]
              (let [expr (:expr (json/read-str
                                 (json/write-str input)
                                 :key-fn keyword))]
                {:expr expr
                 :result (infix expr)})))
   "headers" {"X-Powered-By" "AWS Lambda & Serverless"}})