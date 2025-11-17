# Architecture de Gestion des Statuts de Réservations

## Diagramme de Flux

```
┌─────────────────────────────────────────────────────────────────┐
│                    CRÉATION DE RÉSERVATION                      │
│                                                                 │
│  Mobile App → POST /api/reservations                           │
│  {                                                              │
│    userId, parkingId,                                          │
│    startDateTime: "2025-11-15T10:00:00"                       │
│    endDateTime: "2025-11-15T18:00:00"                         │
│  }                                                              │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ ReservationService   │
              │ createReservation()  │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ Statut Initial       │
              │ "à venir" (10)       │
              │ via                  │
              │ getDefaultStatus()   │
              └──────────┬───────────┘
                         │
                         ▼
        ┌────────────────────────────────────┐
        │   RÉSERVATION SAUVEGARDÉE EN BDD   │
        │   Status = "à venir" (10)          │
        └────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│              MISE À JOUR AUTOMATIQUE (SCHEDULER)                │
│                                                                 │
│  Toutes les 5 minutes (300 000 ms)                            │
└─────────────────────────────────────────────────────────────────┘
                         │
                         ▼
        ┌────────────────────────────────────┐
        │ ReservationStatusScheduler         │
        │ @Scheduled(fixedRate = 300000)     │
        └────────┬───────────────────────────┘
                 │
                 ├─────────────────────────────────┐
                 │                                 │
                 ▼                                 ▼
    ┌────────────────────────┐      ┌────────────────────────┐
    │ Recherche Réservations │      │ Recherche Réservations │
    │ "à venir" + date début │      │ "En cours" + date fin  │
    │ passée                 │      │ passée                 │
    └────────┬───────────────┘      └────────┬───────────────┘
             │                                │
             ▼                                ▼
    ┌────────────────────────┐      ┌────────────────────────┐
    │ Mise à jour:           │      │ Mise à jour:           │
    │ "à venir" (10)         │      │ "En cours" (15)        │
    │      ↓                 │      │      ↓                 │
    │ "En cours" (15)        │      │ "Terminée" (20)        │
    └────────────────────────┘      └────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                   CONSULTATION PAR L'UTILISATEUR                │
│                                                                 │
│  Mobile App → GET /api/reservations/user/{userId}              │
└─────────────────────────────────────────────────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ ReservationService   │
              │ findByUserId()       │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ Pour chaque          │
              │ réservation:         │
              │ updateReservation    │
              │ Status()             │
              └──────────┬───────────┘
                         │
                         ▼
        ┌────────────────┴──────────────────┐
        │                                   │
        │ if (startDate > now)              │
        │   → "à venir" (10)                │
        │                                   │
        │ else if (endDate > now)           │
        │   → "En cours" (15)               │
        │                                   │
        │ else                              │
        │   → "Terminée" (20)               │
        │                                   │
        └───────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                    ANNULATION MANUELLE                          │
│                                                                 │
│  Mobile App → POST /api/reservations/{id}/cancel               │
└─────────────────────────────────────────────────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ ReservationService   │
              │ cancelReservation()  │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ Statut → "Annulée"   │
              │ (25)                 │
              └──────────────────────┘
```

## Diagramme de Classes Simplifié

```
┌──────────────────────────────────────────────────────────────┐
│                    ReservationController                     │
│──────────────────────────────────────────────────────────────│
│ + POST   /api/reservations                                   │
│ + GET    /api/reservations/user/{userId}                     │
│ + POST   /api/reservations/{id}/cancel                       │
│ + PUT    /api/reservations/{id}/status/{statusId}            │
│ + GET    /api/reservations/filter?statusId=...               │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         │ uses
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                    ReservationService                        │
│──────────────────────────────────────────────────────────────│
│ + createReservation(request)                                 │
│ + findByUserId(userId)                                       │
│ + cancelReservation(id)                                      │
│ + updateStatus(id, statusId)                                 │
│ - updateReservationStatus(reservation)                       │
│ - getStatusByValue(value)                                    │
│ - getDefaultReservationStatus()                              │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         │ uses
                         ▼
┌──────────────────────────────────────────────────────────────┐
│              ReservationStatusScheduler                      │
│──────────────────────────────────────────────────────────────│
│ @Scheduled(fixedRate = 300000)                               │
│ + updateReservationStatuses()                                │
│ - getOrCreateStatus(label, value)                            │
└──────────────────────────────────────────────────────────────┘


┌─────────────────────┐       ┌────────────────────────────┐
│   Reservation       │       │   ReservationStatus        │
│─────────────────────│       │────────────────────────────│
│ - id                │       │ - id                       │
│ - totalPrice        │◄──────│ - label                    │
│ - startDateTime     │       │ - value                    │
│ - endDateTime       │       │                            │
│ - reservationStatus │       │ Exemples:                  │
│ - user              │       │ "à venir"     → 10         │
│ - vehicles          │       │ "En cours"    → 15         │
│                     │       │ "Terminée"    → 20         │
│                     │       │ "Annulée"     → 25         │
└─────────────────────┘       └────────────────────────────┘
```

## Timeline d'une Réservation

```
T0: Création
│
│   Status: "à venir" (10)
│   ┌──────────────────────────────────────┐
│   │ Réservation créée                    │
│   │ startDate: 15/11 10h00               │
│   │ endDate:   15/11 18h00               │
│   └──────────────────────────────────────┘
│
│
├──── 13/11 15h00 : Création
│
│
├──── 13/11 15h05 : Scheduler check (pas de changement)
│
│
├──── 15/11 09h55 : Scheduler check (pas de changement)
│
│
├──── 15/11 10h05 : Scheduler check ✅ CHANGEMENT
│     │
│     │   Status: "En cours" (15)
│     │   ┌──────────────────────────────────────┐
│     │   │ La réservation a commencé            │
│     │   └──────────────────────────────────────┘
│     │
│
│
├──── 15/11 18h05 : Scheduler check ✅ CHANGEMENT
│     │
│     │   Status: "Terminée" (20)
│     │   ┌──────────────────────────────────────┐
│     │   │ La réservation est terminée          │
│     │   └──────────────────────────────────────┘
│     │
│
▼
Fin
```

## Décisions de Conception

### 1. Pourquoi un Scheduler toutes les 5 minutes ?

**Avantages :**
- ✅ Performance : Pas de calcul à chaque requête
- ✅ Simplicité : Logique centralisée
- ✅ Fiabilité : Mise à jour garantie même sans consultation

**Alternative rejetée :** Calcul en temps réel à chaque requête
- ❌ Surcharge des requêtes GET
- ❌ Redondance des calculs
- ❌ Coût CPU élevé

### 2. Pourquoi deux mécanismes de mise à jour ?

**Scheduler (Batch):**
- Mise à jour en base de données
- Exécution périodique (5 minutes)
- Pour la cohérence des données

**Dynamique (Temps Réel):**
- Calcul à la consultation
- Affichage précis pour l'utilisateur
- Pas de persistance immédiate

### 3. Pourquoi des valeurs numériques (10, 15, 20, 25) ?

**Avantages :**
- ✅ Tri facile (ORDER BY value)
- ✅ Comparaison directe (value > 15)
- ✅ Extensibilité (valeurs intermédiaires possibles)
- ✅ Performance (index sur integer)

## Séquence de Traitement

```
1. Création Réservation
   ↓
2. Statut = "à venir" (10)
   ↓
3. Sauvegarde en BDD
   ↓
4. Scheduler tourne toutes les 5 min
   ↓
5. Si startDate passée → "En cours" (15)
   ↓
6. Si endDate passée → "Terminée" (20)
   ↓
7. Consultation utilisateur → Affichage statut correct
```

## Gestion des Cas Particuliers

### Cas 1 : Réservation immédiate
```
Création: 13/11 15h00
Start:    13/11 15h00
End:      13/11 18h00

→ Statut initial: "à venir" (10)
→ À 15h05 (scheduler): "En cours" (15)
```

### Cas 2 : Réservation de longue durée
```
Création: 13/11 15h00
Start:    14/11 08h00
End:      20/11 18h00

→ Statut: "à venir" (10)
→ 14/11 08h05: "En cours" (15)
→ 20/11 18h05: "Terminée" (20)
```

### Cas 3 : Annulation
```
Création: 13/11 15h00
Start:    15/11 10h00
End:      15/11 18h00

→ 13/11 15h00: Statut = "à venir" (10)
→ 14/11 10h00: Annulation manuelle
→ Statut = "Annulée" (25)
→ Scheduler ignore (statut final)
```

---

**Note :** Ce diagramme est créé avec des caractères ASCII pour faciliter l'affichage dans tous les éditeurs de texte.
