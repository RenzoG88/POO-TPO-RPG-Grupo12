import modelo.combatientes.Ciberseguridad;
import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.inventario.Cafe;
import modelo.inventario.Herramienta;
import modelo.inventario.Inventario;
import modelo.inventario.Oferta;
import modelo.inventario.Snack;
import modelo.inventario.Tienda;
import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PruebaTiendaIntegrada {

    public static void main(String[] args) throws Exception {
        Partida partida = new Partida();
        Equipo equipo = partida.obtenerEquipo();
        Inventario inventario = equipo.obtenerInventario();
        Empleado primero = equipo.obtenerEmpleados().get(0);
        Empleado segundo = equipo.obtenerEmpleados().get(1);
        Tienda tienda = partida.obtenerTienda();
        Oferta ofertaTeclado = tienda.obtenerOfertas().get(2);

        verificar(tienda.obtenerOfertas().size() == 4
                        && inventario.obtenerCreditos() == 0 && inventario.obtenerItems().isEmpty(),
                "La tienda tiene ofertas y el inventario empieza vacio");
        verificar(!partida.comprar(ofertaTeclado) && ofertaTeclado.estaDisponible()
                        && inventario.obtenerItems().isEmpty(),
                "Sin creditos no hay compra ni cambios de stock");

        inventario.agregarCreditos(200);
        verificar(!partida.comprar(new Oferta(new Snack(), 0))
                        && inventario.obtenerCreditos() == 200,
                "Se rechaza una oferta ajena a la tienda");
        verificar(partida.comprar(ofertaTeclado)
                        && inventario.obtenerCreditos() == 150,
                "La compra descuenta el precio una sola vez");
        verificar(!partida.comprar(ofertaTeclado)
                        && inventario.obtenerCreditos() == 150,
                "No se puede volver a comprar una oferta vendida");

        Herramienta primerTeclado = (Herramienta) ofertaTeclado.obtenerItem();
        verificar(partida.equipar(primero, primerTeclado)
                        && primero.obtenerAtaqueTotal() == 28,
                "Se puede equipar el objeto comprado");

        // Renovacion invocada solo por esta prueba; abrir la pantalla no la ejecuta.
        tienda.renovarOfertas(1);
        Oferta ofertaNueva = tienda.obtenerOfertas().get(2);
        Herramienta segundoTeclado = (Herramienta) ofertaNueva.obtenerItem();
        verificar(primerTeclado != segundoTeclado && !partida.comprar(ofertaTeclado),
                "Renovar crea objetos nuevos y descarta las ofertas anteriores");
        verificar(partida.comprar(ofertaNueva) && partida.equipar(segundo, segundoTeclado)
                        && primero.obtenerHerramienta() == primerTeclado
                        && segundo.obtenerHerramienta() == segundoTeclado,
                "Dos compras distintas no comparten el mismo objeto equipado");

        List<Oferta> ofertas = tienda.obtenerOfertas();
        Snack snack = (Snack) ofertas.get(0).obtenerItem();
        Cafe cafe = (Cafe) ofertas.get(1).obtenerItem();
        verificar(partida.comprar(ofertas.get(0)) && partida.comprar(ofertas.get(1))
                        && inventario.obtenerCreditos() == 65,
                "Se pueden comprar snack y cafe");
        verificar(!partida.usarConsumible(snack, primero)
                        && !partida.usarConsumible(cafe, primero)
                        && inventario.contiene(snack) && inventario.contiene(cafe),
                "Con vida y energia completas los consumibles se conservan");

        primero.recibirDanio(40);
        primero.gastarEnergia(25);
        Ciberseguridad ajeno = new Ciberseguridad();
        ajeno.recibirDanio(40);
        verificar(!partida.usarConsumible(snack, ajeno) && inventario.contiene(snack),
                "No se puede usar un objeto sobre un empleado de otro equipo");
        verificar(partida.usarConsumible(snack, primero)
                        && primero.obtenerVida() == 150 && !inventario.contiene(snack),
                "Snack recupera 30 de vida y se retira del inventario");
        verificar(partida.usarConsumible(cafe, primero)
                        && primero.obtenerEnergia() == 55 && !inventario.contiene(cafe),
                "Cafe recupera 20 de energia y se retira del inventario");
        verificar(!partida.usarConsumible(snack, primero),
                "El mismo consumible no puede usarse dos veces");

        Empleado despedido = equipo.obtenerEmpleados().get(3);
        despedido.recibirDanio(1000);
        Snack reserva = new Snack();
        inventario.agregarItem(reserva);
        verificar(!partida.usarConsumible(reserva, despedido)
                        && despedido.obtenerVida() == 0 && inventario.contiene(reserva),
                "Un consumible no revive a un empleado despedido");

        Path archivo = Files.createTempFile("tienda-integrada-", ".rpg");
        try {
            PersistenciaPartida persistencia = new PersistenciaPartida();
            persistencia.guardar(partida, archivo.toString());
            Partida cargada = persistencia.cargar(archivo.toString());
            List<Oferta> cargadas = cargada.obtenerTienda().obtenerOfertas();
            verificar(!cargadas.get(0).estaDisponible() && !cargadas.get(1).estaDisponible()
                            && !cargadas.get(2).estaDisponible() && cargadas.get(3).estaDisponible(),
                    "Guardar y cargar conserva las ofertas vendidas");
            verificar(cargada.obtenerEquipo().obtenerInventario().obtenerCreditos() == 65
                            && !cargada.comprar(cargadas.get(2))
                            && cargada.obtenerEquipo().obtenerInventario().obtenerCreditos() == 65,
                    "Cargar no renueva la tienda ni permite recomprar");
            verificar(cargadas.get(2).obtenerItem()
                            == cargada.obtenerEquipo().obtenerEmpleados().get(1).obtenerHerramienta(),
                    "Se conserva la referencia al objeto vendido y equipado");
            Empleado cargado = cargada.obtenerEquipo().obtenerEmpleados().get(0);
            Snack reservaCargada = (Snack) cargada.obtenerEquipo().obtenerInventario()
                    .obtenerItems().get(0);
            verificar(cargada.usarConsumible(reservaCargada, cargado)
                            && cargado.obtenerVida() == 160,
                    "El consumible cargado funciona y respeta la vida maxima");
        } finally {
            Files.deleteIfExists(archivo);
        }

        System.out.println("Integracion de tienda, inventario y guardado: OK.");
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) {
            throw new IllegalStateException("FALLO: " + descripcion);
        }
        System.out.println("OK: " + descripcion);
    }
}
