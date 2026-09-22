import modelo.inventario.Herramienta;
import modelo.inventario.Proteccion;
import modelo.inventario.Oferta;

public class Main {

    public static void main(String[] args) {
        Herramienta teclado = new Herramienta("Teclado mecánico", 10);

        System.out.println("Herramienta: " + teclado.obtenerNombre());
        System.out.println("Bonus de ataque: " + teclado.obtenerBonusAtaque());

        Proteccion auriculares = new Proteccion("Auriculares aislantes", 5);

        System.out.println("Protección: " + auriculares.obtenerNombre());
        System.out.println("Bonus de defensa: " + auriculares.obtenerBonusDefensa());

        Oferta ofertaTeclado = new Oferta(teclado, 50);

        System.out.println("Oferta: " + ofertaTeclado.obtenerItem().obtenerNombre());
        System.out.println("Precio: " + ofertaTeclado.obtenerPrecio());
        System.out.println("Disponible: " + ofertaTeclado.estaDisponible());

        ofertaTeclado.marcarVendida();

        System.out.println("Disponible después: " + ofertaTeclado.estaDisponible());
    }

}