package modelo.inventario;

import java.io.Serializable;

public abstract class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;

    public Item(String nombre) {
        this.nombre = nombre;
    }

    public String obtenerNombre() {
        return nombre;
    }
}
