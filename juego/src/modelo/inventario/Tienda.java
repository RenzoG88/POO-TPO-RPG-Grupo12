package modelo.inventario;

import java.util.ArrayList;
import java.util.List;

public class Tienda {

    private List<Oferta> ofertas;

    public Tienda(List<Oferta> ofertasIniciales) {
        if (ofertasIniciales == null) {
            throw new IllegalArgumentException(
                    "La lista de ofertas no puede ser null"
            );
        }

        this.ofertas = new ArrayList<>(ofertasIniciales);
    }

    public boolean comprar(Oferta oferta, Inventario inventario) {
        if (oferta == null || inventario == null) {
            return false;
        }

        if (!ofertas.contains(oferta)) {
            return false;
        }

        if (!oferta.estaDisponible()) {
            return false;
        }

        boolean pagoRealizado =
                inventario.gastarCreditos(oferta.obtenerPrecio());

        if (!pagoRealizado) {
            return false;
        }

        inventario.agregarItem(oferta.obtenerItem());
        oferta.marcarVendida();

        return true;
    }

    // Pendiente del UML:
    // renovarOfertas(ciclo: int): void
}
