package modelo.inventario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tienda implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Oferta> ofertas;

    public Tienda() {
        ofertas = new ArrayList<>();
    }

    public Tienda(List<Oferta> ofertasIniciales) {
        this();

        if (ofertasIniciales == null) {
            throw new IllegalArgumentException("La lista de ofertas no puede ser null");
        }

        for (Oferta oferta : ofertasIniciales) {
            agregarOferta(oferta);
        }
    }

    public List<Oferta> obtenerOfertas() {
        return new ArrayList<>(ofertas);
    }

    public void agregarOferta(Oferta oferta) {
        if (oferta == null) {
            throw new IllegalArgumentException("La oferta no puede ser null");
        }

        for (Oferta existente : ofertas) {
            if (existente == oferta || existente.obtenerItem() == oferta.obtenerItem()) {
                throw new IllegalArgumentException("Cada oferta debe tener su propio objeto");
            }
        }

        ofertas.add(oferta);
    }

    public void renovarOfertas(int ciclo) {
        if (ciclo < 1) {
            throw new IllegalArgumentException("El ciclo debe ser positivo");
        }

        // Catalogo inicial de balance. Cada preparacion recibe objetos nuevos.
        int progreso = ciclo - 1;
        List<Oferta> nuevas = new ArrayList<>();
        nuevas.add(new Oferta(new Snack(), 20));
        nuevas.add(new Oferta(new Cafe(), 15));
        nuevas.add(new Oferta(new Herramienta("Teclado mecanico", 10 + 2 * progreso),
                50 + 10 * progreso));
        nuevas.add(new Oferta(new Proteccion("Auriculares aislantes", 5 + progreso),
                40 + 8 * progreso));
        ofertas = nuevas;
    }

    public boolean comprar(Oferta oferta, Inventario inventario) {
        if (oferta == null || inventario == null
                || !ofertas.contains(oferta) || !oferta.estaDisponible()) {
            return false;
        }

        if (inventario.contiene(oferta.obtenerItem())) {
            return false;
        }

        if (!inventario.gastarCreditos(oferta.obtenerPrecio())) {
            return false;
        }

        inventario.agregarItem(oferta.obtenerItem());
        oferta.marcarVendida();
        return true;
    }
}
