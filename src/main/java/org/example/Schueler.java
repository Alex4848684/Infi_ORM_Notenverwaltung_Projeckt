package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "schueler")
public class Schueler {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String vorname;

    @DatabaseField(canBeNull = false)
    private String nachname;

    @DatabaseField
    private String klasse;

    public Schueler() {}

    public Schueler(String vorname, String nachname, String klasse) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.klasse = klasse;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVorname() { return vorname; }
    public void setVorname(String vorname) { this.vorname = vorname; }

    public String getNachname() { return nachname; }
    public void setNachname(String nachname) { this.nachname = nachname; }

    public String getKlasse() { return klasse; }
    public void setKlasse(String klasse) { this.klasse = klasse; }

    @Override
    public String toString() {
        return "Schueler{" +
                "id=" + id +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", klasse='" + klasse + '\'' +
                '}';
    }
}
