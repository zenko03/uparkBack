# Utiliser Java 17
FROM eclipse-temurin:17

# Créer le répertoire de travail
WORKDIR /app

# Copier seulement les fichiers Maven pour accélérer le build
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Ajouter les permissions d'exécution
RUN chmod +x mvnw

# Pré-télécharger les dépendances Maven
RUN ./mvnw dependency:go-offline

# Copier le code source
COPY src src

# Build l'application
RUN ./mvnw package -DskipTests

# Exposer le port Spring Boot
EXPOSE 8080

# Démarrer l'application avec le JAR compilé
CMD ["java", "-jar", "target/upark-0.0.1-SNAPSHOT.jar"]
