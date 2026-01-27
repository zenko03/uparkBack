#  GUIDE COMPLET DE TESTS DES ENDPOINTS MVP UPARK

##  OBJECTIF
Tester tous les endpoints du MVP Upark (Front Office + Back Office) avec les données de test complètes.

##  PRÉREQUIS

### 1. Préparation de la base de données
```bash
# 1. Nettoyer la base de données
psql -d upark_db -f cleanup-database.sql

# 2. Insérer les données de test complètes
psql -d upark_db -f test-data-complete.sql

# 3. Démarrer le serveur Spring Boot
mvn spring-boot:run
```

### 2. Configuration Postman
- **Base URL**: `http://localhost:8080/api`
- **Authentification**: JWT Token (obtenir via login)
- **Headers**: `Content-Type: application/json`

---

## 🔐 FRONT OFFICE - ENDPOINTS UTILISATEUR

### 1. AUTHENTIFICATION

#### 1.1 Inscription d'un nouvel utilisateur
```http
POST /api/auth/register
Content-Type: application/json

{
    "name": "Test",
    "first_name": "User",
    "user_name": "test_user_new",
    "email": "test.new@example.com",
    "password": "password123",
    "phone_number": "+261340000000"
}
```

**Résultat attendu:**
```json
{
    "message": "Utilisateur inscrit avec succès",
    "userId": 12
}
```

#### 1.2 Connexion utilisateur
```http
POST /api/auth/authenticate
Content-Type: application/json

{
    "email": "sitraka.client@gmail.com",
    "password": "password123"
}
```

**Résultat attendu:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 7,
    "email": "sitraka.client@gmail.com",
    "role": "USER",
    "expiresIn": 3600
}
```

#### 1.3 Connexion administrateur
```http
POST /api/auth/authenticate
Content-Type: application/json

{
    "email": "admin@upark.mg",
    "password": "password123"
}
```

**Résultat attendu:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "email": "admin@upark.mg",
    "role": "ADMIN",
    "expiresIn": 3600
}
```

### 2. GESTION DES VÉHICULES

#### 2.1 Lister tous les types de véhicules
```http
GET /api/vehicles
Authorization: Bearer {token}
```

**Résultat attendu:**
```json
[
    {
        "id_vehicles": 1,
        "types": "Voiture",
        "icon": "car-icon"
    },
    {
        "id_vehicles": 2,
        "types": "Moto",
        "icon": "motorcycle-icon"
    },
    {
        "id_vehicles": 3,
        "types": "Utilitaire léger",
        "icon": "van-icon"
    }
]
```

#### 2.2 Détails d'un type de véhicule
```http
GET /api/vehicles/1
Authorization: Bearer {token}
```

**Résultat attendu:**
```json
{
    "id_vehicles": 1,
    "types": "Voiture",
    "icon": "car-icon"
}
```

### 3. RECHERCHE DE PARKINGS

#### 3.1 Lister tous les parkings
```http
GET /api/parkings
Authorization: Bearer {token}
```

**Résultat attendu:**
```json
[
    {
        "id_parking": 1,
        "label": "Parking Centre Ville Analakely",
        "hourly_rate": 2500.00,
        "description": "Parking sécurisé en plein centre ville...",
        "localisation": "POINT(47.5060 -18.9137)",
        "id_users": 3
    }
]
```

#### 3.2 Recherche par adresse/localisation
```http
GET /api/parkings/search?address=Analakely&radius=5
Authorization: Bearer {token}
```

**Résultat attendu:**
```json
[
    {
        "id_parking": 1,
        "label": "Parking Centre Ville Analakely",
        "distance": 0.2,
        "hourly_rate": 2500.00
    }
]
```

#### 3.3 Détails d'un parking
```http
GET /api/parkings/1
Authorization: Bearer {token}
```

**Résultat attendu:**
```json
{
    "id_parking": 1,
    "label": "Parking Centre Ville Analakely",
    "hourly_rate": 2500.00,
    "description": "Parking sécurisé en plein centre ville...",
    "localisation": "POINT(47.5060 -18.9137)",
    "parking_vehicles": [
        {
            "id_vehicles": 1,
            "types": "Voiture",
            "numbers": 15
        }
    ],
    "notes_moyennes": 4.17
}
```

### 4. RÉSERVATIONS

#### 4.1 Calculer le prix d'une réservation
```http
POST /api/reservations/calculate-price
Authorization: Bearer {token}
Content-Type: application/json

{
    "id_parking": 1,
    "start_datetime": "2024-12-01T10:00:00",
    "end_datetime": "2024-12-01T18:00:00",
    "vehicles": [
        {
            "id_vehicles": 1,
            "quantity": 1
        }
    ]
}
```

**Résultat attendu:**
```json
{
    "total_price": 20000.00,
    "duration_hours": 8,
    "hourly_rate": 2500.00,
    "commission": 2000.00,
    "price_without_commission": 18000.00
}
```

#### 4.2 Vérifier la disponibilité
```http
POST /api/reservations/check-availability
Authorization: Bearer {token}
Content-Type: application/json

{
    "id_parking": 1,
    "start_datetime": "2024-12-01T10:00:00",
    "end_datetime": "2024-12-01T18:00:00",
    "vehicles": [
        {
            "id_vehicles": 1,
            "quantity": 1
        }
    ]
}
```

**Résultat attendu:**
```json
{
    "available": true,
    "available_places": 14,
    "requested_places": 1,
    "message": "Places disponibles pour la période demandée"
}
```

#### 4.3 Créer une réservation
```http
POST /api/reservations
Authorization: Bearer {token}
Content-Type: application/json

{
    "id_parking": 1,
    "start_datetime": "2024-12-01T10:00:00",
    "end_datetime": "2024-12-01T18:00:00",
    "payment_method": "mobile_money",
    "vehicles": [
        {
            "id_vehicles": 1,
            "quantity": 1
        }
    ]
}
```

**Résultat attendu:**
```json
{
    "id_reservation": 11,
    "total_price": 20000.00,
    "status": "En attente de confirmation",
    "creation_date": "2024-11-07T15:30:00",
    "message": "Réservation créée avec succès"
}
```

#### 4.4 Lister les réservations d'un utilisateur
```http
GET /api/reservations/user/7
Authorization: Bearer {token}
```

**Résultat attendu:**
```json
[
    {
        "id_reservation": 1,
        "total_price": 15000.00,
        "status": "Terminée",
        "creation_date": "2024-10-28T15:30:00",
        "parking": {
            "label": "Parking Centre Ville Analakely",
            "hourly_rate": 2500.00
        }
    }
]
```

#### 4.5 Détails d'une réservation
```http
GET /api/reservations/1
Authorization: Bearer {token}
```

**Résultat attendu:**
```json
{
    "id_reservation": 1,
    "total_price": 15000.00,
    "status": "Terminée",
    "creation_date": "2024-10-28T15:30:00",
    "start_datetime": "2024-10-30T10:00:00",
    "end_datetime": "2024-10-30T16:00:00",
    "payment_method": "espèces",
    "user": {
        "name": "Rasoa",
        "first_name": "Sitraka"
    },
    "parking": {
        "label": "Parking Centre Ville Analakely",
        "hourly_rate": 2500.00
    }
}
```

---

## 🏢 BACK OFFICE - ENDPOINTS ADMINISTRATEUR

### 1. TABLEAU DE BORD

#### 1.1 Aperçu global du tableau de bord
```http
GET /api/dashboard/overview
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
{
    "date_jour": "2024-11-07",
    "commissions_jour": 0,
    "commissions_mois": 18300.00,
    "reservations_jour": 0,
    "reservations_mois": 10,
    "utilisateurs_actifs_jour": 0,
    "total_parkings": 8,
    "top_parking_mois": "Parking Centre Ville Analakely"
}
```

#### 1.2 Statistiques des commissions
```http
GET /api/dashboard/commissions?startDate=2024-10-01&endDate=2024-11-07
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
{
    "commissionsParPeriode": [
        {
            "jour": "2024-10-28",
            "total_commissions": 1500.00,
            "nombre_transactions": 1,
            "type_commission": "Commission globale"
        }
    ],
    "totalMoisCourant": 18300.00,
    "totalJour": 0,
    "moyenneJournaliere": 0
}
```

#### 1.3 Réservations par statut
```http
GET /api/dashboard/reservations/status?startDate=2024-10-01&endDate=2024-11-07
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
{
    "reservationsParStatut": [
        {
            "statut_label": "Terminée",
            "total_reservations": 3,
            "montant_total": 58000.00
        },
        {
            "statut_label": "En cours",
            "total_reservations": 2,
            "montant_total": 50000.00
        }
    ],
    "totalReservations": 10
}
```

#### 1.4 Top parkings par réservations
```http
GET /api/dashboard/parkings/top-reservations?limit=5
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
[
    {
        "id_parking": 1,
        "parking_label": "Parking Centre Ville Analakely",
        "nombre_reservations": 3,
        "chiffre_affaires": 60000.00,
        "note_moyenne": 4.17,
        "rang": 1
    }
]
```

#### 1.5 Utilisateurs actifs
```http
GET /api/dashboard/users/active?startDate=2024-10-01&endDate=2024-11-07
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
{
    "utilisateursParPeriode": [
        {
            "jour": "2024-10-28",
            "utilisateurs_actifs": 1,
            "nombre_reservations": 1
        }
    ],
    "actifsAujourdhui": 0,
    "actifs7DerniersJours": 4,
    "actifs30DerniersJours": 4
}
```

### 2. GESTION DES TYPES DE VÉHICULES

#### 2.1 CRUD complet des véhicules

**Créer un type de véhicule:**
```http
POST /api/vehicles
Authorization: Bearer {admin_token}
Content-Type: application/json

{
    "types": "Scooter",
    "icon": "scooter-icon"
}
```

**Résultat attendu:**
```json
{
    "id_vehicles": 8,
    "types": "Scooter",
    "icon": "scooter-icon",
    "message": "Type de véhicule créé avec succès"
}
```

**Modifier un type de véhicule:**
```http
PUT /api/vehicles/8
Authorization: Bearer {admin_token}
Content-Type: application/json

{
    "types": "Scooter Électrique",
    "icon": "escooter-icon"
}
```

**Supprimer un type de véhicule:**
```http
DELETE /api/vehicles/8
Authorization: Bearer {admin_token}
```

### 3. GESTION DES COMMISSIONS

#### 3.1 Commission globale
```http
GET /api/global-commission/1
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
{
    "id_global_commission": 1,
    "rate": 10.0,
    "creation_date": "2024-10-08T15:30:00"
}
```

**Modifier la commission globale:**
```http
PUT /api/global-commission/1
Authorization: Bearer {admin_token}
Content-Type: application/json

{
    "rate": 12.0
}
```

#### 3.2 Commissions par véhicule
```http
GET /api/commission-vehicles
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
[
    {
        "id_commission_vehicles": 1,
        "rate": 8.0,
        "vehicle": {
            "types": "Voiture"
        }
    }
]
```

#### 3.3 Commissions partenaires
```http
GET /api/commission-partners
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
[
    {
        "id_commission_partners": 1,
        "rate": 5.0,
        "partner": {
            "name": "Société Parking Mada",
            "email": "contact@parking-mada.mg"
        }
    }
]
```

### 4. GESTION DES RÉSERVATIONS

#### 4.1 Lister toutes les réservations (admin)
```http
GET /api/reservations
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
[
    {
        "id_reservation": 1,
        "total_price": 15000.00,
        "status": "Terminée",
        "user": {
            "name": "Rasoa",
            "first_name": "Sitraka"
        },
        "parking": {
            "label": "Parking Centre Ville Analakely"
        }
    }
]
```

#### 4.2 Filtrer les réservations
```http
GET /api/reservations/filter?status=Terminée&startDate=2024-10-01&endDate=2024-11-07
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
[
    {
        "id_reservation": 1,
        "total_price": 15000.00,
        "status": "Terminée",
        "creation_date": "2024-10-28T15:30:00"
    }
]
```

#### 4.3 Mettre à jour le statut d'une réservation
```http
PUT /api/reservations/9/status
Authorization: Bearer {admin_token}
Content-Type: application/json

{
    "status": "Confirmée"
}
```

### 5. GESTION DES DÉLAIS DE RÉSERVATION

#### 5.1 Obtenir le délai actif
```http
GET /api/reservation-delay/active
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
{
    "id_reservation_delay": 1,
    "delay_in_hours": 2,
    "delay_in_minutes": 0,
    "creation_date": "2024-10-08T15:30:00"
}
```

#### 5.2 Modifier le délai de réservation
```http
PUT /api/reservation-delay/1
Authorization: Bearer {admin_token}
Content-Type: application/json

{
    "delay_in_hours": 4,
    "delay_in_minutes": 30
}
```

#### 5.3 Valider un délai de réservation
```http
GET /api/reservation-delay/check-availability?requested_datetime=2024-12-01T10:00:00
Authorization: Bearer {admin_token}
```

**Résultat attendu:**
```json
{
    "available": true,
    "minimum_datetime": "2024-12-01T08:00:00",
    "current_datetime": "2024-11-07T15:30:00",
    "delay_required_hours": 2,
    "message": "La réservation respecte le délai minimum requis"
}
```

---

## 🧪 SCÉNARIOS DE TEST COMPLETS

### SCÉNARIO 1: PARCOURS CLIENT COMPLET

1. **Inscription** → `POST /api/auth/register`
2. **Connexion** → `POST /api/auth/authenticate`
3. **Recherche parking** → `GET /api/parkings/search`
4. **Vérification disponibilité** → `POST /api/reservations/check-availability`
5. **Calcul prix** → `POST /api/reservations/calculate-price`
6. **Création réservation** → `POST /api/reservations`
7. **Consultation réservations** → `GET /api/reservations/user/{id}`

### SCÉNARIO 2: PARCOURS ADMIN COMPLET

1. **Connexion admin** → `POST /api/auth/authenticate`
2. **Consultation tableau de bord** → `GET /api/dashboard/overview`
3. **Analyse statistiques** → `GET /api/dashboard/commissions`
4. **Gestion commissions** → `PUT /api/global-commission/1`
5. **Gestion réservations** → `GET /api/reservations/filter`
6. **Configuration délais** → `PUT /api/reservation-delay/1`

### SCÉNARIO 3: TESTS DE CHARGE

1. **Créer 10 réservations simultanées**
2. **Vérifier les statistiques en temps réel**
3. **Tester les filtres avec des charges élevées**
4. **Valider les performances du dashboard**

---

## 📊 RÉSULTATS ATTENDUS

###  CRITÈRES DE SUCCÈS

1. **Tous les endpoints répondent** (Code 200/201/400/404)
2. **Authentification fonctionnelle** (JWT valide)
3. **Données cohérentes** (respect des contraintes FK)
4. **Performances acceptables** (< 2s par requête)
5. **Gestion d'erreurs** (messages clairs)

### 📈 MÉTRIQUES À SURVEILLER

- **Temps de réponse moyen** des endpoints
- **Taux de succès** des requêtes
- **Consistance des données** (base vs API)
- **Utilisation mémoire** du serveur
- **Nombre de requêtes simultanées** supportées

---

## 🔍 DÉBOGAGE

### ERREURS COURANTES

1. **401 Unauthorized** → Token invalide ou expiré
2. **403 Forbidden** → Rôle insuffisant
3. **404 Not Found** → Ressource inexistante
4. **400 Bad Request** → Données invalides
5. **500 Internal Server Error** → Erreur serveur

### SOLUTIONS

1. **Vérifier le token JWT** dans les headers
2. **Contrôler les rôles utilisateur** dans la base
3. **Valider les IDs** avant les requêtes
4. **Vérifier le format JSON** des payloads
5. **Consulter les logs** du serveur Spring Boot

---

##  CHECKLIST FINALE

- [ ] Base de données nettoyée et peuplée
- [ ] Serveur Spring Boot démarré
- [ ] Tokens JWT obtenus (user + admin)
- [ ] Tests Front Office validés
- [ ] Tests Back Office validés
- [ ] Scénarios complets exécutés
- [ ] Performances vérifiées
- [ ] Erreurs documentées

**Le MVP Upark est prêt pour la validation finale !** 🎉