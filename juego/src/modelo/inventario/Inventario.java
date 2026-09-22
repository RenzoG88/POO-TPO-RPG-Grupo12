package modelo.inventario;

import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private int creditos;
    private List<Item> items;

    public Inventario() {
        this.creditos = 0;
        this.items = new ArrayList<>();
    }

    public int obtenerCreditos() {
        return creditos;
    }

    public void agregarCreditos(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La cantidad de creditos no puede ser negativa"
            );
        }

        creditos += cantidad;
    }

    public boolean gastarCreditos(int cantidad) {
        if (cantidad < 0 || cantidad > creditos) {
            return false;
        }

        creditos -= cantidad;
        return true;
    }

    public boolean contiene(Item item) {
        return items.contains(item);
    }

    public void agregarItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "El item no puede ser null"
            );
        }

        items.add(item);
    }

    public boolean retirarItem(Item item) {
        return items.remove(item);
    }

    // Pendiente del UML:
    // usarConsumible(item: Consumible, objetivo: Empleado): boolean
}
