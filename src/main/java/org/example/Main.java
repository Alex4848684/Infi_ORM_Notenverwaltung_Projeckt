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

            TableUtils.createTableIfNotExists(connectionSource, Schueler.class);
            TableUtils.createTableIfNotExists(connectionSource, Note.class);

            Dao<Schueler, Integer> schuelerDao = DaoManager.createDao(connectionSource, Schueler.class);
            Dao<Note, Integer> notenDao = DaoManager.createDao(connectionSource, Note.class);

            System.out.println("Lege neuen Schüler und Note an...");
            Schueler s1 = new Schueler("Max", "Mustermann", "3A");
            schuelerDao.create(s1);
            
            Note n1 = new Note("Mathe", 1, s1);
            notenDao.create(n1);

            System.out.println("\n--- Aktuelle Daten in der Datenbank ---");
            List<Schueler> alleSchueler = schuelerDao.queryForAll();
            for (Schueler s : alleSchueler) {
                System.out.println("Schüler: " + s.getVorname() + " " + s.getNachname() + " (Klasse: " + s.getKlasse() + ")");
                
                List<Note> schuelerNoten = notenDao.queryForEq("schueler_id", s.getId());
                for (Note n : schuelerNoten) {
                    System.out.println("  -> Note: " + n.getFach() + " (" + n.getWert() + ")");
                }
            }

        } catch (SQLException e) {
            System.err.println("Datenbank-Fehler: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
