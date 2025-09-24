# Progetto Sistemi Distribuiti 2024-2025 - API REST

**Attenzione**:
l'unica rappresentazione ammessa è in formato JSON.  
Si assumono sempre gli header:

`Content-Type: application/json`
`Accept: application/json`

## GetUser - `../user?id=valore`

### GET

**Descrizione**
Recupera i dati anagrafici e il ruolo dell'utente (utente o amministratore)

**Header**
Query param: `id` (valore univoco dell'utente)

**Risposta JSON**
```json
{
  "name": "Mario",
  "surname": "Rossi",
  "email": "mario.rossi@example.com",
  "fiscalCode": "RSSMRA80A01H501U",
  "admin": false,
  "contributions": 500.0,
  "type": "user",
  "nextVoucherId": 1
}
```

**Codici di stato**  
- `200 OK`
- `404 Not Found`
- `500 Internal Server Error`


## GetUserInfo - `../user/{id}`

### GET

**Descrizione**
Recupera i dati relativi allo stato contributivo dell’utente:
- `available`: importo non ancora assegnato
- `assigned`: importo assegnato, non consumato
- `spent`: importo già consumato

**Parametri**
- **Path**
- `id`: codice fiscale (stringa, 16 caratteri)

**Risposta JSON**
```json
{
  "available": 250,
  "assigned": 150,
  "spent": 100
}
```

**Codici di stato**  
- `200 OK`
- `404 Not Found`
- `500 Internal Server Error`

## GetVoucherList - `../user/{id}/VoucherList`

### GET

**Descrizione**
Restituisce la lista cronologica dei buoni generati dall'utente, con importo, stato e tipo.

**Parametri**
- **Path**
  - `id`: codice fiscale (stringa, 16 caratteri)

**Risposta JSON**
```json
[
  {
    "idVoucher": "abc123",
    "amount": 50,
    "category": "cinema",
    "consumed": false,
    "creationDate": "2024-06-01",
    "consumptionDate": null
  },
]
```

**Codici di stato**  
- `200 OK`
- `404 Not Found`
- `500 Internal Server Error`

## CreateNewVoucher - `../voucher`

### POST

**Descrizione**
Genera un nuovo buono con importo in euro e categoria di buono

**Header**
Query Parameter: `fiscalCode`

**Body richiesta**
```json
{
  "amount": 50,
  "category": "cinema"
}
```
**Codici di stato**  
- `201 Created`
- `400 Bad Request`
- `404 Not Found`
- `500 Internal Server Error`

## GetVoucherInfo - `../voucher/{id}`

### GET

**Descrizione**
Visualizza in dettaglio un buono (importo, tipologia, stato, data di creazione, data di consumo)

**Parametri**
- **Path**
- `id`: codice identificativo del buono

**Risposta JSON**
```json
{
  "id": "RSSMRA80A01H501U/1",
  "amount": 50,
  "category": "cinema",
  "consumed": false,
  "creationDate": "2024-06-01",
  "consumptionDate": null
}
```

**Codici di stato**  
- `200 OK`
- `404 Not Found`
- `500 Internal Server Error`

## ConsumeVoucher - `../voucher`

### POST

**Descrizione**
Consuma un buono.

**Body richiesta**
- `voucher`
```json
{
  "id": "RSSMRA80A01H501U/1",
  "amount": 50,
  "category": "cinema",
  "consumed": false,
  "creationDate": "2024-06-01",
  "consumptionDate": null
}
```

**Codici di stato**  
- `200 OK`
- `400 Bad Request`
- `404 Not Found`
- `500 Internal Server Error`

## DeleteVoucher - `../voucher`

### DELETE

**Descrizione**
Elimina un buono se non è ancora stato consumato

**Header**
Query Parameter: `contributions`

**Body richiesta**
- `voucher`
```json
{
  "id": "RSSMRA80A01H501U/1",
  "amount": 50,
  "category": "cinema",
  "consumed": false,
  "creationDate": "2024-06-01",
  "consumptionDate": null
}
```

**Codici di stato**  
- `200 OK`
- `400 Bad Request`
- `404 Not Found`
- `500 Internal Server Error`

## UpdateVoucher - `../voucher/{category}`

### PUT

**Descrizione**
Modifica un buono assegnato ma non ancora speso, permettendo il cambio di tipologia

**Parametri**
- **Path**
- `category`: categoria del buono

**Body richiesta**
```json
{
  "id": "RSSMRA80A01H501U/1",
  "amount": 50,
  "category": "cinema",
  "status": true
}
```

**Codici di stato**  
- `200 OK`
- `400 Bad Request`
- `404 Not Found`
- `500 Internal Server Error`

## SignIn - `../user`

### POST

**Descrizione**
Registra un nuovo utente con i cammpi: nome, cognome, email, codice fiscale

**Body richiesta**:
```json
{
  "name": "Mario",
  "surname": "Rossi",
  "email": "mario.rossi@example.com",
  "fiscalCode": "RSSMRA80A01H501U"
}
```
**Codici di stato**  
- `201 Created`
- `400 Bad Request`
- `500 Internal Server Error`

## GetApplicationState - `../user/admin`

### GET

**Descrizione**
Restituisce una panoramica globale del sistema:
- Numero utenti registrati
- Contributi totali (disponibili, assegnati, spesi)
- Buoni generati (totali, consumati, non consumati)

**Risposta JSON**:
```json
{
  "userCount": 10,
  "contributions": {
    "available": 1000,
    "assigned": 500,
    "spent": 1500
  },
  "vouchers": {
    "total": 20,
    "consumed": 12,
    "unconsumed": 8
  }
}
```
**Codici di stato**  
- `200 OK`
- `404 Not Found`
- `500 Internal Server Error`