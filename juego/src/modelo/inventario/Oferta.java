package modelo.inventario;

public class Oferta {

    private Item item;
    private int precio;
    private boolean vendida;

    public Oferta(Item item, int precio) {
        if (item == null) {
            throw new IllegalArgumentException("La oferta debe tener un item");
        }

        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        this.item = item;
        this.precio = precio;
        this.vendida = false;
    }

    public boolean estaDisponible() {
        return !vendida;
    }

    public Item obtenerItem() {
        return item;
    }

    public int obtenerPrecio() {
        return precio;
    }

    public void marcarVendida() {
        vendida = true;
    }
}