package it.unimib.sd2025;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//concurrent package necessario per gestire l'accesso concorrente al database in un ambiente multi-threaded

/*
Definizione di una classe Singleton che consente di creare una sola istanza 
dell'oggetto durante l'esecuzione del programma
*/

public class Database {
    private ConcurrentHashMap<String,String> dati;

     //variabile statica (condivisa da tutte le chiamate della classe) che contiene l'unica istanza del database
    private static Database instance;

    /* 
    Costruttore privato fondamentale, affinché non sia possibile creare un oggetto database usando una new() 
    dall'esterno e che, quindi, vengano create più istanze
    */
    private Database(){
        dati=new ConcurrentHashMap<> ();
    }

    //Metodo statico che restituisce l'istanza del database
    public static synchronized Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    //Metodo per aggiungere una coppia chiave-valore al database
    public void addData(String key, String value){
        dati.put(key, value);
    }

    //Metodo per ottenere un valore associato a una chiave
    public String getData(String key){
        return dati.get(key);
    }

    //Metodo per ottenere tutte le coppie chiave-valore del database
    public Map<String, String> getAllData() {
        return new ConcurrentHashMap<>(dati);
    }

    //Metodo per verificare se una chiave esiste nel database
    public boolean containsKey(String key) {
        return dati.containsKey(key);
    }

    //Metodo per rimuovere una coppia chiave-valore dal database
    public void removeData(String key) {
        dati.remove(key);
    }

    
}
