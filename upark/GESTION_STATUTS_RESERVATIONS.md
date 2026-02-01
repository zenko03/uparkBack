# Gestion Dynamique des Statuts de Réservations

## Vue d'ensemble

Ce document explique la gestion automatique et dynamique des statuts de réservations dans l'application UPark.

## Statuts de Réservations

Selon les données de test (`test-data-complete.sql`), les statuts sont définis comme suit :

| Statut | Valeur | Description |
|--------|--------|-------------|
| **à venir** | 10 | Réservation confirmée, date de début dans le futur |
| **En cours** | 15 | Réservation active entre date de début et date de fin |
| **Terminée** | 20 | Réservation passée, date de fin dépassée |
| **Annulée** | 25 | Réservation annulée manuellement par l'utilisateur |

## Architecture Mise en Place

### 1. Mise à Jour Automatique (Scheduler)

**Fichier:** `ReservationStatusScheduler.java`

- **Fréquence:** Toutes les 5 minutes (300 000 ms)
- **Type:** Tâche planifiée Spring (`@Scheduled`)
- **Fonction:** Met à jour automatiquement les statuts en fonction de la date/heure actuelle

**Logique:**
```java
1. Réservations "à venir" → "En cours"
   - Condition: statut = "à venir" ET date de début passée ET date de fin future
   
2. Réservations "En cours" → "Terminée"
   - Condition: statut = "En cours" ET date de fin passée
```

**Exemple de log:**
```
 Début de la mise à jour automatique des statuts de réservations
 Réservation ID 5 : 'à venir' → 'En cours'
 Réservation ID 3 : 'En cours' → 'Terminée'
✨ Mise à jour automatique terminée : 2 réservation(s) mise(s) à jour
```

### 2. Configuration du Scheduler

**Fichier:** `SchedulerConfig.java`

Active les tâches planifiées avec `@EnableScheduling`.

### 3. Mise à Jour Dynamique (Temps Réel)

**Fichier:** `ReservationService.java`

La méthode `updateReservationStatus()` est appelée lors de la consultation des réservations :

```java
private void updateReservationStatus(Reservation reservation) {
    LocalDateTime now = LocalDateTime.now();
    
    if (reservation.getStartDateTime().isAfter(now)) {
        // "à venir" (10)
        reservation.setReservationStatus(getStatusByValue(10));
    } else if (reservation.getEndDateTime().isAfter(now)) {
        // "En cours" (15)
        reservation.setReservationStatus(getStatusByValue(15));
    } else {
        // "Terminée" (20)
        reservation.setReservationStatus(getStatusByValue(20));
    }
}
```

Cette méthode est utilisée dans :
- `findByUserId()` : Liste des réservations d'un utilisateur
- `findByUserIdWithParkingInfo()` : Réservations avec infos parking pour l'interface mobile

### 4. Gestion Manuelle des Statuts

#### 4.1 Annulation de Réservation

**Endpoint:** `POST /api/reservations/{id}/cancel`

**Description:** Annule une réservation en changeant son statut à "Annulée" (25)

**Exemple de requête:**
```bash
POST http://localhost:8080/api/reservations/5/cancel
```

**Réponse:**
```json
{
  "Id_Reservation": 5,
  "totalPrice": 25000.00,
  "startDateTime": "2025-11-15T10:00:00",
  "endDateTime": "2025-11-15T18:00:00",
  "reservationStatus": {
    "Id_Reservation_status": 4,
    "label": "Annulée",
    "value": 25
  }
}
```

#### 4.2 Mise à Jour Manuelle du Statut

**Endpoint:** `PUT /api/reservations/{id}/status/{statusId}`

**Description:** Met à jour le statut d'une réservation manuellement

**Exemple de requête:**
```bash
PUT http://localhost:8080/api/reservations/5/status/3
```

**Paramètres:**
- `id` : ID de la réservation
- `statusId` : ID du nouveau statut (1 pour "à venir", 2 pour "En cours", etc.)

## Flux de Création de Réservation

1. **Création** (`POST /api/reservations`)
   - Statut initial : **"à venir"** (10)
   - `getDefaultReservationStatus()` retourne automatiquement le statut 10

2. **Évolution Automatique**
   - Scheduler vérifie toutes les 5 minutes
   - Passe à **"En cours"** (15) quand `startDateTime` est atteint
   - Passe à **"Terminée"** (20) quand `endDateTime` est dépassé

3. **Annulation Manuelle** (optionnelle)
   - Endpoint : `POST /api/reservations/{id}/cancel`
   - Statut devient **"Annulée"** (25)

## Avantages de cette Approche

 **Automatisation complète** : Pas besoin d'intervention manuelle pour les transitions normales

 **Cohérence des données** : Les statuts reflètent toujours l'état réel des réservations

 **Performance optimisée** : Mise à jour par lot toutes les 5 minutes au lieu de calculs en temps réel

 **Flexibilité** : Possibilité d'annulation manuelle ou de mise à jour forcée du statut

 **Évolutivité** : Architecture extensible pour ajouter de nouveaux statuts

## Nouveaux Endpoints Disponibles

### 1. Annulation de Réservation
```http
POST /api/reservations/{id}/cancel
```

### 2. Mise à Jour Manuelle du Statut
```http
PUT /api/reservations/{id}/status/{statusId}
```

### 3. Filtrage par Statut
```http
GET /api/reservations/filter?statusId={statusId}&userId={userId}
```

## Repositories Mis à Jour

### ReservationStatusRepository
- `findByLabel(String label)` : Recherche par label
- `findByValue(int value)` : Recherche par valeur

### ReservationRepository
- `findByReservationStatusIdAndStartDateTimeBefore()` : Pour le scheduler
- `findByReservationStatusIdAndEndDateTimeBefore()` : Pour le scheduler
- `findByReservationStatusId()` : Filtrage par statut

## Tests Recommandés

1. **Créer une réservation** avec une date dans le futur → Statut "à venir"
2. **Attendre 5 minutes** après l'heure de début → Statut "En cours"
3. **Attendre 5 minutes** après l'heure de fin → Statut "Terminée"
4. **Annuler une réservation** → Statut "Annulée"

## Logs à Surveiller

Le scheduler affiche des logs clairs :
```
 Début de la mise à jour automatique des statuts de réservations
 Réservation ID 5 : 'à venir' → 'En cours'
✨ Mise à jour automatique terminée : 1 réservation(s) mise(s) à jour
```

En cas d'erreur :
```
Erreur: Erreur lors de la mise à jour automatique des statuts : {message}
```

## Maintenance

- Le scheduler tourne automatiquement au démarrage de l'application
- Aucune configuration supplémentaire requise
- Les statuts sont créés automatiquement s'ils n'existent pas en base

## Prochaines Améliorations Possibles

1. **Notifications Push** : Alerter l'utilisateur quand le statut change
2. **Webhooks** : Appeler des APIs externes lors des changements de statut
3. **Historique** : Garder une trace de tous les changements de statut
4. **Rappels** : Envoyer des rappels avant le début/fin de réservation
5. **Statistiques** : Dashboard admin avec analyse des statuts
