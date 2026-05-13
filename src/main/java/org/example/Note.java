package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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

    public Note() {}

    public Note(String fach, int wert, Schueler schueler) {
        this.fach = fach;
        this.wert = wert;
        this.schueler = schueler;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFach() { return fach; }
    public void setFach(String fach) { this.fach = fach; }

    public int getWert() { return wert; }
    public void setWert(int wert) { this.wert = wert; }

    public Schueler getSchueler() { return schueler; }
    public void setSchueler(Schueler schueler) { this.schueler = schueler; }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", fach='" + fach + '\'' +
                ", wert=" + wert +
                ", schueler=" + (schueler != null ? schueler.getNachname() : "null") +
                '}';
    }
}
