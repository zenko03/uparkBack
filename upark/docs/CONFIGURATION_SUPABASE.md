# Configuration Supabase

## Prerequis

- Compte Supabase (https://supabase.com)
- Projet Supabase cree

## Services utilises

| Service | Usage |
|---------|-------|
| Database (PostgreSQL) | Base de donnees principale |
| Storage | Stockage images parkings |
| Edge Functions | Envoi notifications push via FCM |
| Realtime | Synchronisation notifications (optionnel) |

## 1. Creation du projet

1. Supabase Dashboard > New Project
2. Configuration :
   - Name: `upark`
   - Database Password: (noter et sauvegarder)
   - Region: `EU West` (proche de Render)

## 2. Recuperer les cles API

Dashboard > Settings > API

| Cle | Variable backend | Usage |
|-----|------------------|-------|
| Project URL | `SUPABASE_URL` | URL de base |
| anon (public) | `SUPABASE_ANON_KEY` | Acces public limite |
| service_role | `SUPABASE_SERVICE_ROLE_KEY` | Acces admin complet |

## 3. Configuration base de donnees

### 3.1 Connection string

Dashboard > Settings > Database > Connection string

Format URI :
```
postgresql://postgres.[PROJECT_REF]:[PASSWORD]@aws-1-eu-west-2.pooler.supabase.com:6543/postgres
```

Variables pour le backend :
| Partie | Variable |
|--------|----------|
| URL complete | `SPRING_DATASOURCE_URL` |
| Username | `SPRING_DATASOURCE_USERNAME` |
| Password | `SPRING_DATASOURCE_PASSWORD` |

Exemple :
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-eu-west-2.pooler.supabase.com:6543/postgres?prepareThreshold=0
SPRING_DATASOURCE_USERNAME=postgres.fbpefbjoxzkxombdcqif
SPRING_DATASOURCE_PASSWORD=upark2025!!
```

### 3.2 Executer les scripts SQL

Dashboard > SQL Editor

Executer dans l'ordre :
1. `src/main/java/com/urban/upark/data/Parking-script.sql` - Schema principal
2. `src/main/java/com/urban/upark/data/init-notifications.sql` - Tables notifications
3. `src/main/java/com/urban/upark/data/dashboard-views.sql` - Vues statistiques

## 4. Configuration Storage

### 4.1 Creer le bucket

Dashboard > Storage > New bucket

| Parametre | Valeur |
|-----------|--------|
| Name | `parking-images` |
| Public | `true` |

### 4.2 Policies (RLS)

Dashboard > Storage > parking-images > Policies

Creer les policies :

**SELECT (lecture publique)** :
```sql
CREATE POLICY "Public read access"
ON storage.objects FOR SELECT
USING (bucket_id = 'parking-images');
```

**INSERT (upload authentifie)** :
```sql
CREATE POLICY "Authenticated upload"
ON storage.objects FOR INSERT
WITH CHECK (bucket_id = 'parking-images');
```

### 4.3 Fichiers concernes dans le backend

| Fichier | Usage |
|---------|-------|
| `SupabaseStorageService.java` | Upload images |
| `ImageProxyController.java` | Proxy images |
| `ParkingImageService.java` | Gestion images parking |

## 5. Edge Functions

### 5.1 Structure

```
supabase/
  functions/
    send-push-notification/
      index.ts        # Code de la fonction
      deno.json       # Configuration Deno
      import_map.json # Imports
```

### 5.2 Deploiement

#### Via Supabase CLI

```bash
npm install -g supabase
supabase login
supabase link --project-ref fbpefbjoxzkxombdcqif
supabase functions deploy send-push-notification
```

#### Via Dashboard

Dashboard > Edge Functions > (copier-coller le code)

### 5.3 Secrets Edge Functions

Dashboard > Edge Functions > Secrets

| Secret | Valeur | Source |
|--------|--------|--------|
| `FIREBASE_PROJECT_ID` | `upark-c2eb4` | Firebase Console |
| `FIREBASE_CLIENT_EMAIL` | `firebase-adminsdk-xxx@upark-c2eb4.iam.gserviceaccount.com` | Service Account JSON |
| `FIREBASE_PRIVATE_KEY` | `-----BEGIN PRIVATE KEY-----...` | Service Account JSON |

Recuperer les valeurs :
1. Firebase Console > Project Settings > Service Accounts
2. Generate new private key
3. Telecharger le JSON
4. Extraire `project_id`, `client_email`, `private_key`

La cle privee doit contenir les vrais sauts de ligne (pas `\n`).

## 6. Realtime (optionnel)

Pour les notifications en temps reel.

Dashboard > Database > Replication

Activer pour la table `notifications` :
```sql
ALTER PUBLICATION supabase_realtime ADD TABLE notifications;
```

## 7. Configuration application.properties

```properties
# Supabase Database
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# SSL requis
spring.datasource.hikari.data-source-properties.sslmode=require
spring.datasource.hikari.data-source-properties.ssl=true

# Supabase API
supabase.url=${SUPABASE_URL:https://fbpefbjoxzkxombdcqif.supabase.co}
supabase.key=${SUPABASE_SERVICE_ROLE_KEY}
supabase.anon.key=${SUPABASE_ANON_KEY}
```

## 8. Fichiers du backend utilisant Supabase

| Fichier | Service | Variables |
|---------|---------|-----------|
| `SupabaseStorageService.java` | Storage | `supabase.url`, `supabase.key` |
| `NotificationService.java` | Edge Functions | `supabase.url`, `supabase.key` |
| `ImageProxyController.java` | Storage | `supabase.url` |
| `application.properties` | Database | `spring.datasource.*` |

## 9. Recapitulatif des identifiants

### A sauvegarder (compte Supabase)

| Element | Exemple |
|---------|---------|
| Project Reference | `fbpefbjoxzkxombdcqif` |
| Project URL | `https://fbpefbjoxzkxombdcqif.supabase.co` |
| Database Password | `upark2025!!` |
| Anon Key | `eyJhbGci...` |
| Service Role Key | `eyJhbGci...` |

### Variables pour nouveau projet

Si changement de projet Supabase, modifier :

| Fichier | Elements |
|---------|----------|
| `application.properties` | URL, username, password |
| Render Environment | Toutes les variables SUPABASE_* |
| Supabase Edge Functions | Secrets Firebase |

## 10. Troubleshooting

### Erreur connexion database
- Verifier le password
- Utiliser Connection Pooling (port 6543)
- Verifier `?prepareThreshold=0` dans l'URL

### Erreur Storage upload
- Verifier bucket public
- Verifier policies RLS
- Verifier service_role key

### Erreur Edge Function
- Verifier les secrets
- Consulter les logs : Dashboard > Edge Functions > Logs
