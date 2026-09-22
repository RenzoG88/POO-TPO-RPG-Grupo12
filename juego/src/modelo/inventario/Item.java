package modelo.inventario;

public abstract class Item {

    private String nombre;

    public Item(String nombre) {
        this.nombre = nombre;
    }

    public String obtenerNombre() {
        return nombre;
    }
}
