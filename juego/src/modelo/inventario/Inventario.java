package modelo.inventario;

import java.io.Serializable;
import modelo.combatientes.Empleado;

import java.util.ArrayList;
import java.util.List;

public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;

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

        if (items.contains(item)) {
            throw new IllegalArgumentException("El mismo objeto ya esta en el inventario");
        }

        items.add(item);
    }

    public boolean retirarItem(Item item) {
        return items.remove(item);
    }

    public boolean usarConsumible(Consumible item, Empleado objetivo) {
        if (item == null || !contiene(item) || !item.puedeAplicarse(objetivo)) {
            return false;
        }

        item.aplicar(objetivo);
        return retirarItem(item);
    }

    public List<Item> obtenerItems() {
        return new ArrayList<>(items);
    }
}
