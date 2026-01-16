# Deploiement Backend sur Render

## Prerequis

- Compte Render.com
- Repository GitHub/GitLab connecte a Render
- Base de donnees Supabase configuree

## Architecture

```
Render (Web Service)
    |
    +-- Spring Boot API (port 8080)
    |
    +-- Supabase PostgreSQL (externe)
```

## Configuration Render

### 1. Creer un Web Service

1. Dashboard Render > New > Web Service
2. Connecter le repository GitHub
3. Configuration :
   - Name: `upark-api`
   - Region: `Frankfurt (EU Central)`
   - Branch: `prod`
   - Runtime: `Docker`
   - Dockerfile Path: `./Dockerfile`
   - Plan: `Free` ou `Starter`

### 2. Variables d'environnement (obligatoires)

Dashboard Render > Environment > Environment Variables

#### Base de donnees Supabase

| Variable | Exemple | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://aws-1-eu-west-2.pooler.supabase.com:6543/postgres?prepareThreshold=0` | URL connexion PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `postgres.fbpefbjoxzkxombdcqif` | Username Supabase |
| `SPRING_DATASOURCE_PASSWORD` | `motdepasse` | Password Supabase |

Ou trouver ces valeurs :
- Supabase > Settings > Database > Connection string > URI

#### Supabase Storage et Edge Functions

| Variable | Exemple | Description |
|----------|---------|-------------|
| `SUPABASE_URL` | `https://fbpefbjoxzkxombdcqif.supabase.co` | URL projet Supabase |
| `SUPABASE_SERVICE_ROLE_KEY` | `eyJhbGci...` | Cle service_role (admin) |
| `SUPABASE_ANON_KEY` | `eyJhbGci...` | Cle anon (public) |

Ou trouver ces valeurs :
- Supabase > Settings > API > Project API keys

#### OAuth2

| Variable | Exemple | Description |
|----------|---------|-------------|
| `OAUTH_GOOGLE_CLIENT_ID` | `918409349260-xxx.apps.googleusercontent.com` | Client ID Google |
| `OAUTH_FACEBOOK_APP_ID` | `1522395478989208` | App ID Facebook |
| `OAUTH_FACEBOOK_APP_SECRET` | `c46cfbdb...` | App Secret Facebook |

Ou trouver ces valeurs :
- Google Cloud Console > APIs & Services > Credentials
- Facebook for Developers > App Settings > Basic

#### Autres

| Variable | Valeur | Description |
|----------|--------|-------------|
| `PORT` | `8080` | Port du serveur (injecte par Render) |

### 3. application.properties (production)

Modifier pour utiliser les variables d'environnement :

```properties
# Port
server.port=${PORT:8080}

# Base de donnees (variables Render)
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# Supabase
supabase.url=${SUPABASE_URL:https://fbpefbjoxzkxombdcqif.supabase.co}
supabase.key=${SUPABASE_SERVICE_ROLE_KEY}
supabase.anon.key=${SUPABASE_ANON_KEY}

# OAuth2
oauth.google.client-id=${OAUTH_GOOGLE_CLIENT_ID}
oauth.facebook.app-id=${OAUTH_FACEBOOK_APP_ID}
oauth.facebook.app-secret=${OAUTH_FACEBOOK_APP_SECRET}
```

### 4. Dockerfile Production

Le Dockerfile actuel est optimise pour le dev local.
Pour la production, utiliser :

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Recapitulatif des variables Render

### Variables obligatoires

| Variable | Source |
|----------|--------|
| `SPRING_DATASOURCE_URL` | Supabase > Database |
| `SPRING_DATASOURCE_USERNAME` | Supabase > Database |
| `SPRING_DATASOURCE_PASSWORD` | Supabase > Database |
| `SUPABASE_SERVICE_ROLE_KEY` | Supabase > API |
| `SUPABASE_ANON_KEY` | Supabase > API |
| `OAUTH_GOOGLE_CLIENT_ID` | Google Cloud Console |

### Variables optionnelles

| Variable | Source |
|----------|--------|
| `SUPABASE_URL` | Supabase > API (valeur par defaut dans code) |
| `OAUTH_FACEBOOK_APP_ID` | Facebook Developers |
| `OAUTH_FACEBOOK_APP_SECRET` | Facebook Developers |

## Deploiement

### Methode 1 : Deploiement automatique

Chaque push sur `prod` declenche un deploiement.

### Methode 2 : Deploiement manuel

1. Dashboard Render > Service > Manual Deploy
2. Ou via CLI : `render deploy`

## Logs et Debug

```
Dashboard Render > Service > Logs
```

Filtrer par :
- `ERROR` : Erreurs critiques
- `WARN` : Avertissements
- `INFO` : Informations generales

## Domaine personnalise

1. Dashboard Render > Service > Settings > Custom Domains
2. Ajouter le domaine
3. Configurer DNS (CNAME vers `*.onrender.com`)

## Health Check

Render verifie automatiquement `/` ou configurer :

1. Settings > Health Check Path : `/api/v1/health`
2. Creer un endpoint health si necessaire

## Limites Plan Free

- Inactivite apres 15 min sans requete
- Cold start : 30-60 secondes
- 750 heures/mois

## Rollback

1. Dashboard Render > Service > Events
2. Cliquer sur un deploiement precedent
3. "Rollback to this deploy"

## Troubleshooting

### Erreur : Application failed to start
- Verifier les logs
- Verifier les variables d'environnement
- Verifier la connexion Supabase

### Erreur : Connection refused (database)
- Verifier l'IP Render autorisee dans Supabase
- Supabase > Settings > Database > Connection Pooling

### Cold start lent
- Passer au plan Starter ($7/mois)
- Ou utiliser un service de ping externe

## Fichiers concernes

```
upark/
  +-- Dockerfile           # Configuration Docker
  +-- render.yaml          # Configuration Render (optionnel)
  +-- pom.xml              # Dependances Maven
  +-- src/main/resources/
       +-- application.properties  # Configuration Spring
```
