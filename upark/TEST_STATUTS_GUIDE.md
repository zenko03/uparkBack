# Guide de Test - Gestion des Statuts de Réservations

Ce guide présente des exemples de requêtes pour tester la gestion automatique et manuelle des statuts de réservations.

## Configuration

**Base URL:** `http://localhost:8080/api`

**Headers:**
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer {votre_token_jwt}"
}
```

---

## 1. Création d'une Réservation

### Requête
```http
POST /api/reservations
Content-Type: application/json

{
  "userId": 7,
  "parkingId": 1,
  "startDateTime": "2025-11-15T10:00:00",
  "endDateTime": "2025-11-15T18:00:00",
  "paymentMethod": "espèces",
  "selectedVehicles": [
    {
      "vehicleTypeId": 1,
      "quantity": 1
    }
  ]
}
```

### Réponse Attendue
```json
{
  "Id_Reservation": 11,
  "totalPrice": 20000.00,
  "creationDate": "2025-11-13T14:30:00",
  "paymentDate": null,
  "startDateTime": "2025-11-15T10:00:00",
  "endDateTime": "2025-11-15T18:00:00",
  "paymentMethod": "espèces",
  "reservationStatus": {
    "Id_Reservation_status": 1,
    "label": "à venir",
    "value": 10
  }
}
```

**Statut initial:** `"à venir"` (10) car la date de début est dans le futur.

---

## 2. Consulter les Réservations d'un Utilisateur

### Requête
```http
GET /api/reservations/user/7
```

### Réponse Attendue
```json
[
  {
    "id": 11,
    "totalPrice": 20000.00,
    "creationDate": "2025-11-13T14:30:00",
    "startDateTime": "2025-11-15T10:00:00",
    "endDateTime": "2025-11-15T18:00:00",
    "status": "à venir",
    "parking": {
      "id": 1,
      "name": "Parking Centre Ville Analakely",
      "address": "SRID=4326;POINT(47.5060 -18.9137)"
    }
  }
]
```

**Note:** Le statut est calculé dynamiquement en fonction de la date actuelle.

---

## 3. Annuler une Réservation

### Requête
```http
POST /api/reservations/11/cancel
```

### Réponse Attendue
```json
{
  "Id_Reservation": 11,
  "totalPrice": 20000.00,
  "startDateTime": "2025-11-15T10:00:00",
  "endDateTime": "2025-11-15T18:00:00",
  "reservationStatus": {
    "Id_Reservation_status": 4,
    "label": "Annulée",
    "value": 25
  }
}
```

**Statut après annulation:** `"Annulée"` (25)

---

## 4. Mettre à Jour le Statut Manuellement

### Requête
```http
PUT /api/reservations/11/status/2
```

**Paramètres:**
- `11` : ID de la réservation
- `2` : ID du statut "En cours"

### Réponse Attendue
```json
{
  "Id_Reservation": 11,
  "totalPrice": 20000.00,
  "startDateTime": "2025-11-15T10:00:00",
  "endDateTime": "2025-11-15T18:00:00",
  "reservationStatus": {
    "Id_Reservation_status": 2,
    "label": "En cours",
    "value": 15
  }
}
```

---

## 5. Filtrer les Réservations par Statut

### Requête - Réservations "à venir"
```http
GET /api/reservations/filter?statusId=1
```

### Requête - Réservations "En cours"
```http
GET /api/reservations/filter?statusId=2
```

### Requête - Réservations "Terminée"
```http
GET /api/reservations/filter?statusId=3
```

### Requête - Réservations "Annulée"
```http
GET /api/reservations/filter?statusId=4
```

### Réponse Attendue
```json
[
  {
    "Id_Reservation": 5,
    "totalPrice": 20000.00,
    "startDateTime": "2025-11-13T12:00:00",
    "endDateTime": "2025-11-14T12:00:00",
    "reservationStatus": {
      "label": "En cours",
      "value": 15
    }
  }
]
```

---

## 6. Filtrer par Utilisateur et Statut

### Requête
```http
GET /api/reservations/filter?userId=7&statusId=1
```

**Résultat:** Toutes les réservations "à venir" de l'utilisateur 7.

---

## 7. Filtrer par Plage de Dates

### Requête
```http
GET /api/reservations/filter/by-date-range?startDate=2025-11-01T00:00:00&endDate=2025-11-30T23:59:59
```

**Résultat:** Toutes les réservations entre le 1er et 30 novembre 2025.

---

## 8. Vérifier la Disponibilité

### Requête
```http
POST /api/reservations/check-availability
Content-Type: application/json

{
  "parkingId": 1,
  "startDateTime": "2025-11-15T10:00:00",
  "endDateTime": "2025-11-15T18:00:00",
  "selectedVehicles": [
    {
      "vehicleTypeId": 1,
      "quantity": 1
    }
  ]
}
```

### Réponse Attendue
```json
true
```

**Résultat:** `true` si disponible, `false` sinon.

---

## 9. Calculer le Prix

### Requête
```http
POST /api/reservations/calculate-price
Content-Type: application/json

{
  "parkingId": 1,
  "startDateTime": "2025-11-15T10:00:00",
  "endDateTime": "2025-11-15T18:00:00",
  "selectedVehicles": [
    {
      "vehicleTypeId": 1,
      "quantity": 2
    }
  ]
}
```

### Réponse Attendue
```json
40000.00
```

**Calcul:** 2500 Ar/h × 8 heures × 2 véhicules = 40 000 Ar

---

## Scénarios de Test Automatique

### Scénario 1 : Cycle de Vie Complet d'une Réservation

```bash
# 1. Créer une réservation pour dans 1 heure
POST /api/reservations
{
  "startDateTime": "2025-11-13T16:00:00",
  "endDateTime": "2025-11-13T18:00:00"
}
# Statut attendu: "à venir" (10)

# 2. Attendre que l'heure de début soit passée + 5 min (scheduler)
# Statut attendu après scheduler: "En cours" (15)

# 3. Attendre que l'heure de fin soit passée + 5 min (scheduler)
# Statut attendu après scheduler: "Terminée" (20)
```

### Scénario 2 : Annulation d'une Réservation Future

```bash
# 1. Créer une réservation pour demain
POST /api/reservations
{
  "startDateTime": "2025-11-14T10:00:00",
  "endDateTime": "2025-11-14T18:00:00"
}
# Statut: "à venir" (10)

# 2. Annuler la réservation
POST /api/reservations/{id}/cancel
# Statut attendu: "Annulée" (25)
```

### Scénario 3 : Vérification du Scheduler

```bash
# 1. Vérifier les logs de l'application toutes les 5 minutes
# Rechercher dans les logs:
 Début de la mise à jour automatique des statuts de réservations
 Réservation ID X : 'à venir' → 'En cours'
 Réservation ID Y : 'En cours' → 'Terminée'
✨ Mise à jour automatique terminée : N réservation(s) mise(s) à jour
```

---

## Codes d'Erreur

| Code | Statut | Description |
|------|--------|-------------|
| 200 | OK | Requête réussie |
| 201 | Created | Ressource créée avec succès |
| 400 | Bad Request | Données invalides |
| 404 | Not Found | Réservation non trouvée |
| 500 | Internal Server Error | Erreur serveur |

---

## Collection Postman

Voici un fichier JSON pour importer dans Postman :

```json
{
  "info": {
    "name": "UPark - Gestion Statuts Réservations",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Créer une réservation",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"userId\": 7,\n  \"parkingId\": 1,\n  \"startDateTime\": \"2025-11-15T10:00:00\",\n  \"endDateTime\": \"2025-11-15T18:00:00\",\n  \"paymentMethod\": \"espèces\",\n  \"selectedVehicles\": [\n    {\n      \"vehicleTypeId\": 1,\n      \"quantity\": 1\n    }\n  ]\n}",
          "options": {
            "raw": {
              "language": "json"
            }
          }
        },
        "url": {
          "raw": "http://localhost:8080/api/reservations",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "reservations"]
        }
      }
    },
    {
      "name": "Annuler une réservation",
      "request": {
        "method": "POST",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/reservations/11/cancel",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "reservations", "11", "cancel"]
        }
      }
    },
    {
      "name": "Mettre à jour le statut",
      "request": {
        "method": "PUT",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/reservations/11/status/2",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "reservations", "11", "status", "2"]
        }
      }
    }
  ]
}
```

---

## Notes de Débogage

Si le scheduler ne fonctionne pas :

1. Vérifier que `@EnableScheduling` est présent dans `SchedulerConfig.java`
2. Vérifier les logs au démarrage : ` Début de la mise à jour automatique...`
3. Vérifier que les statuts existent en base :
   ```sql
   SELECT * FROM Reservation_status;
   ```
4. Vérifier les réservations et leurs dates :
   ```sql
   SELECT Id_Reservation, start_datetime, end_datetime, Id_Reservation_status 
   FROM Reservation 
   ORDER BY creation_date DESC;
   ```

---

## Support

Pour toute question ou problème, consulter :
- `GESTION_STATUTS_RESERVATIONS.md` : Documentation complète
- Logs de l'application : Affichent les mises à jour automatiques
- Code source : Commenté et bien structuré
