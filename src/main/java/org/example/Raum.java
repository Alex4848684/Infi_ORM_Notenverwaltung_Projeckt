package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "raeume")
public class Raum {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    @DatabaseField
    private int kapazitaet; // Optional: Kapazität des Raumes

    public Raum() {
        // ORMLite needs a no-arg constructor
    }

    public Raum(String name, int kapazitaet) {
        this.name = name;
        this.kapazitaet = kapazitaet;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKapazitaet() {
        return kapazitaet;
    }

    public void setKapazitaet(int kapazitaet) {
        this.kapazitaet = kapazitaet;
    }

    @Override
    public String toString() {
        return "Raum{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", kapazitaet=" + kapazitaet +
               '}';
    }
}
