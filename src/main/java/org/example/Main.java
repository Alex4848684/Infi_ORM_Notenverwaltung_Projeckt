package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

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
            TableUtils.dropTable(connectionSource, Note.class, true);
            TableUtils.createTable(connectionSource, Note.class);

            Dao<Schueler, Integer> schuelerDao = DaoManager.createDao(connectionSource, Schueler.class);
            Dao<Note, Integer> notenDao = DaoManager.createDao(connectionSource, Note.class);
            Dao<Lehrer, Integer> lehrerDao = DaoManager.createDao(connectionSource, Lehrer.class); // Create Lehrer DAO

            System.out.println("Lege neuen Schüler, Lehrer und Note an...");
            Schueler s1 = new Schueler("Max", "Mustermann", "3A");
            schuelerDao.create(s1);
            
            Lehrer l1 = new Lehrer("Anna", "Schmidt"); // Create a new teacher
            lehrerDao.create(l1);

            Note n1 = new Note("Mathe", 1, s1, l1); // Pass the teacher to the Note constructor
            notenDao.create(n1);

            System.out.println("\n--- Aktuelle Daten in der Datenbank ---");
            List<Schueler> alleSchueler = schuelerDao.queryForAll();
            for (Schueler s : alleSchueler) {
                System.out.println("Schüler: " + s.getVorname() + " " + s.getNachname() + " (Klasse: " + s.getKlasse() + ")");
                
                List<Note> schuelerNoten = notenDao.queryForEq("schueler_id", s.getId());
                for (Note n : schuelerNoten) {
                    System.out.println("  -> Note: " + n.getFach() + " (" + n.getWert() + ") am " + n.getDatum() + " eingetragen von " + n.getLehrer().getNachname());
                }
            }

        } catch (SQLException e) {
            System.err.println("Datenbank-Fehler: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
