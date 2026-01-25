# Configuration Firebase (FCM)

## Prerequis

- Compte Google
- Projet Firebase

## Services utilises

| Service | Usage |
|---------|-------|
| Firebase Cloud Messaging (FCM) | Envoi notifications push |
| Service Account | Authentification API FCM |

## 1. Creation du projet Firebase

1. Firebase Console (https://console.firebase.google.com)
2. Add project
3. Configuration :
   - Name: `upark`
   - Google Analytics: optionnel

## 2. Ajouter l'application Android

Firebase Console > Project Overview > Add app > Android

| Parametre | Valeur |
|-----------|--------|
| Package name | `com.parking_mobile` |
| App nickname | `UPark Mobile` |
| Debug signing certificate | (optionnel) |

### Telecharger google-services.json

1. Project Settings > General > Your apps
2. Telecharger `google-services.json`
3. Placer dans : `parking_mobile/android/app/google-services.json`

## 3. Activer Cloud Messaging

Firebase Console > Project Settings > Cloud Messaging

Verifier que FCM est active (par defaut).

## 4. Creer un Service Account

### 4.1 Generer la cle

Firebase Console > Project Settings > Service accounts

1. Cliquer "Generate new private key"
2. Telecharger le fichier JSON
3. Sauvegarder dans un endroit securise

### 4.2 Contenu du fichier

Exemple `upark-c2eb4-firebase-adminsdk-xxx.json` :

```json
{
  "type": "service_account",
  "project_id": "upark-c2eb4",
  "private_key_id": "xxx",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvAI...\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-xxx@upark-c2eb4.iam.gserviceaccount.com",
  "client_id": "117049732273021997468",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token"
}
```

### 4.3 Valeurs importantes

| Champ | Usage |
|-------|-------|
| `project_id` | Identifiant projet |
| `client_email` | Email du service account |
| `private_key` | Cle privee pour signature JWT |

## 5. Configuration Supabase Edge Function

Les secrets Firebase sont stockes dans Supabase, pas dans le backend.

### 5.1 Ajouter les secrets

Supabase Dashboard > Edge Functions > Secrets

| Secret | Source (fichier JSON) |
|--------|----------------------|
| `FIREBASE_PROJECT_ID` | `project_id` |
| `FIREBASE_CLIENT_EMAIL` | `client_email` |
| `FIREBASE_PRIVATE_KEY` | `private_key` |

### 5.2 Format de la cle privee

La cle doit contenir les vrais sauts de ligne.

Transformer :
```
-----BEGIN PRIVATE KEY-----\nMIIEvAI...\n-----END PRIVATE KEY-----\n
```

En :
```
-----BEGIN PRIVATE KEY-----
MIIEvAI...
-----END PRIVATE KEY-----
```

## 6. Configuration Mobile (React Native)

### 6.1 Fichiers de configuration

| Fichier | Emplacement |
|---------|-------------|
| `google-services.json` | `parking_mobile/android/app/` |

### 6.2 Dependances

`package.json` :
```json
{
  "dependencies": {
    "@react-native-firebase/app": "^18.x",
    "@react-native-firebase/messaging": "^18.x"
  }
}
```

`android/app/build.gradle` :
```gradle
apply plugin: 'com.google.gms.google-services'
```

`android/build.gradle` :
```gradle
classpath 'com.google.gms:google-services:4.4.0'
```

### 6.3 Fichiers du frontend

| Fichier | Usage |
|---------|-------|
| `src/config/firebase.js` | Initialisation FCM |
| `src/services/notificationService.js` | API notifications |
| `App.tsx` | Listeners FCM |

## 7. Architecture des notifications

```
Mobile App
    |
    +-- Obtient token FCM (firebase.js)
    |
    +-- Enregistre token (POST /api/v1/device-tokens)
    |
    v
Backend (Spring Boot)
    |
    +-- Sauvegarde token (DeviceTokenController)
    |
    +-- Envoie notification (NotificationService)
    |
    +-- Appelle Edge Function Supabase
    |
    v
Supabase Edge Function
    |
    +-- Genere OAuth2 token (Service Account)
    |
    +-- Appelle FCM API v1
    |
    v
Firebase Cloud Messaging
    |
    +-- Envoie au device
    |
    v
Mobile App (notification recue)
```

## 8. Fichiers du backend

| Fichier | Usage |
|---------|-------|
| `DeviceTokenController.java` | Endpoint enregistrement token |
| `NotificationController.java` | Endpoints notifications |
| `NotificationService.java` | Logique envoi via Supabase |
| `DeviceToken.java` | Entite token |
| `Notification.java` | Entite notification |

## 9. Tables base de donnees

```sql
-- Tokens FCM par appareil
CREATE TABLE Device_tokens (
   Id_Device_token SERIAL PRIMARY KEY,
   token TEXT NOT NULL,
   platform VARCHAR(10) CHECK (platform IN ('android', 'ios')),
   is_active BOOLEAN DEFAULT TRUE,
   Id_Users INTEGER REFERENCES Users(Id_Users)
);

-- Historique notifications
CREATE TABLE Notifications (
   Id_Notification SERIAL PRIMARY KEY,
   title VARCHAR(255) NOT NULL,
   message TEXT NOT NULL,
   type VARCHAR(50),
   read BOOLEAN DEFAULT FALSE,
   Id_Users INTEGER REFERENCES Users(Id_Users)
);
```

## 10. Test des notifications

### Via Postman

```
POST /api/v1/notifications/test-push
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "userId": 14,
  "title": "Test",
  "message": "Message de test"
}
```

### Verifier dans les logs

Backend :
```
📤 Envoi notification à user 14
 Notification sauvegardée en BDD
 Notification envoyée au token: xxx...
```

Supabase Edge Functions > Logs :
```
Notification envoyée avec succès
```

## 11. Recapitulatif

### Fichiers a sauvegarder

| Fichier | Contenu |
|---------|---------|
| `google-services.json` | Configuration Android |
| `upark-xxx-firebase-adminsdk.json` | Service Account |

### Configuration pour nouveau projet

| Etape | Action |
|-------|--------|
| 1 | Creer projet Firebase |
| 2 | Ajouter app Android avec bon package name |
| 3 | Telecharger google-services.json |
| 4 | Generer nouveau Service Account |
| 5 | Configurer secrets Supabase Edge Function |

### Variables a modifier

| Emplacement | Element |
|-------------|---------|
| Supabase Secrets | FIREBASE_PROJECT_ID |
| Supabase Secrets | FIREBASE_CLIENT_EMAIL |
| Supabase Secrets | FIREBASE_PRIVATE_KEY |
| Mobile | google-services.json |

## 12. Troubleshooting

### Token FCM non genere
- Verifier google-services.json
- Verifier permissions Android (POST_NOTIFICATIONS pour Android 13+)

### 401 Unauthorized (Edge Function)
- Verifier SUPABASE_SERVICE_ROLE_KEY sur Render

### 500 Edge Function
- Verifier secrets Firebase dans Supabase
- Consulter logs Edge Function

### Notification non recue
- Verifier token enregistre dans device_tokens
- Verifier is_active = true
- Tester en background (notifications foreground necessitent handling)
