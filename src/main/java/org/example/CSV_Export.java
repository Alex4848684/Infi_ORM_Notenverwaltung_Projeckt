package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utility class to export database tables to CSV files.
 * Usage: CSV_Export.exportAll(connectionSource, "output/directory");
 */
public class CSV_Export {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat FILE_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void exportAll(ConnectionSource connectionSource, String outDir) throws SQLException, IOException {
        Dao<Raum, Integer> raumDao = DaoManager.createDao(connectionSource, Raum.class);
        Dao<Lehrer, Integer> lehrerDao = DaoManager.createDao(connectionSource, Lehrer.class);
        Dao<Schueler, Integer> schuelerDao = DaoManager.createDao(connectionSource, Schueler.class);
        Dao<Note, Integer> notenDao = DaoManager.createDao(connectionSource, Note.class);

        File dir = new File(outDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Konnte Ausgabeordner nicht erstellen: " + outDir);
            }
        }

        exportRaeume(raumDao, new File(dir, "raeume.csv"));
        exportLehrer(lehrerDao, new File(dir, "lehrer.csv"));
        exportSchueler(schuelerDao, new File(dir, "schueler.csv"));
        exportNoten(notenDao, new File(dir, "noten.csv"));
    }

    private static void exportRaeume(Dao<Raum, Integer> dao, File outFile) throws SQLException, IOException {
        List<Raum> list = dao.queryForAll();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(outFile))) {
            w.write("id,name,kapazitaet");
            w.newLine();
            for (Raum r : list) {
                w.write(r.getId() + "," + escapeCsv(r.getName()) + "," + r.getKapazitaet());
                w.newLine();
            }
        }
    }

    private static void exportLehrer(Dao<Lehrer, Integer> dao, File outFile) throws SQLException, IOException {
        List<Lehrer> list = dao.queryForAll();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(outFile))) {
            w.write("id,vorname,nachname,raum_name");
            w.newLine();
            for (Lehrer l : list) {
                String raumName = (l.getRaum() != null) ? l.getRaum().getName() : "";
                w.write(l.getId() + "," + escapeCsv(l.getVorname()) + "," + escapeCsv(l.getNachname()) + "," + escapeCsv(raumName));
                w.newLine();
            }
        }
    }

    private static void exportSchueler(Dao<Schueler, Integer> dao, File outFile) throws SQLException, IOException {
        List<Schueler> list = dao.queryForAll();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(outFile))) {
            w.write("id,vorname,nachname,klasse,raum_name,durchschnitt");
            w.newLine();
            for (Schueler s : list) {
                String raumName = (s.getRaum() != null) ? s.getRaum().getName() : "";
                w.write(s.getId() + "," + escapeCsv(s.getVorname()) + "," + escapeCsv(s.getNachname()) + ","
                        + escapeCsv(s.getKlasse()) + "," + escapeCsv(raumName) + "," + s.getSchuelerDurchschnitt());
                w.newLine();
            }
        }
    }

    private static void exportNoten(Dao<Note, Integer> dao, File outFile) throws SQLException, IOException {
        List<Note> list = dao.queryForAll();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(outFile))) {
            w.write("id,fach,wert,schueler_id,schueler_name,lehrer_id,lehrer_name,datum");
            w.newLine();
            for (Note n : list) {
                Schueler s = n.getSchueler();
                Lehrer l = n.getLehrer();
                String schuelerName = (s != null) ? (s.getVorname() + " " + s.getNachname()) : "";
                String lehrerName = (l != null) ? (l.getVorname() + " " + l.getNachname()) : "";
                String datum = (n.getDatum() != null) ? DATE_FORMAT.format(n.getDatum()) : "";

                w.write(n.getId() + "," + escapeCsv(n.getFach()) + "," + n.getWert() + ","
                        + (s != null ? s.getId() : "") + "," + escapeCsv(schuelerName) + ","
                        + (l != null ? l.getId() : "") + "," + escapeCsv(lehrerName) + "," + escapeCsv(datum));
                w.newLine();
            }
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        boolean needsQuotes = value.contains(",") || value.contains("\n") || value.contains("\r") || value.contains("\"");
        String escaped = value.replace("\"", "\"\"");
        if (needsQuotes) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}

