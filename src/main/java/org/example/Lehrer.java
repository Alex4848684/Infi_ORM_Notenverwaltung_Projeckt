package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "lehrer")
public class Lehrer {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String vorname;

    @DatabaseField(canBeNull = false)
    private String nachname;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "raum_name", foreignColumnName = "name")
    private Raum raum;
    public Lehrer() {
    }

    public Lehrer(String vorname, String nachname, Raum raum) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.raum = raum;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(Raum raum) {
        this.raum = raum;
    }

    @Override
    public String toString() {
        return "Lehrer{" +
               "id=" + id +
               ", vorname='" + vorname + '\'' +
               ", nachname='" + nachname + '\'' +
               ", raum=" + (raum != null ? raum.getName() : "null") +
               '}';
    }
}
