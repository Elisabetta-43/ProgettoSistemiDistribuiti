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
| `conditions`| String | Condizioni per filtrare i record (opzionale) | `"status=valid"`                        |

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
| RETRIEVE | `type`, `ID`, `conditions` | Recupera un record dal database   | 
| UPDATE   | `type`, `ID`, `parameter`  | Aggiorna un record nel database   |
| DELETE   | `type`, `ID`, `conditions` | Elimina un record dal database    | 


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
Seguendo questi passaggi:
1. Il database caricherà correttamente i dati iniziali dai file JSON.
2. La documentazione sarà completa e conforme alle specifiche richieste.

---