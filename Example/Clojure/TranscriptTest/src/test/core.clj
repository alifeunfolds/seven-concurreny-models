(ns test.core
  (:require [test.jabberwocky :refer :all]
            [test.potato      :refer :all]
            [clj-http.client  :as client]))

(def endpoint "http://localhost:3000")

(defn mk-url
  ([path]
    (str endpoint path))
  ([session-id path]
    (mk-url (str "/session/" session-id path))))

(defn deliver-transcript [transcript]
  (let [session-id (:body (client/post (mk-url "/session/create")))]
    (doseq [n (range (count transcript))]
      (client/put (mk-url session-id (str "/snippet/" n)) {:body (nth transcript n)}))
    session-id))

(defn print-translation [session-id]
  (doseq [n (iterate inc 0)]
    (println (:body (client/get (mk-url session-id (str "/translation/" n)))))))

(def jabberwocky-session (future (deliver-transcript jabberwocky)))
(def potato-session (future (deliver-transcript potato)))

(future (print-translation @jabberwocky-session))
(future (print-translation @potato-session))

(defn -main [& args]
  nil)