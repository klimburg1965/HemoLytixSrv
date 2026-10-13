# HemoLytix Server

HemoLytix Server ist die REST-API-Backend-Anwendung für das **HemoLytix**-System zur Erfassung, Analyse und Auswertung von Labor- und Blutbilddaten (u. a. kleines und großes Blutbild, Differenzialblutbild) inklusive automatisierter PDF-Befundgenerierung und auditierter Datenhaltung.

---

## 🛠 Tech Stack & Voraussetzungen

* **Java**: 21
* **Framework**: Spring Boot 3.3.4
* **Security**: Spring Security (JWT / Role-based Access Control)
* **Persistenz**: Spring Data JPA / Hibernate
* **Datenbank**: H2 Database (File-based Storage)
* **Dokumentation**: SpringDoc OpenAPI / Swagger UI
* **PDF-Generierung**: OpenPDF (LibrePDF)
* **Build-Tool**: Apache Maven (3.9+)

---

## 🚀 Schnellstart & Dokumentation

### 1. Anwendung starten

Klone das Repository und starte die Anwendung über Maven:

```bash
mvn spring-boot:run
```

Der Server startet standardmäßig auf Port **8080**.

### 2. Swagger UI & API-Dokumentation

Nach dem Start ist die interaktive OpenAPI-Dokumentation erreichbar unter:

* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
* **OpenAPI Spec (JSON)**: `http://localhost:8080/v3/api-docs`

### 3. H2 Datenbank-Konsole

Die lokale H2-Datenbank kann während der Entwicklung über die Konsole eingesehen werden:

* **URL**: `http://localhost:8080/h2-console`
* **JDBC-URL**: `jdbc:h2:file:./data/hemolytixdb`
* **User**: `sa`
* **Password**: *(leer lassen)*

---

## 🔐 Konfiguration & Umgebungsvariablen

Die Anwendung verwendet Umgebungsvariablen zur sicheren Konfiguration sensitiver Parameter:

| Variable | Beschreibung | Standardwert (Lokal) |
| :--- | :--- | :--- |
| `JWT_SECRET` | Secret Key für die HMAC-SHA256 Signierung der JWTs | *Demowert in `application.properties`* |
| `SERVER_PORT` | HTTP-Port der Anwendung | `8080` |

### Beispiel für den Produktionsstart:

```bash
export JWT_SECRET="DeinKryptografischSichererSecretKeyHier_Mindestens256Bit"
java -jar target/hemolytix-server.jar
```

---

## 🏛 Architektur & Besonderheiten

* **DTO-Driven Architecture**: Klare Trennung zwischen JPA-Entities, Request-DTOs und Response-DTOs zur Vermeidung von Over-Posting und Information Leakage.
* **JPA Auditing**: Automatische Protokollierung von Erstell- und Änderungszeitstempeln sowie Benutzern (`createdAt`, `createdBy`, `updatedAt`, `updatedBy`) via `@EnableJpaAuditing` und `AuditingEntityListener`.
* **Prozentual-Validierung**: Das Differenzialblutbild unterstützt inklusive Grenzwert `0.0 %` valide Befunddaten (`@PositiveOrZero`).
* **RFC 7807 Exception Handling**: Einheitliche Fehlerrückgaben im `ProblemDetail`-Format über einen zentralen `@ControllerAdvice`.

---

## 🧪 Tests ausführen

Führe alle Unit- und Controller-Tests aus:

```bash
mvn clean test
```

## 💻 Frontend & Web-Interface

Das Projekt beinhaltet neben dem Backend ein modernes, leichtgewichtiges Single-File-Web-Frontend (`index.html`), das direkt über den Server ausgeliefert wird und eine intuitive Bedienung für medizinisches Personal ermöglicht:

* **Modernes UI-Design**: Vollständig responsives Layout auf Basis von **Tailwind CSS (CDN)** mit klarer, klinischer Struktur.
* **Interaktive Datenvisualisierung**: Dynamische Verlaufsdiagramme für Laborwerte (wie Hämoglobin, Erythrozyten, Leukozyten und Hämatokrit) über **Chart.js**.
* **Zustandsgesteuerte Masken**: Nahtloser Wechsel zwischen Patienten-Stammdaten, Bearbeitungsmodus (Update), Neuerfassung von Patienten (inkl. Adress-Entity in OneToOne-Beziehung) und der Eingabe neuer Blutbilder mit integrierten medizinischen Plausibilitäts-Orientierungen.
* **Client-seitige JWT-Authentifizierung**: Sichere Anmeldung über REST-Endpunkte mit Token-Speicherung im LocalStorage und automatischer Absicherung aller API-Requests (`fetchWithAuth`).
* **Direkter PDF-Export**: Generierung und direkte Ansicht des offiziellen Patienten-PDF-Berichts im neuen Browser-Tab via `InputStreamResource` und Blob-Handling.

---

## 📄 Lizenz & Zweck

Dieses Projekt wurde als **Portfolio- und Showcase-Projekt** zur Demonstration von Spring Boot, REST-API-Design und Clean Architecture entwickelt. Der Quellcode steht unter der [MIT-Lizenz](LICENSE) frei zu Anschauungs- und Evaluierungszwecken zur Verfügung.