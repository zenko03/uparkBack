# 📋 **GUIDE COMPLET DE TEST POSTMAN - UPARK FRONT OFFICE**

## 🚀 **PRÉREQUIS**

### 1. **Démarrer le serveur backend**
```bash
cd /d "d:/Projet/uparkBack/upark"
mvn spring-boot:run
```
Le serveur démarrera sur `http://localhost:8080`

### 2. **Importer les données de test**
```sql
-- Exécuter le script test-data-fr.sql dans votre base PostgreSQL
-- Cela créera 4 utilisateurs, 5 parkings, et toutes les données de référence
```

### 3. **Configuration Postman**
- **Base URL** : `http://localhost:8080/api`
- **Content-Type** : `application/json`
- **Authentification** : JWT (voir étape 1)

---

## 🔐 **ÉTAPE 1 : AUTHENTIFICATION**

### 1.1 **Connexion Utilisateur**
```http
POST {{base_url}}/auth/authenticate
Content-Type: application/json

{
    "user_name": "jean_rakoto",
    "password": "password123"
}
```

**Réponse attendue :**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
        "id_Users": 2,
        "user_name": "jean_rakoto",
        "email": "jean.rakoto@gmail.com",
        "role": "USER"
    }
}
```

### 1.2 **Utilisateurs de test disponibles**
| user_name | password | role |
|-----------|----------|------|
| admin | password123 | ADMIN |
| jean_rakoto | password123 | USER (propriétaire) |
| marie_rabe | password123 | USER (locataire) |
| paul_rasoa | password123 | USER (propriétaire) |

---

## 🅿️ **ÉTAPE 2 : LISTE DES PARKINGS (Interface listeParking.png)**

### 2.1 **Lister tous les parkings**
```http
GET {{base_url}}/parkings
Authorization: Bearer {{token}}
```

### 2.2 **Recherche par adresse**
```http
GET {{base_url}}/parkings/search/address?address=Analakely
Authorization: Bearer {{token}}
```

### 2.3 **Recherche géolocalisée**
```http
GET {{base_url}}/parkings/search/location?location=SRID=4326;POINT(47.5260 -18.9137)&radius=5
Authorization: Bearer {{token}}
```

### 2.4 **Recherche avancée avec filtres**
```http
GET {{base_url}}/parkings/search?minPrice=1.0&maxPrice=3.0&vehicleType=1&sortBy=price
Authorization: Bearer {{token}}
```

**Réponse attendue :** Liste de parkings avec :
- `id_Parking`, `label`, `hourly_rate`, `description`
- `localisation` (coordonnées géographiques)

---

## 📍 **ÉTAPE 3 : DÉTAIL D'UN PARKING (Interface reservation.png)**

### 3.1 **Obtenir les détails d'un parking**
```http
GET {{base_url}}/parkings/1
Authorization: Bearer {{token}}
```

### 3.2 **Types de véhicules disponibles pour ce parking**
```http
GET {{base_url}}/parking-vehicles?parkingId=1
Authorization: Bearer {{token}}
```

### 3.3 **Liste des types de véhicules**
```http
GET {{base_url}}/vehicles
Authorization: Bearer {{token}}
```

**Réponse attendue :** Détails complets du parking avec :
- Informations générales
- Types de véhicules acceptés
- Capacités disponibles

---

## 📝 **ÉTAPE 4 : FORMULAIRE RÉSERVATION (Interface reservationForm.png)**

### 4.1 **Calculer le prix d'une réservation**
```http
POST {{base_url}}/reservations/calculate-price
Authorization: Bearer {{token}}
Content-Type: application/json

{
    "parkingId": 1,
    "startDateTime": "2025-11-07T08:00:00",
    "endDateTime": "2025-11-07T18:00:00",
    "selectedVehicles": [
        {
            "vehicleTypeId": 1,
            "quantity": 1
        }
    ]
}
```

### 4.2 **Vérifier la disponibilité**
```http
POST {{base_url}}/reservations/check-availability
Authorization: Bearer {{token}}
Content-Type: application/json

{
    "parkingId": 1,
    "startDateTime": "2025-11-07T08:00:00",
    "endDateTime": "2025-11-07T18:00:00",
    "selectedVehicles": [
        {
            "vehicleTypeId": 1,
            "quantity": 1
        }
    ]
}
```

**Réponse attendue :** 
- Prix total calculé (ex: 25.00 pour 10h à 2.50€/h)
- Disponibilité : `true` ou `false`

---

## ✅ **ÉTAPE 5 : CONFIRMATION RÉSERVATION (Interface confirmResa.png)**

### 5.1 **Créer la réservation**
```http
POST {{base_url}}/reservations
Authorization: Bearer {{token}}
Content-Type: application/json

{
    "parkingId": 1,
    "userId": 3,
    "startDateTime": "2025-11-07T08:00:00",
    "endDateTime": "2025-11-07T18:00:00",
    "paymentMethod": "payer_sur_place",
    "selectedVehicles": [
        {
            "vehicleTypeId": 1,
            "quantity": 1
        }
    ]
}
```

### 5.2 **Obtenir les statuts de paiement**
```http
GET {{base_url}}/payment-status
Authorization: Bearer {{token}}
```

**Réponse attendue :** Réservation créée avec :
- `id_Reservation`, `total_price`, `creation_date`
- Statut automatique selon dates

---

## 📋 **ÉTAPE 6 : LISTE DES RÉSERVATIONS (Interface listeResa.png)**

### 6.1 **Lister les réservations d'un utilisateur**
```http
GET {{base_url}}/reservations/user/3
Authorization: Bearer {{token}}
```

### 6.2 **Obtenir les détails d'une réservation**
```http
GET {{base_url}}/reservations/1
Authorization: Bearer {{token}}
```

### 6.3 **Lister les statuts de réservation**
```http
GET {{base_url}}/reservation-status
Authorization: Bearer {{token}}
```

**Réponse attendue :** Liste avec statuts automatiques :
- À venir (vert) : `value_ = 20`
- En cours (bleu) : `value_ = 15` 
- Terminée (gris) : `value_ = 30`

---

## 🔄 **ÉTAPE 7 : OPÉRATIONS SUPPLÉMENTAIRES**

### 7.1 **Mettre à jour une réservation**
```http
PUT {{base_url}}/reservations/1
Authorization: Bearer {{token}}
Content-Type: application/json

{
    "totalPrice": 30.00,
    "paymentMethod": "carte_bancaire"
}
```

### 7.2 **Annuler une réservation**
```http
DELETE {{base_url}}/reservations/1
Authorization: Bearer {{token}}
```

---

## 📊 **DONNÉES DE TEST RÉFÉRENCE**

### Parkings disponibles :
| ID | Nom | Tarif/h | Localisation |
|----|-----|---------|--------------|
| 1 | Parking Centre Ville Analakely | 2.50€ | 47.5260 -18.9137 |
| 2 | Parking Isoraka | 1.80€ | 47.5214 -18.9039 |
| 3 | Parking Andraharo | 3.00€ | 47.5367 -18.8945 |
| 4 | Parking Behoririka | 2.20€ | 47.5189 -18.9203 |
| 5 | Parking Ivandry | 3.50€ | 47.5412 -18.8834 |

### Types de véhicules :
| ID | Type |
|----|------|
| 1 | Voiture |
| 2 | Moto |
| 3 | Utilitaire |
| 4 | Camion |
| 5 | Citadine |

---

## ✅ **CHECKLIST DE VALIDATION**

- [ ] Authentification JWT fonctionnelle
- [ ] Liste parkings avec filtres
- [ ] Recherche par adresse et géolocalisation
- [ ] Détails parking complets
- [ ] Calcul prix automatique
- [ ] Vérification disponibilité
- [ ] Création réservation
- [ ] Liste réservations utilisateur
- [ ] Statuts automatiques (à venir/en cours/terminé)
- [ ] Mise à jour/annulation réservation

---

## 🚨 **ERREURS COURANTES**

### 401 Unauthorized
- Vérifier le token JWT dans les headers
- Token expiré ? Se reconnecter

### 400 Bad Request
- Vérifier le format JSON
- Dates au format `yyyy-MM-dd'T'HH:mm:ss`
- Champs obligatoires manquants

### 404 Not Found
- ID de parking/réservation inexistant
- Endpoint incorrect

### 500 Internal Server Error
- Vérifier les logs du serveur
- Problème de connexion base de données

---

## 🎯 **FLOW COMPLET TEST**

1. **Connexion** → Obtenir token JWT
2. **Recherche** → Trouver parking "Analakely"
3. **Détails** → Voir parking ID 1
4. **Calcul** → Prix pour 1 voiture, 10h
5. **Disponibilité** → Vérifier créneaux
6. **Réservation** → Créer réservation
7. **Liste** → Voir réservations utilisateur
8. **Statuts** → Vérifier mise à jour automatique

**Succès !** 🎉 Votre backend est prêt pour l'intégration front-end !