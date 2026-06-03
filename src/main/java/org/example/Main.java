package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
            TableUtils.dropTable(connectionSource, Lehrer.class, true);
            TableUtils.createTable(connectionSource, Lehrer.class);
            TableUtils.dropTable(connectionSource, Raum.class, true);
            TableUtils.createTable(connectionSource, Raum.class);
            TableUtils.dropTable(connectionSource, Note.class, true);
            TableUtils.createTable(connectionSource, Note.class);

            Dao<Schueler, Integer> schuelerDao = DaoManager.createDao(connectionSource, Schueler.class);
            Dao<Note, Integer> notenDao = DaoManager.createDao(connectionSource, Note.class);
            Dao<Lehrer, Integer> lehrerDao = DaoManager.createDao(connectionSource, Lehrer.class);
            Dao<Raum, Integer> raumDao = DaoManager.createDao(connectionSource, Raum.class);

            Scanner scanner = new Scanner(System.in);

            // --- Räume vordefiniert erstellen ---
            System.out.println("=== RÄUME ERSTELLEN ===");
            Raum r1 = new Raum("A101", 30);
            raumDao.create(r1);
            Raum r2 = new Raum("B102", 25);
            raumDao.create(r2);
            Raum r3 = new Raum("C201", 20);
            raumDao.create(r3);
            System.out.println("Räume erstellt: A101, B102, C201\n");

            // --- Lehrer vordefiniert erstellen ---
            System.out.println("=== LEHRER ERSTELLEN ===");
            Lehrer l1 = new Lehrer("Anna", "Schmidt", r1);
            lehrerDao.create(l1);
            Lehrer l2 = new Lehrer("Max", "Meier", r2);
            lehrerDao.create(l2);
            System.out.println("Lehrer erstellt: Anna Schmidt, Max Meier\n");

            // --- Schüler interaktiv eingeben ---
            System.out.println("=== SCHÜLER EINGEBEN ===");
            List<Schueler> schuelerListe = new java.util.ArrayList<>();
            boolean moreSchueler = true;

            while (moreSchueler) {
                System.out.print("Gib den Vornamen des Schülers ein: ");
                String vorname = scanner.nextLine();

                System.out.print("Gib den Nachnamen des Schülers ein: ");
                String nachname = scanner.nextLine();

                System.out.print("Gib die Klasse ein (z.B. 3A): ");
                String klasse = scanner.nextLine();

                Schueler neuerSchueler = new Schueler(vorname, nachname, klasse, r1);
                schuelerDao.create(neuerSchueler);
                schuelerListe.add(neuerSchueler);
                System.out.println("Schüler " + vorname + " " + nachname + " hinzugefügt.\n");

                System.out.print("Möchtest du einen weiteren Schüler hinzufügen? (ja/nein): ");
                String antwort = scanner.nextLine().toLowerCase();
                if (!antwort.equals("ja")) {
                    moreSchueler = false;
                }
            }

            // --- Noten interaktiv eingeben ---
            System.out.println("\n=== NOTEN EINGEBEN ===");
            List<String> faecher = Arrays.asList("Mathe", "Deutsch", "Englisch", "Chemie", "Physik");
            List<Lehrer> lehrerListe = lehrerDao.queryForAll();

            for (Schueler schueler : schuelerListe) {
                System.out.println("\nNoten für " + schueler.getVorname() + " " + schueler.getNachname() + ":");

                for (String fach : faecher) {
                    System.out.print("  Gib die Note für " + fach + " ein (1-5): ");
                    int wert = Integer.parseInt(scanner.nextLine());

                    Lehrer zugewiesenerLehrer = lehrerListe.get(0); // Erste Lehrer als Standard
                    Note neueNote = new Note(fach, wert, schueler, zugewiesenerLehrer);
                    notenDao.create(neueNote);
                }
            }

            // --- Notendurchschnitt berechnen ---
            System.out.println("\n=== DURCHSCHNITTE BERECHNEN ===");
            for (Schueler schueler : schuelerListe) {
                String[] resultAvg = notenDao.queryBuilder()
                    .selectRaw("AVG(wert)")
                    .where().eq("schueler_id", schueler.getId())
                    .queryRawFirst();
                double avgValue = resultAvg != null ? Double.parseDouble(resultAvg[0]) : 0;
                
                schueler.setSchuelerDurchschnitt(avgValue);
                schuelerDao.update(schueler);
            }

            // --- Räume ändern ---
            System.out.println("\n=== RÄUME SUPLIEREN ===");
            System.out.print("Möchtest du einen Raum suplieren? (ja/nein): ");
            String raumAntwort = scanner.nextLine().toLowerCase();

            while (raumAntwort.equals("ja")) {
                System.out.print("Welchen Raum möchtest du ändern? (z.B. A101): ");
                String alterRaum = scanner.nextLine();

                Raum zuaendernderRaum = raumDao.queryBuilder()
                    .where().eq("name", alterRaum)
                    .queryForFirst();

                if (zuaendernderRaum != null) {
                    System.out.print("Gib den neuen Raumnamen ein: ");
                    String neuerRaumName = scanner.nextLine();

                    System.out.print("Gib die neue Kapazität ein: ");
                    int neueKapazitaet = Integer.parseInt(scanner.nextLine());

                    zuaendernderRaum.setName(neuerRaumName);
                    zuaendernderRaum.setKapazitaet(neueKapazitaet);
                    raumDao.update(zuaendernderRaum);
                    System.out.println("Raum erfolgreich aktualisiert.\n");
                } else {
                    System.out.println("Raum nicht gefunden.\n");
                }

                System.out.print("Möchtest du noch einen Raum ändern? (ja/nein): ");
                raumAntwort = scanner.nextLine().toLowerCase();
            }

            // --- Räume hinzufügen ---
            System.out.println("\n=== RÄUME HINZUFÜGEN ===");
            System.out.print("Möchtest du einen neuen Raum hinzufügen? (ja/nein): ");
            String raumHinzufuegen = scanner.nextLine().toLowerCase();

            while (raumHinzufuegen.equals("ja")) {
                System.out.print("Gib den Namen des neuen Raums ein (z.B. D301): ");
                String raumName = scanner.nextLine();

                System.out.print("Gib die Kapazität ein: ");
                int kapazitaet = Integer.parseInt(scanner.nextLine());

                Raum neuerRaum = new Raum(raumName, kapazitaet);
                raumDao.create(neuerRaum);
                System.out.println("Raum " + raumName + " erfolgreich hinzugefügt.\n");

                System.out.print("Möchtest du noch einen Raum hinzufügen? (ja/nein): ");
                raumHinzufuegen = scanner.nextLine().toLowerCase();
            }

            // --- Lehrer hinzufügen ---
            System.out.println("\n=== LEHRER HINZUFÜGEN ===");
            System.out.print("Möchtest du einen neuen Lehrer hinzufügen? (ja/nein): ");
            String lehrerHinzufuegen = scanner.nextLine().toLowerCase();

            while (lehrerHinzufuegen.equals("ja")) {
                System.out.print("Gib den Vornamen des Lehrers ein: ");
                String lehrerVorname = scanner.nextLine();

                System.out.print("Gib den Nachnamen des Lehrers ein: ");
                String lehrerNachname = scanner.nextLine();

                System.out.println("Verfügbare Räume:");
                List<Raum> verfuegbareRaeume = raumDao.queryForAll();
                int raumIndex = 0;
                for (Raum r : verfuegbareRaeume) {
                    System.out.println((raumIndex + 1) + ". " + r.getName() + " (Kapazität: " + r.getKapazitaet() + ")");
                    raumIndex++;
                }

                System.out.print("Wähle einen Raum (Nummer eingeben): ");
                int raumWahl = Integer.parseInt(scanner.nextLine()) - 1;

                if (raumWahl >= 0 && raumWahl < verfuegbareRaeume.size()) {
                    Raum ausgewaehlterRaum = verfuegbareRaeume.get(raumWahl);
                    Lehrer neuerLehrer = new Lehrer(lehrerVorname, lehrerNachname, ausgewaehlterRaum);
                    lehrerDao.create(neuerLehrer);
                    System.out.println("Lehrer " + lehrerVorname + " " + lehrerNachname + " erfolgreich hinzugefügt.\n");
                } else {
                    System.out.println("Ungültige Auswahl.\n");
                }

                System.out.print("Möchtest du noch einen Lehrer hinzufügen? (ja/nein): ");
                lehrerHinzufuegen = scanner.nextLine().toLowerCase();
            }

            // --- Lehrer löschen ---
            System.out.println("\n=== LEHRER LÖSCHEN ===");
            System.out.print("Möchtest du einen Lehrer löschen? (ja/nein): ");
            String lehrerLoeschen = scanner.nextLine().toLowerCase();

            while (lehrerLoeschen.equals("ja")) {
                System.out.println("Verfügbare Lehrer:");
                List<Lehrer> alleLehrerList = lehrerDao.queryForAll();
                int lehrerIndex = 0;
                for (Lehrer l : alleLehrerList) {
                    System.out.println((lehrerIndex + 1) + ". " + l.getVorname() + " " + l.getNachname());
                    lehrerIndex++;
                }

                System.out.print("Wähle einen Lehrer zum Löschen (Nummer eingeben): ");
                int lehrerWahl = Integer.parseInt(scanner.nextLine()) - 1;

                if (lehrerWahl >= 0 && lehrerWahl < alleLehrerList.size()) {
                    Lehrer zuLoeschendelLehrer = alleLehrerList.get(lehrerWahl);
                    lehrerDao.delete(zuLoeschendelLehrer);
                    System.out.println("Lehrer " + zuLoeschendelLehrer.getVorname() + " " + zuLoeschendelLehrer.getNachname() + " erfolgreich gelöscht.\n");
                } else {
                    System.out.println("Ungültige Auswahl.\n");
                }

                System.out.print("Möchtest du noch einen Lehrer löschen? (ja/nein): ");
                lehrerLoeschen = scanner.nextLine().toLowerCase();
            }

            // --- Ausgabe aller Daten ---
            System.out.println("\n=== AKTUELLE DATEN IN DER DATENBANK ===");
            schuelerListe = schuelerDao.queryForAll();
            for (Schueler s : schuelerListe) {
                schuelerDao.refresh(s);
                System.out.println("Schüler: " + s.getVorname() + " " + s.getNachname() + " (Klasse: " + s.getKlasse() + "), Durchschnitt: " + String.format("%.2f", s.getSchuelerDurchschnitt()));

                List<Note> schuelerNoten = notenDao.queryForEq("schueler_id", s.getId());
                for (Note n : schuelerNoten) {
                    notenDao.refresh(n);
                    System.out.println("  -> Note: " + n.getFach() + " (" + n.getWert() + ") am " + n.getDatum() + " eingetragen von Lehrer: " + n.getLehrer().getNachname());
                }
            }

            System.out.println("\n=== VERFÜGBARE RÄUME ===");
            List<Raum> alleRaeume = raumDao.queryForAll();
            for (Raum r : alleRaeume) {
                System.out.println("Raum: " + r.getName() + " (Kapazität: " + r.getKapazitaet() + ")");
            }

            scanner.close();

        } catch (SQLException e) {
            System.err.println("Datenbank-Fehler: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
