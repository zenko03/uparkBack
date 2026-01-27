#  Guide Complet d'Installation des Notifications Push FCM

##  CE QUI A ÉTÉ FAIT

### 1. Frontend React Native
-  `build.gradle` corrigé (plugin Google Services après dependencies)
-  Permissions FCM ajoutées dans AndroidManifest.xml
-  `firebase.js` créé avec toutes les fonctions FCM
-  `notificationService.js` créé pour communication backend
-  `App.tsx` modifié pour initialiser FCM au démarrage

### 2. Backend Spring Boot
-  Modèles `DeviceToken.java` et `Notification.java` créés
-  Repositories créés avec requêtes optimisées
-  `NotificationService.java` avec intégration Edge Function
-  Controllers pour API notifications et tokens
-  Intégration dans `ReservationRequestService` (acceptation/refus)
-  Configuration Supabase dans `application.properties`

### 3. Supabase Edge Function
-  Fonction `send-push-notification` créée avec API HTTP v1
-  Gestion OAuth2 + Service Account Firebase
-  Endpoint: `https://fbpefbjoxzkxombdcqif.supabase.co/functions/v1/send-push-notification`

### 4. Base de données
-  Script SQL `init-notifications.sql` créé
-  Tables `Device_tokens` et `Notifications`
-  Index pour performance
-  Fonctions de nettoyage automatique

---

##  ÉTAPES À SUIVRE MAINTENANT

### ÉTAPE 1 : Exécuter le script SQL dans Supabase

1. Allez dans **Supabase Dashboard** → Votre projet → **SQL Editor**
2. Copiez tout le contenu de `init-notifications.sql`
3. Collez et cliquez sur **Run**
4. Vérifiez que les tables sont créées : **Table Editor** → Cherchez `Device_tokens` et `Notifications`

### ÉTAPE 2 : Configurer les secrets Supabase pour la Edge Function

1. Allez dans **Supabase Dashboard** → **Edge Functions** → **Secrets**
2. Ajoutez ces 3 secrets :

```
FIREBASE_PROJECT_ID=upark-c2eb4

FIREBASE_CLIENT_EMAIL=firebase-adminsdk-fbsvc@upark-c2eb4.iam.gserviceaccount.com

FIREBASE_PRIVATE_KEY=-----BEGIN PRIVATE KEY-----
MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCLV7c4S0meQ1sf
95Z+nFShH5iCyYmAD/tfDQ/2zXNyIXH6bUVUDwWJJeK1U+IOZGfNz7w9ySyTd1Ud
FrZO2SimplgLUBEmaRw4dq0Cq49/4Z6f0u0EKI/jpaW+P8Tb9V1ST3J3SI3Yc1If
zt7kLe19lbfDFEA/wcbElLo0W1rCrFC7TuzxPdZtRWD4gMwPoDBks/Bm+pGrCI2V
oD2Opq0APRnWiHWQN8H/Tte+5HA2LXMhvIA1ricLuv/z2sIvQWUHgzOzfFD5YHKE
VBWIXMImfJg1Y2ecwICZkNAgEdO8Iewk4Wu1DhvA04zXvFG6koMK3tbIMxsjohqD
iWeWzjMDAgMBAAECggEACARHZ8c5GJBifT/W2jjvS4fQU/H5ZbNFsI3FVdszIaVX
8+L2gqQMmvvhg7IsRsyzBi3F9D4PQPq0HGTSn02yH4hhd2EnY3DXrU3ynT02l9Jo
uGQ/u6YDxUKh+wSbNFZ1BIfKm59CfeoAlxDlVPZpe/ixplkTazegUm4GGn4JBjsi
98pOobe6KZYjR1FdQSXQ/zyXJ0HMW0aUWy7SBCxsDHZjW6rRqn4ZyRomyzMzRxVg
ltCAB8ZAsjibqRAgG4lOdFQ8i3KtvFl2wj1hOiiGb61uPyKqHbzQiQBHb7v5QSOW
ga1ccQNyJ8lcn+HAVEc436IoTujbXhx6R9JV/7VdjQKBgQDEs89SyKFqW9dods/P
dCqPStDQiIITYKT9qZai2+IwwSawm+uNb8ZRk+0L6YwL4ZqSDaiVo5TbBAdU3vlx
5uDUsWC4cpOYgbA1PNcy+UOKoB5w9Q/9eQSN5dN3YLEmNaO9r7Eoa5G6XFAJIIRd
0mWFgTMQfrRW4CxyjxBt7GlN1QKBgQC1WUKJFMuMvQyMwmuGVR1sV7vsG+tdY+NV
JAFgGtsC9X48R0NuieaS7jiOiKhf1Hp1hNievymGqN13TLRsUAVy3FEzvz9Xv5wz
7gauYPJFvDfMKjtczgVTF6Bkuu/EtDsxU0ihF9ylEZQE54At4z/09XP36gXcIYW4
gEuiHIBxdwKBgHENs5Qt8fAyVw0YdHoUlz2CT5/7RV+jxVBxvz1dSZSh/v20r967
pm1OqwiUs8REZ1WYehN/UFfM6fXSreXUP9afGCsK11/o8gC3Nix67WHog3aCsy0I
+CAiPrc3ILoITFMCfrzvjnNhCTMluE5AVc80ocfSOw5Pi9xP865qGV/9AoGAJv/x
LSXJuJSXdFYDZbo8P6aGhXpjnYvzwcWifhaIbxR4d2IkAEuryIFurEh7XVCGnWta
YpldxtXd+uAGFR0IbvEujWBdsPNGFmzkBvBWaeTWYxPZ/FVaE8qK9d613amc6Tit
7+b/zof/OybGDBhJEtcOf4xWgka7STheC5jxjnUCgYBGkzFIu1VHP9UXy2SXpKBf
Lb+hJ7B3M9Bsho0ih2rl40heYqGORsyQLUvukaUCX50T0jBEx+VbFLb/e4R7za+c
m2W88dctOOwb6/krHFtY6UUFmVox34kiWozz1YfOoC3hYM5Sfv6454o7Tnnjxrdw
oj8IYIvSbGiEgMjs5Epucg==
-----END PRIVATE KEY-----
```

** IMPORTANT** : Copiez la clé privée avec les vrais sauts de ligne (pas les `\n`).

### ÉTAPE 3 : Installer les dépendances React Native

```bash
cd D:\Projet\parking_mobile
npm install @react-native-firebase/app @react-native-firebase/messaging
npx react-native link @react-native-firebase/messaging
```

### ÉTAPE 4 : Ajouter la dépendance Hibernate JSON dans pom.xml

Ajoutez cette dépendance dans `pom.xml` :

```xml
<dependency>
    <groupId>io.hypersistence</groupId>
    <artifactId>hypersistence-utils-hibernate-60</artifactId>
    <version>3.7.0</version>
</dependency>
```

### ÉTAPE 5 : Configurer les variables d'environnement Supabase

Ajoutez dans votre `.env` (dev) ou variables Render (prod) :

```
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZicGVmYmpveHpreG9tYmRjcWlmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE3MDQ1NzcsImV4cCI6MjA0NzI4MDU3N30.Q0VVy-gVjvLhJTzG5xjNfD1RCEYxY9V0lGT9yZhF5Ks
```

### ÉTAPE 6 : Rebuild et tester

```bash
# Backend
cd D:\Projet\uparkBack\upark
docker-compose down
docker-compose up --build

# Frontend
cd D:\Projet\parking_mobile
npm run android
```

---

## 🧪 TESTER LES NOTIFICATIONS

### Test 1 : Enregistrer le token FCM

1. Lancez l'app React Native
2. Vérifiez les logs : vous devriez voir `🔑 FCM Token obtenu: ...`
3. Le token est automatiquement envoyé au backend

### Test 2 : Créer une demande de réservation

1. Connectez-vous avec un utilisateur
2. Faites une demande de réservation sur un parking
3. Le propriétaire du parking devrait recevoir une notification

### Test 3 : Accepter/Refuser une demande

1. Connectez-vous en tant que propriétaire
2. Acceptez ou refusez une demande
3. Le demandeur devrait recevoir une notification push

---

## 📊 API ENDPOINTS DISPONIBLES

### Notifications
- `GET /api/v1/notifications/user/{userId}` - Récupérer les notifications
- `GET /api/v1/notifications/user/{userId}/unread-count` - Compter les non lues
- `PUT /api/v1/notifications/{id}/read` - Marquer comme lue
- `PUT /api/v1/notifications/user/{userId}/read-all` - Marquer toutes comme lues
- `DELETE /api/v1/notifications/{id}` - Supprimer une notification

### Tokens FCM
- `POST /api/v1/device-tokens` - Enregistrer un token FCM

**Payload exemple** :
```json
{
  "userId": 14,
  "token": "f7Xk9...",
  "platform": "android"
}
```

---

##  PROCHAINES ÉTAPES

1. **Créer l'écran Notifications.jsx** (déjà le squelette existe)
2. **Ajouter des icônes de badge** pour le nombre de notifications non lues
3. **Configurer pg_cron** dans Supabase pour nettoyage automatique (90 jours)
4. **Tester sur iOS** (nécessite certificat APNs)

---

## 🐛 DÉPANNAGE

### Problème : Token FCM non reçu
- Vérifiez que `google-services.json` est bien dans `android/app/`
- Vérifiez les permissions dans `AndroidManifest.xml`
- Rebuild l'app : `npm run android`

### Problème : Notification non envoyée
- Vérifiez les logs backend : `Erreur: Aucun token FCM trouvé`
- Vérifiez que l'Edge Function Supabase est déployée
- Vérifiez les secrets Firebase dans Supabase

### Problème : Erreur 500 Edge Function
- Vérifiez que les 3 secrets Firebase sont configurés
- Vérifiez que la clé privée est au bon format (avec sauts de ligne)

---

##  CHECKLIST FINALE

- [ ] Script SQL exécuté dans Supabase 
- [ ] Secrets Firebase configurés dans Supabase 
- [ ] Edge Function déployée 
- [ ] Dépendances npm installées 
- [ ] Dépendance Hibernate JSON dans pom.xml 
- [ ] Backend redémarré 
- [ ] App React Native rebuild 
- [ ] Token FCM enregistré 
- [ ] Test notification réussi 

Bonne chance ! 
