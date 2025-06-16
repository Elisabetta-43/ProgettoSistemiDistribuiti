package it.unimib.sd2025;

import java.util.HashMap;
import java.util.Map;

/*
Definizione di una classe Singleton che consente di creare una sola istanza 
dell'oggetto durante l'esecuzione del programma
*/

public class database {
    private HashMap<String,String> dati;

     //variabile statica (condivisa da tutte le chiamate della classe) che contiene l'unica istanza del database
    private static database db_instance;

    /* 
    Costruttore privato fondamentale, affinché non sia possibile creare un oggetto database usando una new() 
    dall'esterno e che, quindi, vengano create più istanze
    */
    private database(){
        dati=new HashMap<String,String> ();
    }
}