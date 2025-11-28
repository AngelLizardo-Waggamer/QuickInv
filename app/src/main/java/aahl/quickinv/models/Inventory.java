package aahl.quickinv.models;

import java.io.Serializable;

/**
 * Modelo que representa un Inventario en la base de datos.
 */
public class Inventory implements Serializable {

    private long id;
    private String name;

    // Constructor vacío
    public Inventory() {
    }

    // Constructor completo
    public Inventory(long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
