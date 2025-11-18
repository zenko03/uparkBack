# Guide de déploiement sur Render

Ce guide vous explique comment déployer votre application Spring Boot sur Render gratuitement.

## Prérequis

- Compte Render.com (inscription gratuite)
- Repository GitHub public ou privé
- Application Spring Boot prête pour la production

## Étapes de déploiement

### 1. Préparation de l'application

Avant de déployer, vous devez adapter votre application pour fonctionner avec Render :

#### a) Configuration de la base de données

Modifiez votre fichier `src/main/resources/application.properties` pour utiliser la variable d'environnement fournie par Render :

```properties
spring.application.name=upark
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/upark}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:toky}
spring.datasource.driver-class-name=org.postgresql.Driver
server.port=${PORT:8080}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

#### b) Configuration du serveur

Assurez-vous que votre application peut fonctionner sur le port fourni par Render. Dans votre classe principale `UparkApplication.java`, vous pouvez ajouter une configuration pour gérer les variables d'environnement :

```java
@SpringBootApplication
public class UparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(UparkApplication.class, args);
    }

}
```

### 2. Configuration du déploiement sur Render

#### a) Création d'un fichier système de build

Créez un fichier `system.properties` à la racine de votre projet pour spécifier la version de Java :

```
java.runtime.version=17
```

#### b) Configuration Maven

Assurez-vous que votre `pom.xml` contient bien la configuration du plugin Spring Boot pour créer un JAR exécutable (ce qui est déjà le cas dans votre projet).

### 3. Déploiement sur Render

#### a) Connexion à Render

1. Allez sur [https://render.com](https://render.com) et créez un compte gratuit
2. Connectez votre compte GitHub à Render

#### b) Création du service Web

1. Cliquez sur "New +" et sélectionnez "Web Service"
2. Sélectionnez votre repository GitHub contenant l'application Spring Boot
3. Choisissez la branche à déployer (généralement `main` ou `master`)
4. Donnez un nom à votre service (ex: `upark-api`)
5. Sélectionnez la région de déploiement
6. Pour le type d'environnement, sélectionnez "Maven"
7. Pour la version de Runtime, sélectionnez "Java 17"
8. Dans "Build Command", entrez :
   ```
   ./mvnw clean package -DskipTests
   ```
9. Dans "Start Command", entrez :
   ```
   java -jar target/upark-0.0.1-SNAPSHOT.jar
   ```
10. Cliquez sur "Create Web Service"

### 4. Configuration de la base de données PostgreSQL

1. Cliquez sur "New +" et sélectionnez "PostgreSQL"
2. Donnez un nom à votre base de données (ex: `upark-db`)
3. Sélectionnez le plan gratuit
4. Cliquez sur "Create Database"

Une fois la base de données créée, vous verrez les détails de connexion.

### 5. Connexion de l'application à la base de données

1. Retournez sur votre service Web
2. Cliquez sur "Environment" dans le menu de gauche
3. Cliquez sur "Edit" pour modifier les variables d'environnement
4. Ajoutez les variables suivantes :
   - `DATABASE_URL` : URL de connexion à la base de données (disponible dans les détails de la base PostgreSQL)
   - `DB_USERNAME` : Nom d'utilisateur de la base de données
   - `DB_PASSWORD` : Mot de passe de la base de données

5. Cliquez sur "Save Changes"
6. Forcez un nouveau déploiement en cliquant sur "Manual Deploy" → "Deploy latest commit"

### 6. Configuration de la base de données

Votre application utilisera `spring.jpa.hibernate.ddl-auto=update` pour créer automatiquement les tables lors du premier démarrage. Si vous préférez un contrôle plus fin, vous pouvez désactiver cette option et utiliser des scripts SQL pour initialiser la base de données.

### 7. Vérification du déploiement

1. Une fois le déploiement terminé, vous verrez l'URL de votre application (ex: `https://upark-api.onrender.com`)
2. Accédez à cette URL pour vérifier que votre application fonctionne
3. Vous pouvez également consulter les logs en cliquant sur "Logs" dans le menu de gauche

## Configuration supplémentaire

### Variables d'environnement recommandées

Ajoutez ces variables d'environnement à votre service Web :

- `SPRING_PROFILES_ACTIVE` : `prod` (pour activer le profil de production)
- `JAVA_OPTS` : `-Xmx512m` (limite la mémoire utilisée par l'application)
- `SERVER_SERVLET_CONTEXT_PATH` : Si vous souhaitez un chemin de base spécifique

### Configuration de sécurité

Pour une application en production, assurez-vous de :

1. Utiliser des clés JWT sécurisées via des variables d'environnement
2. Configurer CORS pour autoriser uniquement les domaines nécessaires
3. Activer HTTPS
4. Configurer les en-têtes de sécurité appropriées

### Surveillance et logs

- Consultez régulièrement les logs via l'interface Render
- Considérez l'ajout de métriques et de monitoring si nécessaire
- Configurez des alertes pour les erreurs critiques

## Dépannage

### Problèmes courants

1. **Timeout au démarrage** : L'application Spring Boot peut prendre plus de 30 secondes à démarrer. Assurez-vous que votre application répond à `/` ou configurez un endpoint de santé.

2. **Problèmes de base de données** : Vérifiez que les variables d'environnement de la base de données sont correctement configurées.

3. **Problèmes de mémoire** : Si votre application dépasse la limite de mémoire gratuite, optimisez votre code ou envisagez un plan payant.

### Endpoint de santé

Ajoutez l'endpoint de santé Spring Boot pour surveiller l'état de votre application :

Ajoutez cette dépendance à votre `pom.xml` :
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Et ajoutez cette configuration à votre `application.properties` :
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

## Limitations du plan gratuit

- 750 heures de service gratuit par mois
- Redémarrage automatique toutes les 15 minutes si l'application est inactive
- Limite de 1 Go de stockage pour la base de données PostgreSQL gratuite
- Limite de bande passante

## Mises à jour

Pour déployer des mises à jour :

1. Poussez vos modifications sur GitHub
2. Le déploiement se fera automatiquement (si activé) ou manuellement via "Manual Deploy"