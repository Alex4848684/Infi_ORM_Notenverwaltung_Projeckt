# Notenverwaltungssystem für Schulen

## Voraussetzungen
- Java 25
- MySQL

## Datenbank

Erstellen Sie eine MySQL-Datenbank mit dem Namen: schulverwaltung und fals die Tabellen nicht automatisch erstelt werden nach dem Ausführen folgenden Tabellen:

- Schueler
- Lehrer
- Noten
- Raum

## Datenbankverbindung

Passen Sie die MySQL-Daten in den Zeilen 28–30 an:

```java
String databaseUrl = "jdbc:mysql://localhost:3306/schulverwaltung?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
String username = "IHR_BENUTZERNAME";
String password = "IHR_PASSWORT";
```
