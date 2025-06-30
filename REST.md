# Distributed Systems Project 2024-2025 - REST API

**Note**: Only JSON format is supported.  
Therefore, the following headers are always assumed:

`Content-Type: application/json` e `Accept: application/json`

---

## GetUserInfo - `../user/{id}`

### GET

**Description**  
Retrieves personal information and user role (user or administrator).  
If the user is **not** an admin, the response also includes contribution status:
- `available`: unassigned amount  
- `assigned`: assigned but not yet used  
- `spent`: amount already used  

**Parameters**
- **Path**
  - `id`: fiscal code (string, 16 characters)

**JSON Response**
```json
{
  "name": "Mario",
  "surname": "Rossi",
  "email": "mario.rossi@example.com",
  "fiscalCode": "RSSMRA80A01H501U",
  "admin": false,
  "available": 250,
  "assigned": 150,
  "spent": 100
}
```

**Status Codes**
- `200 OK`
- `404 Not Found`

---

## GetVoucherList - `../user/{id}/vouchers`

### GET

**Description**  
Returns a chronological list of vouchers created by the user, including amount, status, and category.

**Parameters**
- **Path**
  - `id`: fiscal code (string, 16 characters)

**JSON Response**
```json
[
  {
    "id": "abc123",
    "amount": 50,
    "category": "cinema",
    "consumed": false,
    "creationDate": "2024-06-01",
    "consumptionDate": null
  }
]
```

**Status Codes**
- `200 OK`
- `404 Not Found`

---

## CreateNewVoucher - `../user/{id}/vouchers`

### POST

**Description**  
Generates a new voucher with a specified amount and category.

**Request Body**
```json
{
  "amount": 50,
  "category": "cinema"
}
```

**Status Codes**
- `201 Created`
- `400 Bad Request`

---

## GetVoucherInfo - `../vouchers/{voucherId}`

### GET

**Description**  
Displays details of a voucher (amount, category, status, creation date, and consumption date).

**Parameters**
- **Path**
  - `voucherId`: voucher identifier

**JSON Response**
```json
{
  "id": "abc123",
  "amount": 50,
  "category": "cinema",
  "consumed": false,
  "creationDate": "2024-06-01",
  "consumptionDate": null
}
```

**Status Codes**
- `200 OK`
- `404 Not Found`

---

## ConsumeVoucher - `../user/{id}/vouchers/{voucherId}/consume`

### POST

**Description**  
Consumes a voucher.

**Parameters**
- **Path**
  - `id`: fiscal code (string, 16 characters)
  - `voucherId`: voucher identifier

**Status Codes**
- `200 OK`
- `404 Not Found`

---

## DeleteVoucher - `../user/{id}/vouchers/{voucherId}`

### DELETE

**Description**  
Deletes a voucher if it has not been consumed.

**Parameters**
- **Path**
  - `id`: fiscal code (string, 16 characters)
  - `voucherId`: voucher identifier

**Status Codes**
- `200 OK`
- `400 Bad Request`
- `404 Not Found`

---

## UpdateVoucher - `../user/{id}/vouchers/{voucherId}`

### PUT

**Description**  
Updates a voucher (assigned but not yet consumed), allowing the category to be changed.

**Parameters**
- **Path**
  - `id`: fiscal code (string, 16 characters)
  - `voucherId`: voucher identifier

**Request Body**
```json
{
  "category": "books"
}
```

**Status Codes**
- `200 OK`
- `400 Bad Request`
- `404 Not Found`

---

## SignIn - `../user`

### POST

**Description**  
Registers a new user with name, surname, email, and fiscal code.

**Request Body**
```json
{
  "name": "Mario",
  "surname": "Rossi",
  "email": "mario.rossi@example.com",
  "fiscalCode": "RSSMRA80A01H501U"
}
```

**Status Codes**
- `201 Created`
- `400 Bad Request`

---

## GetApplicationState - `../user/admin`

### GET

**Description**  
Returns a global overview of the system:
- Number of registered users
- Total contributions (available, assigned, spent)
- Vouchers generated (total, consumed, unconsumed)

**JSON Response**
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

**Status Codes**
- `200 OK`
- `404 Not Found`

----------------------------------------------------------------------------------

# Progetto Sistemi Distribuiti 2024-2025 - API REST

**Attenzione**: l'unica rappresentazione ammessa è in formato JSON. 
Pertanto vengono sempre assunti gli header:

`Content-Type: application/json` e `Accept: application/json`.

---

## GetUserInfo - `../user/{id}`

### GET

**Descrizione**
Recupera i dati anagrafici e il ruolo dell'utente (utente o amministratore)
Se l'uutente non è un admin, vengono restituiti anche i dati relativi allo stato contributivo dell’utente:
- `available`: importo non ancora assegnato  
- `assigned`: importo assegnato, non consumato 
- `spent`: importo già consumato 

**Parametri**
- **Path**
- `id`: codice fiscale (stringa, 16 caratteri)

**Risposta JSON**
```json
{
  "name": "Mario",
  "surname": "Rossi",
  "email": "mario.rossi@example.com",
  "fiscalCode": "RSSMRA80A01H501U",
  "admin": false,
  "available": 250,
  "assigned": 150,
  "spent": 100
}
```

**Codici di stato restituiti**  
- `200 OK`
- `404 Not Found`

---

## GetVoucherList - `../user/{id}/vouchers`

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
    "id": "abc123",
    "amount": 50,
    "category": "cinema",
    "consumed": false,
    "creationDate": "2024-06-01",
    "consumptionDate": null
  },
]
```

**Codici di stato restituiti**  
- `200 OK`
- `404 Not Found`

---

## CreateNewVoucher - `../user/{id}/vouchers`

### POST

**Descrizione**
Genera un nuovo buono con importo in euro e categoria di buono

**Body richiesta**
```json
{
  "amount": 50,
  "category": "cinema"
}
```
**Codici di stato restituiti**  
- `201 Created`
- `400 Bad Request`

---

## GetVoucherInfo - `../vouchers/{voucherId}`

### GET

**Descrizione**
Visualizza in dettaglio un buono (importo, tipologia, stato, data di creazione, data di consumo)

**Parametri**
- **Path**
- `voucherId`: codice identificativo del buono

**Risposta JSON**
```json
{
  "id": "abc123",
  "amount": 50,
  "category": "cinema",
  "consumed": false,
  "creationDate": "2024-06-01",
  "consumptionDate": null
}
```

**Codici di stato restituiti**  
- `200 OK`
- `404 Not Found`

---

## ConsumeVoucher - `../user/{id}/vouchers/{voucherId}/consume`

### POST

**Descrizione**
Consuma un buono.

**Parametri**:
- **Path**
- `id`: codice fiscale (stringa, 16 caratteri)
- `voucherId`: codice identificativo del buono

**Codici di stato restituiti**  
- `200 OK`
- `404 Not Found`

---

## DeleteVoucher - `../user/{id}/vouchers/{voucherId}`

### DELETE

**Descrizione**
Elimina un buono se non è ancora stato consumato

**Parametri**
- **Path**
- `id`: codice fiscale (stringa, 16 caratteri)
- `voucherId`: codice identificativo del buono

**Codici di stato restituiti**  
- `200 OK`
- `400 Bad Request`
- `404 Not Found`

---

## UpdateVoucher - `../user/{id}/vouchers/{voucherId}`

### PUT

**Descrizione**
Modifica un buono assegnato ma non ancora speso, permettendo il cambio di tipologia

**Parametri**
- **Path**
- `id`: codice fiscale (stringa, 16 caratteri)
- `voucherId`: codice identificativo del buono

**Body richiesta**
```json
{
  "category": "books"
}
```

**Codici di stato restituiti**  
- `200 OK`
- `400 Bad Request`
- `404 Not Found`

---

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
**Codici di stato restituiti**  
- `201 Created`
- `400 Bad Request`

---

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
**Codici di stato restituiti**  
- `200 OK`
- `404 Not Found`

---