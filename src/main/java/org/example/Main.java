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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Treiber nicht gefunden!");
        }

        Logger.getLogger("com.j256.ormlite").setLevel(Level.WARNING);

        String databaseUrl = "jdbc:mysql://localhost:3306/schulverwaltung?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "RySj3b481";

        try (ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, username, password)) {

            // Create tables only if they don't exist (no data deletion)
            TableUtils.createTableIfNotExists(connectionSource, Raum.class);
            TableUtils.createTableIfNotExists(connectionSource, Lehrer.class);
            TableUtils.createTableIfNotExists(connectionSource, Schueler.class);
            TableUtils.createTableIfNotExists(connectionSource, Note.class);

            Dao<Schueler, Integer> schuelerDao = DaoManager.createDao(connectionSource, Schueler.class);
            Dao<Note, Integer> notenDao = DaoManager.createDao(connectionSource, Note.class);
            Dao<Lehrer, Integer> lehrerDao = DaoManager.createDao(connectionSource, Lehrer.class);
            Dao<Raum, Integer> raumDao = DaoManager.createDao(connectionSource, Raum.class);

            Scanner scanner = new Scanner(System.in);

            System.out.println("=== RÄUME INITIALISIEREN ===");
            Raum r1 = raumDao.queryBuilder().where().eq("name", "A101").queryForFirst();
            if (r1 == null) {
                r1 = new Raum("A101", 30);
                raumDao.create(r1);
                System.out.println("Raum A101 hinzugefügt.");
            } else {
                System.out.println("Raum A101 existiert bereits.");
            }

            Raum r2 = raumDao.queryBuilder().where().eq("name", "B102").queryForFirst();
            if (r2 == null) {
                r2 = new Raum("B102", 25);
                raumDao.create(r2);
                System.out.println("Raum B102 hinzugefügt.");
            } else {
                System.out.println("Raum B102 existiert bereits.");
            }

            Raum r3 = raumDao.queryBuilder().where().eq("name", "C201").queryForFirst();
            if (r3 == null) {
                r3 = new Raum("C201", 20);
                raumDao.create(r3);
                System.out.println("Raum C201 hinzugefügt.");
            } else {
                System.out.println("Raum C201 existiert bereits.");
            }
            System.out.println();

            System.out.println("=== LEHRER INITIALISIEREN ===");
            Lehrer l1 = lehrerDao.queryBuilder().where().eq("vorname", "Anna").and().eq("nachname", "Schmidt").queryForFirst();
            if (l1 == null) {
                l1 = new Lehrer("Anna", "Schmidt", r1);
                lehrerDao.create(l1);
                System.out.println("Lehrer Anna Schmidt hinzugefügt.");
            } else {
                System.out.println("Lehrer Anna Schmidt existiert bereits.");
            }

            Lehrer l2 = lehrerDao.queryBuilder().where().eq("vorname", "Max").and().eq("nachname", "Meier").queryForFirst();
            if (l2 == null) {
                l2 = new Lehrer("Max", "Meier", r2);
                lehrerDao.create(l2);
                System.out.println("Lehrer Max Meier hinzugefügt.");
            } else {
                System.out.println("Lehrer Max Meier existiert bereits.");
            }
            System.out.println();


            System.out.println("=== SCHÜLER EINGEBEN ===");
            List<Schueler> schuelerListe = new java.util.ArrayList<>();
            System.out.print("Möchtest du Schüler hinzufügen? (j/n): ");
            String schuelerHinzuHinzufuegen = scanner.nextLine().toLowerCase();

            if (schuelerHinzuHinzufuegen.equals("j")) {
                boolean moreSchueler = true;

                while (moreSchueler) {
                    System.out.print("Gib den Vornamen des Schülers ein: ");
                    String vorname = scanner.nextLine();

                    System.out.print("Gib den Nachnamen des Schülers ein: ");
                    String nachname = scanner.nextLine();

                    System.out.print("Gib die Klasse ein (z.B. 3A): ");
                    String klasse = scanner.nextLine();


                    Schueler existierenderSchueler = schuelerDao.queryBuilder()
                        .where().eq("vorname", vorname).and().eq("nachname", nachname).and().eq("klasse", klasse)
                        .queryForFirst();

                    if (existierenderSchueler != null) {
                        System.out.println("Schüler " + vorname + " " + nachname + " existiert bereits. Wird ignoriert.\n");
                        schuelerListe.add(existierenderSchueler);
                    } else {
                        Schueler neuerSchueler = new Schueler(vorname, nachname, klasse, r1);
                        schuelerDao.create(neuerSchueler);
                        schuelerListe.add(neuerSchueler);
                        System.out.println("Schüler " + vorname + " " + nachname + " hinzugefügt.\n");
                    }

                    System.out.print("Möchtest du einen weiteren Schüler hinzufügen? (j/n): ");
                    String antwort = scanner.nextLine().toLowerCase();
                    if (!antwort.equals("j")) {
                        moreSchueler = false;
                    }
                }
            }


            System.out.println("\n=== NOTEN EINGEBEN ===");
            if (!schuelerListe.isEmpty()) {
                List<String> faecher = Arrays.asList("Mathe", "Deutsch", "Englisch", "Chemie", "Physik");
                List<Lehrer> lehrerListe = lehrerDao.queryForAll();

                for (Schueler schueler : schuelerListe) {
                    System.out.println("\nNoten für " + schueler.getVorname() + " " + schueler.getNachname() + ":");

                    for (String fach : faecher) {
                        boolean gueltigeNote = false;
                        while (!gueltigeNote) {
                            try {
                                System.out.print("  Gib die Note für " + fach + " ein (1-5): ");
                                int wert = Integer.parseInt(scanner.nextLine());

                                if (wert >= 1 && wert <= 5) {
                                    Lehrer zugewiesenerLehrer = lehrerListe.get(0);
                                    Note neueNote = new Note(fach, wert, schueler, zugewiesenerLehrer);
                                    notenDao.create(neueNote);
                                    gueltigeNote = true;
                                } else {
                                    System.out.println("  Ungültig! Die Note muss zwischen 1 und 5 liegen.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("  Ungültige Eingabe! Bitte gib eine Zahl zwischen 1 und 5 ein.");
                            }
                        }
                    }
                }
            } else {
                System.out.println("Keine Schüler vorhanden. Überspringe Noten-Eingabe.");
            }

            System.out.println("\n=== DURCHSCHNITTE BERECHNEN ===");
            if (!schuelerListe.isEmpty()) {
                for (Schueler schueler : schuelerListe) {
                    String[] resultAvg = notenDao.queryBuilder()
                        .selectRaw("AVG(wert)")
                        .where().eq("schueler_id", schueler.getId())
                        .queryRawFirst();
                    double avgValue = resultAvg != null ? Double.parseDouble(resultAvg[0]) : 0;

                    schueler.setSchuelerDurchschnitt(avgValue);
                    schuelerDao.update(schueler);
                }
            } else {
                System.out.println("Keine Schüler vorhanden. Überspringe Durchschnittsberechnung.");
            }

            // --- Räume ändern ---
            System.out.println("\n=== RÄUME SUPLIEREN ===");
            System.out.print("Möchtest du einen Raum suplieren? (j/n): ");
            String raumAntwort = scanner.nextLine().toLowerCase();

            while (raumAntwort.equals("j")) {
                System.out.println("\nVerfügbare Räume:");
                List<Raum> allRaumeForSupl = raumDao.queryForAll();
                List<Lehrer> allLehrerForSupl = lehrerDao.queryForAll();

                for (int i = 0; i < allRaumeForSupl.size(); i++) {
                    Raum r = allRaumeForSupl.get(i);
                    StringBuilder teacherNames = new StringBuilder();
                    for (Lehrer l : allLehrerForSupl) {
                        if (l.getRaum() != null && l.getRaum().getName().equals(r.getName())) {
                            if (!teacherNames.isEmpty()) {
                                teacherNames.append(", ");
                            }
                            teacherNames.append(l.getVorname()).append(" ").append(l.getNachname());
                        }
                    }
                    String teachers = !teacherNames.isEmpty() ? teacherNames.toString() : "keine";
                    System.out.println((i + 1) + ". " + r.getName() + " (Kapazität: " + r.getKapazitaet() + ", Lehrer: " + teachers + ")");
                }

                System.out.print("\nWähle einen Raum zum Suplieren (Nummer eingeben): ");
                try {
                    int raumWahl = Integer.parseInt(scanner.nextLine()) - 1;

                    if (raumWahl >= 0 && raumWahl < allRaumeForSupl.size()) {
                        Raum zuaendernderRaum = allRaumeForSupl.get(raumWahl);
                        System.out.print("Gib den neuen Raumnamen ein: ");
                        String neuerRaumName = scanner.nextLine();

                        Raum existingWithNewName = raumDao.queryBuilder()
                            .where().eq("name", neuerRaumName)
                            .queryForFirst();

                        if (existingWithNewName != null && existingWithNewName.getId() != zuaendernderRaum.getId()) {
                            System.out.println("Fehler: Ein Raum mit dem Namen '" + neuerRaumName + "' existiert bereits.\n");
                        } else {
                            try {
                                System.out.print("Gib die neue Kapazität ein: ");
                                int neueKapazitaet = Integer.parseInt(scanner.nextLine());

                                zuaendernderRaum.setName(neuerRaumName);
                                zuaendernderRaum.setKapazitaet(neueKapazitaet);
                                raumDao.update(zuaendernderRaum);
                                System.out.println("Raum erfolgreich in der Datenbank aktualisiert.\n");
                            } catch (NumberFormatException e) {
                                System.out.println("Ungültige Eingabe! Die Kapazität muss eine Zahl sein.\n");
                            }
                        }
                    } else {
                        System.out.println("Ungültige Auswahl. Bitte gib eine Nummer zwischen 1 und " + allRaumeForSupl.size() + " ein.\n");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige Eingabe! Bitte gib eine Raum ID ein.\n");
                }

                System.out.print("Möchtest du noch einen Raum suplieren? (j/n): ");
                raumAntwort = scanner.nextLine().toLowerCase();
            }

            System.out.println("\n=== RÄUME HINZUFÜGEN ===");
            System.out.print("Möchtest du einen neuen Raum hinzufügen? (j/n): ");
            String raumHinzufuegen = scanner.nextLine().toLowerCase();

            while (raumHinzufuegen.equals("j")) {
                System.out.print("Gib den Namen des neuen Raums ein (z.B. D301): ");
                String raumName = scanner.nextLine();

                Raum existierenderRaum = raumDao.queryBuilder().where().eq("name", raumName).queryForFirst();
                if (existierenderRaum != null) {
                    System.out.println("Raum " + raumName + " existiert bereits. Wird ignoriert.\n");
                } else {
                    try {
                        System.out.print("Gib die Kapazität ein: ");
                        int kapazitaet = Integer.parseInt(scanner.nextLine());

                        Raum neuerRaum = new Raum(raumName, kapazitaet);
                        raumDao.create(neuerRaum);
                        System.out.println("Raum " + raumName + " erfolgreich hinzugefügt.\n");
                    } catch (NumberFormatException e) {
                        System.out.println("Ungültige Eingabe! Die Kapazität muss eine Zahl sein.\n");
                    }
                }

                System.out.print("Möchtest du noch einen Raum hinzufügen? (j/n): ");
                raumHinzufuegen = scanner.nextLine().toLowerCase();
            }

            System.out.println("\n=== LEHRER HINZUFÜGEN ===");
            System.out.print("Möchtest du einen neuen Lehrer hinzufügen? (j/n): ");
            String lehrerHinzufuegen = scanner.nextLine().toLowerCase();

            while (lehrerHinzufuegen.equals("j")) {
                System.out.print("Gib den Vornamen des Lehrers ein: ");
                String lehrerVorname = scanner.nextLine();

                System.out.print("Gib den Nachnamen des Lehrers ein: ");
                String lehrerNachname = scanner.nextLine();

                Lehrer existierenderLehrer = lehrerDao.queryBuilder()
                    .where().eq("vorname", lehrerVorname).and().eq("nachname", lehrerNachname)
                    .queryForFirst();

                if (existierenderLehrer != null) {
                    System.out.println("Lehrer " + lehrerVorname + " " + lehrerNachname + " existiert bereits. Wird ignoriert.\n");
                } else {
                    System.out.println("Verfügbare Räume:");
                    List<Raum> verfuegbareRaeume = raumDao.queryForAll();
                    int raumIndex = 0;
                    for (Raum r : verfuegbareRaeume) {
                        System.out.println((raumIndex + 1) + ". " + r.getName() + " (Kapazität: " + r.getKapazitaet() + ")");
                        raumIndex++;
                    }

                    System.out.print("Wähle einen Raum (Nummer eingeben): ");
                    try {
                        int raumWahl = Integer.parseInt(scanner.nextLine()) - 1;

                        if (raumWahl >= 0 && raumWahl < verfuegbareRaeume.size()) {
                            Raum ausgewaehlterRaum = verfuegbareRaeume.get(raumWahl);
                            Lehrer neuerLehrer = new Lehrer(lehrerVorname, lehrerNachname, ausgewaehlterRaum);
                            lehrerDao.create(neuerLehrer);
                            System.out.println("Lehrer " + lehrerVorname + " " + lehrerNachname + " erfolgreich hinzugefügt.\n");
                        } else {
                            System.out.println("Ungültige Auswahl. Bitte gib eine Nummer zwischen 1 und " + verfuegbareRaeume.size() + " ein.\n");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ungültige Eingabe! Bitte gib eine Nummer ein, nicht den Raumnamen.\n");
                    }
                }

                System.out.print("Möchtest du noch einen Lehrer hinzufügen? (j/n): ");
                lehrerHinzufuegen = scanner.nextLine().toLowerCase();
            }

            System.out.println("\n=== LEHRER LÖSCHEN ===");
            System.out.print("Möchtest du einen Lehrer löschen? (j/n): ");
            String lehrerLoeschen = scanner.nextLine().toLowerCase();

            while (lehrerLoeschen.equals("j")) {
                System.out.println("Verfügbare Lehrer:");
                List<Lehrer> alleLehrerList = lehrerDao.queryForAll();
                int lehrerIndex = 0;
                for (Lehrer l : alleLehrerList) {
                    System.out.println((lehrerIndex + 1) + ". " + l.getVorname() + " " + l.getNachname());
                    lehrerIndex++;
                }

                System.out.print("Wähle einen Lehrer zum Löschen (Nummer eingeben): ");
                try {
                    int lehrerWahl = Integer.parseInt(scanner.nextLine()) - 1;

                    if (lehrerWahl >= 0 && lehrerWahl < alleLehrerList.size()) {
                        Lehrer zuLoeschendelLehrer = alleLehrerList.get(lehrerWahl);
                        lehrerDao.delete(zuLoeschendelLehrer);
                        System.out.println("Lehrer " + zuLoeschendelLehrer.getVorname() + " " + zuLoeschendelLehrer.getNachname() + " erfolgreich gelöscht.\n");
                    } else {
                        System.out.println("Ungültige Auswahl. Bitte gib eine Nummer zwischen 1 und " + alleLehrerList.size() + " ein.\n");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige Eingabe! Bitte gib eine Nummer ein.\n");
                }

                System.out.print("Möchtest du noch einen Lehrer löschen? (j/n): ");
                lehrerLoeschen = scanner.nextLine().toLowerCase();
            }

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

            System.out.print("\nMöchtest du die Daten als CSV exportieren? (j/n): ");
            String exportAntwort = scanner.nextLine().toLowerCase();
            if (exportAntwort.equals("j")) {
                try {
                    CSV_Export.exportAll(connectionSource, "exports");
                    System.out.println("CSV-Export erfolgreich: Ordner 'exports' erstellt/aktualisiert.");
                } catch (SQLException | IOException e) {
                    System.err.println("Fehler beim CSV-Export: " + e.getMessage());
                }
            }

            scanner.close();

        } catch (SQLException e) {
            System.err.println("Datenbank-Fehler: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
