# Documentation Technique - UPark Backend

Ce repertoire contient la documentation technique pour la configuration et le deploiement du projet.

## Documents disponibles

| Document | Description |
|----------|-------------|
| [DEPLOIEMENT_RENDER.md](DEPLOIEMENT_RENDER.md) | Deploiement sur Render.com |
| [CONFIGURATION_SUPABASE.md](CONFIGURATION_SUPABASE.md) | Configuration Supabase (Database, Storage, Edge Functions) |
| [CONFIGURATION_FIREBASE.md](CONFIGURATION_FIREBASE.md) | Configuration Firebase/FCM (Push Notifications) |
| [CONFIGURATION_OAUTH.md](CONFIGURATION_OAUTH.md) | Configuration OAuth2 (Google, Facebook) |

## Structure du projet

```
upark/
  +-- docs/                    # Documentation technique
  +-- src/main/java/...        # Code source Java
  +-- src/main/resources/      # Configuration
       +-- application.properties
  +-- supabase/                # Edge Functions Supabase
       +-- functions/
            +-- send-push-notification/
  +-- .env                     # Variables locales (non versionne)
  +-- Dockerfile               # Container Docker
  +-- render.yaml              # Configuration Render
  +-- pom.xml                  # Dependances Maven
```

## Comptes externes requis

| Service | Usage | Document |
|---------|-------|----------|
| Render.com | Hebergement API | DEPLOIEMENT_RENDER.md |
| Supabase | Database + Storage + Edge Functions | CONFIGURATION_SUPABASE.md |
| Firebase | Push Notifications (FCM) | CONFIGURATION_FIREBASE.md |
| Google Cloud Console | OAuth2 + API Client | CONFIGURATION_OAUTH.md |
| Facebook Developers | OAuth2 Facebook | CONFIGURATION_OAUTH.md |

## Variables d'environnement

### Render (production)

| Variable | Source |
|----------|--------|
| `SPRING_DATASOURCE_URL` | Supabase > Database |
| `SPRING_DATASOURCE_USERNAME` | Supabase > Database |
| `SPRING_DATASOURCE_PASSWORD` | Supabase > Database |
| `SUPABASE_URL` | Supabase > API |
| `SUPABASE_SERVICE_ROLE_KEY` | Supabase > API |
| `SUPABASE_ANON_KEY` | Supabase > API |
| `OAUTH_GOOGLE_CLIENT_ID` | Google Cloud Console |
| `OAUTH_FACEBOOK_APP_ID` | Facebook Developers |
| `OAUTH_FACEBOOK_APP_SECRET` | Facebook Developers |

### Supabase Edge Functions (secrets)

| Variable | Source |
|----------|--------|
| `FIREBASE_PROJECT_ID` | Firebase Console |
| `FIREBASE_CLIENT_EMAIL` | Service Account JSON |
| `FIREBASE_PRIVATE_KEY` | Service Account JSON |

## Fichiers sensibles a sauvegarder

| Fichier | Description |
|---------|-------------|
| `google-services.json` | Configuration Firebase Android |
| `upark-xxx-firebase-adminsdk.json` | Service Account Firebase |
| `.env` | Variables locales (non versionne) |

## Instructions pour nouveau developpeur

1. Cloner le repository
2. Lire les documents dans l'ordre :
   - CONFIGURATION_SUPABASE.md (base de donnees)
   - CONFIGURATION_FIREBASE.md (notifications push)
   - CONFIGURATION_OAUTH.md (authentification sociale)
   - DEPLOIEMENT_RENDER.md (mise en production)
3. Creer les comptes externes necessaires
4. Configurer les variables dans `.env` (local) ou Render (prod)

## Ordre de configuration pour nouveau projet

1. Supabase : creer projet, executer scripts SQL
2. Firebase : creer projet, telecharger google-services.json
3. Google Cloud : configurer OAuth2
4. Facebook : configurer application
5. Supabase Edge Functions : deployer et configurer secrets
6. Render : deployer backend avec variables d'environnement
