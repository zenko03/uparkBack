# Guide de test pour la correction des statuts de réservations

## Problème identifié
Les réservations confirmées pour une date actuelle restaient avec le statut "à venir" au lieu de passer à "En cours".

## Corrections apportées

### 1. Dans ReservationService.java
- **createReservation()**: La réservation est maintenant créée avec le statut approprié selon la date actuelle, pas toujours "à venir"
- **updateReservationStatus()**: Améliorée pour ne pas modifier les réservations annulées
- **updateAllReservationStatuses()**: Nouvelle méthode pour mettre à jour manuellement tous les statuts
- **updateReservationStatusById()**: Nouvelle méthode pour mettre à jour le statut d'une réservation spécifique

### 2. Dans ReservationStatusScheduler.java
- Amélioration de la logique pour gérer les cas où une réservation "à venir" doit passer directement à "Terminée" si la période est entièrement passée
- Ajout de logs détaillés pour le débogage

### 3. Dans ReservationController.java
- **POST /api/reservations/update-all-statuses**: Met à jour tous les statuts de réservations
- **POST /api/reservations/{id}/update-status**: Met à jour le statut d'une réservation spécifique

## Comment tester

### 1. Test de création de réservation
```bash
# Créer une réservation pour maintenant
POST /api/reservations
{
  "parkingId": 1,
  "userId": 1,
  "startDateTime": "2025-01-21T10:00:00",
  "endDateTime": "2025-01-21T12:00:00",
  "paymentMethod": "carte",
  "selectedVehicles": [
    {
      "vehicleTypeId": 1,
      "quantity": 1
    }
  ]
}
```
**Résultat attendu**: La réservation doit être créée avec le statut "En cours" si l'heure actuelle est entre 10:00 et 12:00.

### 2. Test de mise à jour manuelle de tous les statuts
```bash
POST /api/reservations/update-all-statuses
```
**Résultat attendu**: Message indiquant le nombre de réservations mises à jour.

### 3. Test de mise à jour d'une réservation spécifique
```bash
POST /api/reservations/{id}/update-status
```
**Résultat attendu**: Message indiquant le changement de statut (ex: "à venir → En cours").

### 4. Vérification des logs
Le scheduler exécute automatiquement la mise à jour toutes les 5 minutes. Vérifiez les logs pour voir :
```
🔄 Début de la mise à jour automatique des statuts de réservations
✅ Réservation ID X : 'à venir' → 'En cours' (Début: ..., Fin: ...)
✨ Mise à jour automatique terminée : X réservation(s) mise(s) à jour
```

## Scénarios de test

### Scénario 1: Réservation future
- **Date de début**: Dans le futur
- **Statut attendu**: "à venir"

### Scénario 2: Réservation en cours
- **Date de début**: Dans le passé ou maintenant
- **Date de fin**: Dans le futur
- **Statut attendu**: "En cours"

### Scénario 3: Réservation terminée
- **Date de début**: Dans le passé
- **Date de fin**: Dans le passé ou maintenant
- **Statut attendu**: "Terminée"

### Scénario 4: Réservation annulée
- **Statut**: "Annulée"
- **Comportement**: Ne doit pas être modifié par la mise à jour automatique

## Débogage

Si les statuts ne se mettent toujours pas à jour correctement :

1. **Vérifier les logs du scheduler**:
   - Le scheduler est-il bien démarré ?
   - Y a-t-il des erreurs dans les logs ?

2. **Vérifier les données**:
   ```sql
   SELECT Id_Reservation, start_datetime, end_datetime, 
          rs.label as status, rs.value_
   FROM Reservation r
   JOIN Reservation_status rs ON r.Id_Reservation_status = rs.Id_Reservation_status
   ORDER BY r.start_datetime;
   ```

3. **Forcer la mise à jour manuelle**:
   ```bash
   POST /api/reservations/update-all-statuses
   ```

4. **Vérifier une réservation spécifique**:
   ```bash
   POST /api/reservations/{id}/update-status
   ```

## Notes importantes

- Le scheduler s'exécute toutes les 5 minutes (300 000 ms)
- Les réservations annulées ne sont jamais modifiées automatiquement
- La mise à jour manuelle est immédiate et peut être utilisée pour le débogage
- Les logs détaillés ont été ajoutés pour faciliter le suivi des mises à jour