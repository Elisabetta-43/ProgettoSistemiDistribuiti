# Progetto Sistemi Distribuiti 2024-2025 - Stateless Minds

# Carta Cultura Giovani - Descrizione:
Questo progetto implementa un'applicazione distribuita per la gestione della "Carta Cultura Giovani". 
L'applicazione è composta da tre componenti:

- **Client Web**: Interfaccia utente, implementata in HTML e javascript, comunica con il server Web 
tramite API REST per la gestione del contributo e dei buoni. 
Permette ai utenti di gestire il contributo di 500€ della "Carta Cultura Giovani" e consente di:
- controllare lo stato del contributo(disponibile, assegnato o speso);
- visualizzare l'elenco cronologico dei buoni generati con i relativi dettagli;
- generare nuovi buoni specificando importo e tipo di bene, modificare buoni non ancora spesi;
- consumare o eliminare buoni non consumati;
- registrare nuovi utenti e visualizzare lo stato globale del sistema. 

- **Server Web**: Logica di gestione del contributo e dei buoni implementata in Java per le API REST e 
JSON-B (Jakarta JSON Binding) per la serializzazione/deserializzazione dei dati. Tale Server implementa
le API REST per il Client Web, modellando le operazioni CRUD (create, retrieve, update, delete) sui dati degli utenti e dei voucher (buoni), gestice l'accesso alle risorse, evitando conflitti quando lo stesso utente genera più buoni contemporaneamente, comunica con il Database tramite un protocollo TCP personalizzato (implementato nella classe `ProtocolHandler` e gestito tramite la classe `TCPConnection`). 

- **Database**: Database chiave-valore in-memory implementato in java per la gestione dei dati di 
utenti, contributi e voucher (buoni).
Tale Database comunica con il Server Web tramite un protocollo TCP testuale, supporta operazioni CRUD
(create, retrieve, update, delete) sui dati, getsisce la concorrenza esplicitamente tramite l'utilizzo 
di una struttura dati thread-safe (ConcurrentHashMap) e l'utilizzo di un metodo synchronized per 
l'accesso all'unica istanza disponibile del Database, supportando più connessioni Socket dal Server 
Web e la gestione di richieste simultanee.

## Componenti del gruppo
* Francesco Fracchia (915877) <f.fracchia1@campus.unimib.it>
* Elisabetta Locatelli (914621) <e.locatelli43@campus.unimib.it>
* Matteo Lorenzin (914593) <m.lorenzin2@campus.unimib.it>

## Struttura del Progetto
- `database/`: Contiene il codice relativo al database.
- `server-web/`: Contiene il codice relativo al server web.
- `client-web/`: Contiene il codice relativo al client web.
- `README.md`: Descrizione del progetto e istruzioni per la compilazione ed esecuzione.
- `REST.md`: Documentazione delle API REST.
- `TCP.md`: Documentazione del protocollo TCP.

## Interazione tra i componenti 
### Client Web ↔ Server Web: 
Il Client invia richieste HTTP al Server Web tramite API REST.
Il Server Web risponde con dati JSON.
### Server Web ↔ Database:
Il Server Web invia richieste TCP al Database per salvare, modificare, recuperare o eliminare i dati.
Il Database risponde con dati JSON.

## Istruzioni per la Compilazione ed Esecuzione
### Prerequisiti
- Java 21
- Maven 3.8+
- Ambiente di sviluppo configurato con la VM fornita durante il laboratorio.

### Compilazione
1. Aprire un terminale nella directory principale del progetto.
2. Eseguire il comando:
   ```bash
   mvn clean install

## Compilazione ed esecuzione

Sia il Server Web sia il Database sono applicazioni Java gestite con Maven. All'interno delle rispettive cartelle si può trovare il file `pom.xml` in cui è presenta la configurazione di Maven per il progetto e con cui si gestiscono le dipendenza, la compilazione e l'esecuzione. Si presuppone l'utilizzo della macchina virtuale di laboratorio, per cui nel `pom.xml` è specificato l'uso di Java 21.

### Client Web

Per avviare il client Web è necessario utilizzare l'estensione "Live Preview" su Visual Studio Code, come mostrato durante il laboratorio. Tale estensione espone un server locale con i file contenuti nella cartella `client-web`.

**Attenzione**: è necessario configurare CORS in Google Chrome e abilitare i tre Enable dalle impostazioni dell'estensione, come mostrato nell'ottevo laaboratorio del corso?? su Javascript (AJAX).

### Server Web

Il server Web utilizza Jetty e Jersey. Si può avviare eseguendo `mvn jetty:run` all'interno della cartella `server-web`. Espone le API REST all'indirizzo `localhost` alla porta `8080`.

### Database

Il database è una semplice applicazione Java. Si possono utilizzare i seguenti comandi Maven:

* `mvn clean`: per ripulire la cartella dai file temporanei,
* `mvn compile`: per compilare l'applicazione,
* `mvn exec:java`: per avviare l'applicazione (presuppone che la classe principale sia `Main.java`). Si pone in ascolto all'indirizzo `localhost` alla porta `3030`.


## Avviare il Client Web: 
aprire il file `ProgettoSD\client-web\index.html` in un browser.
## Avviare il Database: 
`java -cp target/database.jar it.unimib.sd2025.Main`.
## Avviare il Server Web: 
`java -cp target/server-web.jar it.unimib.sd2025.server.Main`.
