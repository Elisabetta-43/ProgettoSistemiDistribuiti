# Distributed Systems Project 2024-2025 - TCP

## 1. Overview

- **Type:** Textual protocol  
- **Port Used:** 3030  
- **Description:** The TCP protocol is used for communication between the Web Server and the Database. 
The exchanged messages are in JSON format and represent requests and responses for CRUD operations 
(Create, Retrieve, Update, Delete) on database data.

---

## 2. Message Structure

### 2.1. Request
Requests sent from the Web Server to the Database are represented as JSON strings. 
Each request contains the following fields:

| Field       | Type   | Description                                  | Example Value                           |
|-------------|--------|----------------------------------------------|-----------------------------------------|
| `op`        | String | Operation to be performed                    | `"CREATE"`                              |
|             |        | (`CREATE`, `RETRIEVE`, `UPDATE`, `DELETE`)   |                                         |
| `type`      | String | Type of record to manage                     | `"User"`                                |
| `ID`        | String | Record identifier                            | `"RSSMRA80A01H501U"`                    |
| `parameter` | Object | Parameters for the operation (optional)      | `{"name": "Mario", "surname": "Rossi"}` |
| `condition` | String | Condition to filter records (optional)       | `"fiscalCode=RSSMRA80A01H501U"`          |

**Example Request:**
```json
{
    "op": "CREATE",
    "type": "User",
    "ID": "RSSMRA80A01H501U",
    "parameter": {
        "name": "Mario",
        "surname": "Rossi",
        "email": "mario.rossi@example.com"
    }
}
```

---

### 2.2. Response
Responses sent from the database to the web server are represented as JSON strings. 
Each response contains the following fields:

| Field        | Type   | Description                          | Example Value                        |
|--------------|--------|--------------------------------------|--------------------------------------|
| `StatusCode` | String | Status code of the operation         | `"200"`                              |
| `Message`    | String | Descriptive message of the operation | `"Operation completed successfully"` |
| `QueryResult`| Object | Query result (optional)              | `{"id": 1, "name": "John Doe"}`      |

**Example Response:**
```json
{
    "StatusCode": "200",
    "Message": "Operation completed successfully",
    "QueryResult": {
        "id": 1,
        "name": "John Doe"
    }
}
```
### 2.3 Supported Commands
| Command  | Parameters                 | Description                          | 
|----------|----------------------------|--------------------------------------|
| CREATE   | `type`, `ID`, `parameter`  | Creates a new record in the database | 
| RETRIEVE | `type`, `ID`, `condition`  | Retrieves a record from the database | 
| UPDATE   | `type`, `ID`, `parameter`  | Updates a record in the database     |
| DELETE   | `type`, `ID`, `condition`  | Deletes a record from the database   | 


## Example Request "CREATE":
`{"op": "CREATE", "type": "User", "ID": "RSSMRA80A01H501U", "parameter": {...}}`

## Example Request "RETRIEVE":
`{"op": "RETRIEVE", "type": "User", "ID": "RSSMRA80A01H501U"}`

## Example Request "UPDATE":
`{"op": "UPDATE", "type": "User", "ID": "RSSMRA80A01H501U", "parameter": {...}}`

## Example Request "DELETE":
`{"op": "DELETE", "type": "User", "ID": "RSSMRA80A01H501U"}`

---

### **Conclusion**
By following this steps the Database will correctly process CRUD operations based on the protocol.

--------------------------------------------------------------------------------------------------

# Progetto Sistemi Distribuiti 2024-2025 - TCP

## 1. Panoramica

- **Tipo:** Protocollo testuale  
- **Porta Utilizzata:** 3030  
- **Descrizione:** Il protocollo TCP è utilizzato per la comunicazione tra il server
Web e il database. I messaggi scambiati sono in formato JSON e rappresentano richieste
e risposte per operazioni CRUD (Create, Retrieve, Update, Delete) sui dati del database.

---

## 2. Struttura dei Messaggi

### 2.1. Richiesta
Le richieste inviate dal server Web al database sono rappresentate come stringhe JSON. 
Ogni richiesta contiene i seguenti campi:

| Campo       | Tipo   | Descrizione                                  | Esempio Valore                          |
|-------------|--------|----------------------------------------------|-----------------------------------------|
| `op`        | String | Operazione da eseguire                       | `"CREATE"`                              |
|             |        | (`CREATE`, `RETRIEVE`, `UPDATE`, `DELETE`)   |                                         |
| `type`      | String | Tipo di record da gestire                    | `"User"`                                |
| `ID`        | String | Identificativo del record                    | `"RSSMRA80A01H501U"`                    |
| `parameter` | Object | Parametri per l'operazione (opzionale)       | `{"name": "Mario", "surname": "Rossi"}` |
| `condition` | String | Condizione per filtrare i record (opzionale) | `"fiscalCode=RSSMRA80A01H501U"`          |

**Esempio di Richiesta:**
```json
{
    "op": "CREATE",
    "type": "User",
    "ID": "RSSMRA80A01H501U",
    "parameter": {
        "name": "Mario",
        "surname": "Rossi",
        "email": "mario.rossi@example.com"
    }
}
```
### 2.2. Risposta
Le risposte inviate dal database al server Web sono rappresentate come stringhe JSON. 
Ogni risposta contiene i seguenti campi:

| Campo        | Tipo    | Descrizione                           | Esempio Valore                  |
|--------------|---------|---------------------------------------|---------------------------------|
| `StatusCode` | String  | Codice di stato dell'operazione       | `"201"`                         |
| `Message`    | String  | Messaggio descrittivo dell'operazione | `"Record created successfully"` |
| `QueryResult`| Object  | Risultato della query (opzionale)     | `null`                          |

**Esempio di Risposta:**
```json
{
    "StatusCode": "201",
    "Message": "Record created successfully",
    "QueryResult": null
}
```

### 2.3 Comandi Supportati
| Comando  | Parametri                  | Descrizione                       | 
|----------|----------------------------|-----------------------------------|
| CREATE   | `type`, `ID`, `parameter`  | Crea un nuovo record nel database | 
| RETRIEVE | `type`, `ID`, `condition`  | Recupera un record dal database   | 
| UPDATE   | `type`, `ID`, `parameter`  | Aggiorna un record nel database   |
| DELETE   | `type`, `ID`, `condition`  | Elimina un record dal database    | 


## Esempio Richiesta "CREATE":
`{"op": "CREATE", "type": "User", "ID": "RSSMRA80A01H501U", "parameter": {...}}`

## Esempio Richiesta "RETRIEVE":
`{"op": "RETRIEVE", "type": "User", "ID": "RSSMRA80A01H501U"}`

## Esempio Richiesta "UPDATE":
`{"op": "UPDATE", "type": "User", "ID": "RSSMRA80A01H501U", "parameter": {...}}`

## Esempio Richiesta "DELETE":
`{"op": "DELETE", "type": "User", "ID": "RSSMRA80A01H501U"}`

---

### **Conclusione**
Seguendo questi passaggi il Database caricherà correttamente i dati iniziali dai file JSON.

---