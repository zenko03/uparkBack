# Utiliser Java 17
FROM eclipse-temurin:17

# Créer le répertoire de travail
WORKDIR /app

# Copier seulement les fichiers Maven pour accélérer le build
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Pré-télécharger les dépendances Maven
RUN ./mvnw dependency:go-offline

# Exposer le port Spring Boot
EXPOSE 8080

# Démarrer Spring Boot avec DevTools
CMD ["./mvnw", "spring-boot:run"]
