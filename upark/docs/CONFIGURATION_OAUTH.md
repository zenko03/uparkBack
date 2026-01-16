# Configuration OAuth2 (Google/Facebook)

## Prerequis

- Compte Google (Google Cloud Console)
- Compte Facebook (Facebook for Developers)

## Services utilises

| Provider | Usage |
|----------|-------|
| Google OAuth2 | Connexion avec compte Google |
| Facebook OAuth2 | Connexion avec compte Facebook |

## 1. Configuration Google OAuth2

### 1.1 Creer un projet Google Cloud

1. Google Cloud Console (https://console.cloud.google.com)
2. Create Project
3. Nom : `upark`

### 1.2 Activer les APIs

APIs & Services > Enable APIs

Activer :
- People API
- Google Identity Platform

### 1.3 Configurer l'ecran de consentement

APIs & Services > OAuth consent screen

| Parametre | Valeur |
|-----------|--------|
| User Type | External |
| App name | UPark |
| Support email | (votre email) |
| Scopes | email, profile, openid |

### 1.4 Creer les credentials

APIs & Services > Credentials > Create Credentials > OAuth client ID

#### Web Application (pour le backend)

| Parametre | Valeur |
|-----------|--------|
| Application type | Web application |
| Name | upark-backend |
| Authorized redirect URIs | (optionnel) |

Resultat :
- Client ID : `918409349260-xxx.apps.googleusercontent.com`
- Client Secret : (non utilise pour vérification ID Token)

#### Android Application (pour le mobile)

| Parametre | Valeur |
|-----------|--------|
| Application type | Android |
| Name | upark-android |
| Package name | `com.parking_mobile` |
| SHA-1 certificate | (voir section 1.5) |

### 1.5 Obtenir le SHA-1 Android

Debug (developpement) :
```bash
cd parking_mobile/android
./gradlew signingReport
```

Chercher :
```
Variant: debug
Config: debug
Store: ~/.android/debug.keystore
SHA1: AA:BB:CC:...
```

Release (production) :
```bash
keytool -list -v -keystore your-release.keystore
```

### 1.6 Configuration backend

`application.properties` :
```properties
oauth.google.client-id=${OAUTH_GOOGLE_CLIENT_ID}
```

Valeur : Client ID du Web Application (pas Android).

### 1.7 Configuration mobile

`google-services.json` contient automatiquement les Client IDs.

`SocialLoginButtons.jsx` :
```javascript
import { GoogleSignin } from '@react-native-google-signin/google-signin';

GoogleSignin.configure({
  webClientId: '918409349260-xxx.apps.googleusercontent.com',
  offlineAccess: true,
});
```

Le `webClientId` doit correspondre au Client ID du Web Application.

## 2. Configuration Facebook OAuth2

### 2.1 Creer une application Facebook

1. Facebook for Developers (https://developers.facebook.com)
2. My Apps > Create App
3. Type : Consumer
4. Nom : `UPark`

### 2.2 Configuration de base

App Settings > Basic

| Element | Exemple |
|---------|---------|
| App ID | `1522395478989208` |
| App Secret | `c46cfbdb...` |
| Privacy Policy URL | (obligatoire pour production) |

### 2.3 Ajouter la plateforme Android

App Settings > Basic > Add Platform > Android

| Parametre | Valeur |
|-----------|--------|
| Package Name | `com.parking_mobile` |
| Default Activity | `com.parking_mobile.MainActivity` |
| Key Hashes | (voir section 2.4) |

### 2.4 Generer les Key Hashes

Debug :
```bash
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
```
Password : `android`

Release :
```bash
keytool -exportcert -alias YOUR_ALIAS -keystore your-release.keystore | openssl sha1 -binary | openssl base64
```

### 2.5 Permissions Facebook

App Review > Permissions and Features

Demander :
- email (auto-approuve)
- public_profile (auto-approuve)

### 2.6 Configuration backend

`application.properties` :
```properties
oauth.facebook.app-id=${OAUTH_FACEBOOK_APP_ID}
oauth.facebook.app-secret=${OAUTH_FACEBOOK_APP_SECRET}
```

### 2.7 Configuration mobile

`android/app/src/main/res/values/strings.xml` :
```xml
<string name="facebook_app_id">1522395478989208</string>
<string name="facebook_client_token">YOUR_CLIENT_TOKEN</string>
<string name="fb_login_protocol_scheme">fb1522395478989208</string>
```

## 3. Fichiers du backend

| Fichier | Usage |
|---------|-------|
| `OAuthService.java` | Verification tokens Google/Facebook |
| `AuthenticationController.java` | Endpoints login OAuth |
| `application.properties` | Configuration Client IDs |

## 4. Fichiers du mobile

| Fichier | Usage |
|---------|-------|
| `SocialLoginButtons.jsx` | Boutons connexion sociale |
| `authService.js` | Appels API authentification |
| `google-services.json` | Configuration Google |
| `strings.xml` | Configuration Facebook |

## 5. Flux d'authentification

### Google

```
1. Mobile : GoogleSignin.signIn()
2. Mobile : Obtient idToken
3. Mobile : POST /api/v1/auth/google {idToken}
4. Backend : OAuthService.verifyGoogleToken()
5. Backend : Verifie signature avec Google servers
6. Backend : Cree/recupere utilisateur
7. Backend : Retourne JWT + userData
8. Mobile : Stocke JWT, navigue vers home
```

### Facebook

```
1. Mobile : LoginManager.logInWithPermissions()
2. Mobile : Obtient accessToken
3. Mobile : POST /api/v1/auth/facebook {accessToken}
4. Backend : OAuthService.verifyFacebookToken()
5. Backend : Appelle Graph API Facebook
6. Backend : Cree/recupere utilisateur
7. Backend : Retourne JWT + userData
8. Mobile : Stocke JWT, navigue vers home
```

## 6. Recapitulatif des identifiants

### Google

| Element | Emplacement |
|---------|-------------|
| Web Client ID | application.properties, SocialLoginButtons.jsx |
| Android Client ID | google-services.json (auto) |
| SHA-1 Debug | Google Cloud Console |
| SHA-1 Release | Google Cloud Console |

### Facebook

| Element | Emplacement |
|---------|-------------|
| App ID | application.properties, strings.xml |
| App Secret | application.properties (backend only) |
| Client Token | strings.xml |
| Key Hash | Facebook App Settings |

## 7. Variables Render

| Variable | Source |
|----------|--------|
| `OAUTH_GOOGLE_CLIENT_ID` | Google Cloud Console |
| `OAUTH_FACEBOOK_APP_ID` | Facebook Developers |
| `OAUTH_FACEBOOK_APP_SECRET` | Facebook Developers |

## 8. Configuration pour nouveau projet

### Google

1. Creer projet Google Cloud
2. Configurer OAuth consent screen
3. Creer Web Application credentials
4. Creer Android credentials avec SHA-1
5. Telecharger google-services.json
6. Mettre a jour application.properties
7. Mettre a jour webClientId dans le mobile

### Facebook

1. Creer application Facebook
2. Ajouter plateforme Android
3. Ajouter Key Hashes
4. Mettre a jour application.properties
5. Mettre a jour strings.xml

## 9. Troubleshooting

### Google : "Token invalide"
- Verifier webClientId (Web, pas Android)
- Verifier SHA-1 dans Google Cloud Console
- Regenerer google-services.json

### Google : "verifier.verify() returned null"
- Token expire (demander nouveau token)
- Client ID incorrect
- Probleme reseau vers Google

### Facebook : "Invalid token"
- Verifier App ID et App Secret
- Verifier Key Hash
- App en mode developpement (seuls admins peuvent tester)

### Facebook : Email null
- Utilisateur n'a pas autorise l'email
- Email non verifie sur Facebook
