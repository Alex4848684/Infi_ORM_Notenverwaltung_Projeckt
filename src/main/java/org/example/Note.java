package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date; // Import Date class

@DatabaseTable(tableName = "noten")
public class Note {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String fach;

    @DatabaseField(canBeNull = false)
    private int wert;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "schueler_id")
    private Schueler schueler;

    @DatabaseField(canBeNull = false)
    private Date datum; // New field for the date

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "lehrer_id")
    private Lehrer lehrer; // New field for the teacher

    public Note() {}

    public Note(String fach, int wert, Schueler schueler, Lehrer lehrer) {
        this.fach = fach;
        this.wert = wert;
        this.schueler = schueler;
        this.lehrer = lehrer;
        this.datum = new Date(); // Set current date when a note is created
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFach() { return fach; }
    public void setFach(String fach) { this.fach = fach; }

    public int getWert() { return wert; }
    public void setWert(int wert) { this.wert = wert; }

    public Schueler getSchueler() { return schueler; }
    public void setSchueler(Schueler schueler) { this.schueler = schueler; }

    public Date getDatum() { return datum; }
    public void setDatum(Date datum) { this.datum = datum; }

    public Lehrer getLehrer() { return lehrer; }
    public void setLehrer(Lehrer lehrer) { this.lehrer = lehrer; }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", fach='" + fach + '\'' +
                ", wert=" + wert +
                ", datum=" + datum +
                ", schueler=" + (schueler != null ? schueler.getNachname() : "null") +
                ", lehrer=" + (lehrer != null ? lehrer.getNachname() : "null") +
                '}';
    }
}
