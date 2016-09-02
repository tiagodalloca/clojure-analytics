(time
 (let [butter-promise (promise)]
   (doseq [butter [10 123 119 1939 581 19 0 1039 45 86 0]]
     (future (if-let
              [satisfactory-butter
                ((fn [x] (cond (= x 86) x)) butter)]
              (deliver butter-promise satisfactory-butter))))
   (println "Multiple-threaded says:" @butter-promise)))

(time
  (doseq [butter [10 123 119 1939 581 19 0 1039 45 86 0]]
    (if-let
     [satisfactory-butter
       ((fn [x] (cond (= x 86) x)) butter)]
     (println "Single-threaded says:" satisfactory-butter))))


(defrecord Operacao
  [nome-manipulador valor-operado])

(defrecord Conta
  [saldo operacoes])

(defrecord Pessoa
  [nome])

(defn realizar-operacao [conta operacao]
  (if (and
        (satisfies? Conta conta)
        (satisfies? Operacao operacao))
    ; (->
    ;   conta
    ;   (update :saldo #(+ % (:valor-operado operacao)))
    ;   (update :operacoes #(conj % operacao)))))
    (let [conta (update conta :saldo #(+ % (:valor-operado operacao)))]
      (update conta :operacoes #(conj % operacao))))
  conta)

(defn realizar-operacoes
  ([conta consumidor coll-valores]
   (if (and
         (instance? Pessoa consumidor)
         (instance? Conta conta)
         (coll? coll-valores))
     (->
       (update conta :operacoes
         (fn [old]
           (concat old
              (map #(->Operacao (:nome consumidor) %) coll-valores))))
       (update :saldo
         (fn [old]
           (reduce + old coll-valores))))))

  ([conta consumidor coll-valores func]
   (if (and
         (instance? Pessoa consumidor)
         (instance? Conta conta)
         (coll? coll-valores))
     (loop [valor coll-valores
             conta conta]))))

(defn fazer-calculo-demorado [n]
  (loop [ a 1
          b 1
          ct 2]
    (if (<= ct n)
      (recur b (+' a b) (inc ct))
      b)))

(time
  (do
    (let
      [coisas-processadas
        (for [numero (range 2000 2050)]
            (future (fazer-calculo-demorado numero)))]
      (doseq [resultado coisas-processadas]
        (println @resultado)))))

(time
  (doseq [numero (range 2000 2050)]
      @(future (->> numero
                (fazer-calculo-demorado)
                (println)))))

(time
  (doseq [numero (range 2000 2050)]
    (println (fazer-calculo-demorado numero))))
