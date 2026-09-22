import modelo.inventario.Herramienta;
import modelo.inventario.Proteccion;
import modelo.inventario.Oferta;
import modelo.inventario.Inventario;
import modelo.inventario.Tienda;
import java.util.ArrayList;
import java.util.List;

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

        Inventario inventario = new Inventario();
        inventario.agregarCreditos(100);

        System.out.println("Gastar 40: " + inventario.gastarCreditos(40));
        System.out.println("Saldo: " + inventario.obtenerCreditos());

        System.out.println("Gastar 100: " + inventario.gastarCreditos(100));
        System.out.println("Saldo: " + inventario.obtenerCreditos());

        inventario.agregarItem(teclado);
        System.out.println("Tiene teclado: " + inventario.contiene(teclado));

        System.out.println("Retirar teclado: " + inventario.retirarItem(teclado));
        System.out.println("Tiene teclado: " + inventario.contiene(teclado));

        Oferta ofertaMouse = new Oferta(
                new Herramienta("Mouse ergonómico", 5),
                30
        );

        List<Oferta> ofertasIniciales = new ArrayList<>();
        ofertasIniciales.add(ofertaMouse);

        Tienda tienda = new Tienda(ofertasIniciales);

        Inventario inventarioCompra = new Inventario();
        inventarioCompra.agregarCreditos(50);

        System.out.println("Compra: " + tienda.comprar(ofertaMouse, inventarioCompra));
        System.out.println("Saldo: " + inventarioCompra.obtenerCreditos());
        System.out.println("Tiene mouse: " + inventarioCompra.contiene(ofertaMouse.obtenerItem()));

        System.out.println("Comprar otra vez: " + tienda.comprar(ofertaMouse, inventarioCompra));
        System.out.println("Saldo final: " + inventarioCompra.obtenerCreditos());
    }

}