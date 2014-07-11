(ns sixdegrees.core)

(import '(java.net URL)
        '(java.lang StringBuilder)
        '(java.io BufferedReader InputStreamReader))

(require 'clojure.set)

(defn pmapcat
  [f batches]
  "from http://blog.zololabs.com/tag/utilities/"
  (->> batches
       (pmap f)
       (apply concat)
       doall))

(defn extract-wiki-links
  [url]
  (let [content (slurp url)]
    (map #(str "http://en.wikipedia.org" (second %)) (re-seq #"href=\"(/wiki/[^\":]+)\"" content))))

(def kevin-bacon-url "http://en.wikipedia.org/wiki/Kevin_Bacon")

(defn pages-to-kevin-bacon-rec
  [urls visited-urls current-count]
  (let [urls-to-try (clojure.set/difference (set urls) (set visited-urls))]
    (if (contains? (set urls-to-try) kevin-bacon-url) current-count
          (let [next-urls (pmapcat extract-wiki-links urls-to-try)]
              (recur next-urls (clojure.set/union urls-to-try visited-urls) (inc current-count))))))

(defn pages-to-kevin-bacon
  [url]
  (if (= url kevin-bacon-url) 0
    (pages-to-kevin-bacon-rec [url] #{} 0)))

(defn -main [& args]
  (println (pages-to-kevin-bacon (first args)))
  (System/exit 0))
