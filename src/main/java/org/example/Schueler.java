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

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "raum_name", foreignColumnName = "name")
    private Raum raum;

    @DatabaseField
    private double schuelerDurchschnitt;

    public Schueler() {}

    public Schueler(String vorname, String nachname, String klasse, Raum raum) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.klasse = klasse;
        this.raum = raum;
        this.schuelerDurchschnitt = 0.0; // Initialize average
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVorname() { return vorname; }
    public void setVorname(String vorname) { this.vorname = vorname; }

    public String getNachname() { return nachname; }
    public void setNachname(String nachname) { this.nachname = nachname; }

    public String getKlasse() { return klasse; }
    public void setKlasse(String klasse) { this.klasse = klasse; }

    public Raum getRaum() { return raum; }
    public void setRaum(Raum raum) { this.raum = raum; }

    public double getSchuelerDurchschnitt() { return schuelerDurchschnitt; }
    public void setSchuelerDurchschnitt(double schuelerDurchschnitt) { this.schuelerDurchschnitt = schuelerDurchschnitt; }

    @Override
    public String toString() {
        return "Schueler{" +
                "id=" + id +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", klasse='" + klasse + '\'' +
                ", raum=" + (raum != null ? raum.getName() : "null") +
                ", schuelerDurchschnitt=" + schuelerDurchschnitt +
                '}';
    }
}
