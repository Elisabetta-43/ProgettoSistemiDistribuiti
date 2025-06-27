# Distributed Systems Project 2024-2025 - Stateless Minds

## Youth Culture Card - Description:
This project implements a distributed application for managing the "Youth Culture Card". 
The application is composed of three components:

- **Client Web**: User interface implemented in HTML and JavaScript, communicates with the Web Server 
via REST APIs for managing the contribution and vouchers. 
It allows users to manage the €500 contribution of the "Youth Culture Card" and enables:
  - Checking the status of the contribution (available, assigned, or spent);
  - Viewing the chronological list of generated vouchers with their details;
  - Generating new vouchers by specifying the amount and type of goods, modifying vouchers that have not yet been spent;
  - Consuming or deleting unused vouchers;
  - Registering new users and viewing the global system status.

- **Web Server**: Logic for managing the contribution and vouchers implemented in Java for REST APIs and 
JSON-B (Jakarta JSON Binding) for data serialization/deserialization. This server implements
REST APIs for the Web Client, modeling CRUD operations (create, retrieve, update, delete) on user and voucher data, manages resource access, avoiding conflicts when the same user generates multiple vouchers simultaneously, and communicates with the Database via a custom TCP protocol (implemented in the `ProtocolHandler` class and managed via the `TCPConnection` class).

- **Database**: In-memory key-value database implemented in Java for managing user, contribution, and voucher data.
This database communicates with the Web Server via a textual TCP protocol, supports CRUD operations
(create, retrieve, update, delete) on data, explicitly manages concurrency using a thread-safe data structure (ConcurrentHashMap) and a synchronized method for accessing the single available instance of the Database, supporting multiple Socket connections from the Web Server and handling simultaneous requests.

## Group Members
* Francesco Fracchia (915877) <f.fracchia1@campus.unimib.it>
* Elisabetta Locatelli (914621) <e.locatelli43@campus.unimib.it>
* Matteo Lorenzin (914593) <m.lorenzin2@campus.unimib.it>

## Project Structure
- `database/`: Contains the code related to the database.
- `server-web/`: Contains the code related to the web server.
- `client-web/`: Contains the code related to the web client.
- `README.md`: Project description and instructions for compilation and execution.
- `REST.md`: Documentation of the REST APIs.
- `TCP.md`: Documentation of the TCP protocol.

## Interaction Between Components
### Web Client ↔ Web Server: 
The Client sends HTTP requests to the Web Server via REST APIs.
The Web Server responds with JSON data.
### Web Server ↔ Database:
The Web Server sends TCP requests to the Database to save, modify, retrieve, or delete data.
The Database responds with JSON data.

## Compilation and Execution Instructions
### Prerequisites
- Java 21
- Maven 3.8+
- Development environment configured with the VM provided during the lab.

### Compilation
1. Open a terminal in the project's root directory.
2. Run the command:
   ```bash
   mvn clean install

## Compilation and Execution

Both the Web Server and the Database are Java applications managed with Maven. Within their respective folders, you can find the `pom.xml` file, which contains Maven's configuration for the project and manages dependencies, compilation, and execution. It is assumed that the laboratory virtual machine is used, and the `pom.xml` specifies the use of Java 21.

### Web Client

To start the Web Client, you need to use the "Live Preview" extension in Visual Studio Code, as demonstrated during the lab. This extension exposes a local server with the files contained in the `client-web` folder.

**Note**: You need to configure CORS in Google Chrome and enable the three "Enable" options in the extension settings, as shown during the eighth lab session on JavaScript (AJAX).

### Web Server

The Web Server uses Jetty and Jersey. It can be started by running `mvn jetty:run` inside the `server-web` folder. It exposes REST APIs at `localhost` on port `8080`.

### Database

The Database is a simple Java application. You can use the following Maven commands:

* `mvn clean`: to clean the folder of temporary files,
* `mvn compile`: to compile the application,
* `mvn exec:java`: to start the application (assumes the main class is `Main.java`). It listens at `localhost` on port `3030`.

## Start the Web Client: 
Open the file `ProgettoSD\client-web\index.html` in a browser.
## Start the Database: 
`java -cp target/database.jar it.unimib.sd2025.Main`.
## Start the Web Server: 
`java -cp target/server-web.jar it.unimib.sd2025.server.Main`.

------------------------------------------------------------------------------------------------

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
