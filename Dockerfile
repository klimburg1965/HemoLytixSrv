# 1. Offizielles Eclipse Temurin Java 21 JDK Alpine Image als Basis
FROM eclipse-temurin:21-jdk-alpine

# 2. Arbeitsverzeichnis im Container festlegen
WORKDIR /app

# 3. Die von Maven gebaute JAR-Datei in den Container kopieren
COPY target/hemolytixsrv-0.0.1-SNAPSHOT.jar app.jar

# 4. Port 8080 nach außen freigeben
EXPOSE 8080

# 5. Startbefehl der Anwendung
ENTRYPOINT ["java", "-jar", "app.jar"]