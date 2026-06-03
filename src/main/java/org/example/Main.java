package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random; // Import Random class

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Treiber nicht gefunden!");
        }

        String databaseUrl = "jdbc:mysql://localhost:3306/schulverwaltung?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "RySj3b481";

        try (ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, username, password)) {

            // Drop and create tables to ensure schema is up-to-date
            TableUtils.dropTable(connectionSource, Schueler.class, true);
            TableUtils.createTable(connectionSource, Schueler.class);
            TableUtils.dropTable(connectionSource, Lehrer.class, true); // Drop Lehrer table
            TableUtils.createTable(connectionSource, Lehrer.class); // Create Lehrer table
            TableUtils.dropTable(connectionSource, Raum.class, true); // Drop Raum table
            TableUtils.createTable(connectionSource, Raum.class); // Create Raum table
            TableUtils.dropTable(connectionSource, Note.class, true);
            TableUtils.createTable(connectionSource, Note.class);

            Dao<Schueler, Integer> schuelerDao = DaoManager.createDao(connectionSource, Schueler.class);
            Dao<Note, Integer> notenDao = DaoManager.createDao(connectionSource, Note.class);
            Dao<Lehrer, Integer> lehrerDao = DaoManager.createDao(connectionSource, Lehrer.class); // Create Lehrer DAO
            Dao<Raum, Integer> raumDao = DaoManager.createDao(connectionSource, Raum.class); // Create Raum DAO

            System.out.println("Lege neue Schüler, Lehrer, Räume und Noten an...");
            
            // --- Räume erstellen ---
            Raum r1 = new Raum("A101", 30); 
            raumDao.create(r1);
            Raum r2 = new Raum("B102", 25);
            raumDao.create(r2);
            Raum r3 = new Raum("C201", 20);
            raumDao.create(r3);

            // --- Lehrer erstellen ---
            Lehrer l1 = new Lehrer("Anna", "Schmidt", r1); // Assign r1 to l1
            lehrerDao.create(l1);
            Lehrer l2 = new Lehrer("Max", "Meier", r2); // Assign r2 to l2
            lehrerDao.create(l2);
            List<Lehrer> lehrerListe = Arrays.asList(l1, l2); // For random assignment

            // --- Schüler erstellen ---
            Schueler s1 = new Schueler("Max", "Mustermann", "3A", r1); 
            schuelerDao.create(s1);
            Schueler s2 = new Schueler("Lena", "Müller", "3A", r2);
            schuelerDao.create(s2);
            Schueler s3 = new Schueler("Tim", "Schneider", "3B", r3);
            schuelerDao.create(s3);
            Schueler s4 = new Schueler("Mia", "Weber", "3B", r1); 
            schuelerDao.create(s4);
            List<Schueler> schuelerListe = schuelerDao.queryForAll(); // Get all created students

            // --- Fächer definieren ---
            List<String> faecher = Arrays.asList("Mathe", "Deutsch", "Englisch", "Chemie", "Physik");
            Random random = new Random();

            // --- Noten erstellen: Jeder Schüler erhält eine Note in jedem Fach (Österreichisches System 1-5) ---
            for (Schueler schueler : schuelerListe) {
                for (String fach : faecher) {
                    int wert = random.nextInt(5) + 1; // Random grade between 1 and 5 (Austrian system)
                    Lehrer zugewiesenerLehrer = lehrerListe.get(random.nextInt(lehrerListe.size())); // Random teacher
                    Note neueNote = new Note(fach, wert, schueler, zugewiesenerLehrer);
                    notenDao.create(neueNote);
                }
            }

            // --- Notendurchschnitt berechnen und in der Schüler-Tabelle aktualisieren ---
            for (Schueler schueler : schuelerListe) {
                String[] resultAvg = notenDao.queryBuilder()
                    .selectRaw("AVG(wert)")
                    .where().eq("schueler_id", schueler.getId())
                    .queryRawFirst();
                double avgValue = resultAvg != null ? Double.parseDouble(resultAvg[0]) : 0;
                
                schueler.setSchuelerDurchschnitt(avgValue); // Durchschnitt im Schüler-Objekt speichern
                schuelerDao.update(schueler); // Schüler-Objekt in der Datenbank aktualisieren
            }


            System.out.println("\n--- Aktuelle Daten in der Datenbank ---");
            for (Schueler s : schuelerListe) { // Iterate through the list of students
                schuelerDao.refresh(s); 
                System.out.println("Schüler: " + s.getVorname() + " " + s.getNachname() + " (Klasse: " + s.getKlasse() + ") Raum: " + (s.getRaum() != null ? s.getRaum().getName() : "Kein Raum zugewiesen") + ", Schüler-Durchschnitt: " + s.getSchuelerDurchschnitt());

                List<Note> schuelerNoten = notenDao.queryForEq("schueler_id", s.getId());
                for (Note n : schuelerNoten) {
                    notenDao.refresh(n);
                    System.out.println("  -> Note: " + n.getFach() + " (" + n.getWert() + ") am " + n.getDatum() + " für Schüler: " + n.getSchueler().getNachname() + " eingetragen von Lehrer: " + n.getLehrer().getNachname());
                }
            }

            System.out.println("\n--- Verfügbare Räume ---");
            List<Raum> alleRaeume = raumDao.queryForAll();
            for (Raum r : alleRaeume) {
                System.out.println("Raum: " + r.getName() + " (Kapazität: " + r.getKapazitaet() + ")");
            }

            System.out.println("\n--- Verfügbare Lehrer ---");
            List<Lehrer> alleLehrer = lehrerDao.queryForAll();
            for (Lehrer l : alleLehrer) {
                lehrerDao.refresh(l); // Refresh to load the room object
                System.out.println("Lehrer: " + l.getVorname() + " " + l.getNachname() + " (Raum: " + (l.getRaum() != null ? l.getRaum().getName() : "Kein Raum zugewiesen") + ")");
            }

        } catch (SQLException e) {
            System.err.println("Datenbank-Fehler: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
