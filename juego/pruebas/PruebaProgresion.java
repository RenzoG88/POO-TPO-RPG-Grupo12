import modelo.combate.Batalla;
import modelo.combatientes.Empleado;
import modelo.combatientes.Enemigo;
import modelo.combatientes.JefeEmpresa;
import modelo.estadostemporales.Burnout;
import modelo.inventario.Herramienta;
import modelo.inventario.Oferta;
import modelo.inventario.Snack;
import modelo.partida.EtapaPartida;
import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;
import modelo.partida.TipoMejora;
import java.nio.file.Files;
import java.nio.file.Path;

public class PruebaProgresion {
    public static void main(String[] args) throws Exception {
        Partida partida = new Partida();
        Empleado primero = partida.obtenerEquipo().obtenerEmpleados().get(0);
        Empleado despedido = partida.obtenerEquipo().obtenerEmpleados().get(3);
        despedido.recibirDanio(1000);
        primero.recibirDanio(30);
        primero.gastarEnergia(20);
        int vidaInicial = primero.obtenerVida();
        int energiaInicial = primero.obtenerEnergia();
        partida.procesarFinCombate();
        verificar(partida.obtenerEstres() == 0 && !partida.elegirMejora(TipoMejora.CREDITOS),
                "Sin combate ganado no hay recompensa ni mejora");

        for (int numero = 1; numero <= 5; numero++) {
            Oferta anterior = partida.obtenerTienda().obtenerOfertas().get(0);
            int saldoAntes = partida.obtenerEquipo().obtenerInventario().obtenerCreditos();
            Batalla batalla = partida.iniciarSiguienteCombate();
            verificar(batalla.obtenerEquipo() == partida.obtenerEquipo()
                            && !batalla.esCombateJefe()
                            && partida.obtenerEtapa() == EtapaPartida.COMBATE,
                    "Combate normal " + numero + " usa el mismo equipo");

            partida.procesarFinCombate();
            verificar(partida.obtenerEtapa() == EtapaPartida.COMBATE
                            && partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == saldoAntes,
                    "Un combate pendiente no entrega recompensa");
            boolean bloqueado = false;
            try {
                partida.iniciarSiguienteCombate();
            } catch (IllegalStateException esperado) {
                bloqueado = true;
            }
            verificar(bloqueado && !partida.puedeGuardar() && !partida.comprar(anterior),
                    "Durante combate no se inicia otro, no se guarda ni se compra");

            Herramienta herramienta = new Herramienta("Prueba", 2);
            Snack snack = new Snack();
            partida.obtenerEquipo().obtenerInventario().agregarItem(herramienta);
            partida.obtenerEquipo().obtenerInventario().agregarItem(snack);
            verificar(!partida.equipar(primero, herramienta)
                            && !partida.usarConsumible(snack, primero),
                    "Partida bloquea las acciones de preparacion durante combate");
            partida.obtenerEquipo().obtenerInventario().retirarItem(herramienta);
            partida.obtenerEquipo().obtenerInventario().retirarItem(snack);

            primero.agregarEstado(new Burnout());
            simularVictoria(batalla);
            partida.procesarFinCombate();
            verificar(partida.obtenerEtapa() == EtapaPartida.PREPARACION
                            && partida.obtenerEstres() == numero
                            && partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == 30 * numero,
                    "La victoria suma estres y 30 creditos");
            Oferta renovada = partida.obtenerTienda().obtenerOfertas().get(0);
            verificar(renovada != anterior && primero.obtenerEstados().isEmpty()
                            && primero.obtenerVida() == vidaInicial
                            && primero.obtenerEnergia() == energiaInicial,
                    "Renueva ofertas y limpia estados sin curar ni recargar");
            partida.procesarFinCombate();
            verificar(partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == 30 * numero
                            && partida.obtenerTienda().obtenerOfertas().get(0) == renovada,
                    "Procesar dos veces no repite recompensa ni renovacion");
        }

        verificar(despedido.obtenerNivel() == 1 && despedido.obtenerExperiencia() == 0
                        && primero.obtenerNivel() == 2 && primero.obtenerExperiencia() == 100,
                "Cada superviviente recibe la XP completa; el despedido no recibe XP");
        Oferta cafeAntesDelJefe = partida.obtenerTienda().obtenerOfertas().get(1);
        verificar(partida.puedeGuardar() && partida.comprar(cafeAntesDelJefe),
                "Se puede comprar y guardar antes del jefe con estres 5");

        Path archivo = Files.createTempFile("progresion-", ".rpg");
        try {
            PersistenciaPartida persistencia = new PersistenciaPartida();
            persistencia.guardar(partida, archivo.toString());
            partida = persistencia.cargar(archivo.toString());
            Batalla jefe = partida.iniciarSiguienteCombate();
            verificar(jefe.esCombateJefe() && jefe.obtenerEnemigos().size() == 1
                            && jefe.obtenerEnemigos().get(0) instanceof JefeEmpresa,
                    "Cargar con estres 5 obliga a enfrentar al jefe");
            Oferta ofertaAntesDeGanar = partida.obtenerTienda().obtenerOfertas().get(0);
            simularVictoria(jefe);
            partida.procesarFinCombate();
            verificar(partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA
                            && partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == 215
                            && partida.obtenerTienda().obtenerOfertas().get(0) == ofertaAntesDeGanar,
                    "El jefe paga 80 con el multiplicador anterior y deja pendiente la mejora");

            boolean bloqueado = false;
            try {
                partida.iniciarSiguienteCombate();
            } catch (IllegalStateException esperado) {
                bloqueado = true;
            }
            verificar(bloqueado && !partida.elegirMejora(null) && !partida.comprar(ofertaAntesDeGanar),
                    "La eleccion no se puede saltear ni usar para comprar");
            persistencia.guardar(partida, archivo.toString());
            partida = persistencia.cargar(archivo.toString());
            partida.procesarFinCombate();
            verificar(partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA
                            && partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == 215
                            && !partida.obtenerTienda().obtenerOfertas().get(1).estaDisponible(),
                    "Guardar/cargar conserva la mejora pendiente y las compras sin pagar otra vez");
            verificar(partida.elegirMejora(TipoMejora.CREDITOS)
                            && partida.obtenerCiclo() == 2 && partida.obtenerEstres() == 0
                            && Math.abs(partida.obtenerMultiplicadorCreditos() - 1.20) < 0.000001
                            && partida.obtenerMultiplicadorExperiencia() == 1.0
                            && partida.obtenerTienda().obtenerOfertas().get(1).estaDisponible(),
                    "Elegir creditos inicia el ciclo 2 con estres 0 y catalogo renovado");
            verificar(!partida.elegirMejora(TipoMejora.CREDITOS),
                    "La misma mejora no puede elegirse dos veces");
            Batalla siguiente = partida.iniciarSiguienteCombate();
            simularVictoria(siguiente);
            partida.procesarFinCombate();
            verificar(partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == 251,
                    "La mejora se aplica a la victoria posterior: 30 x 1.20 = 36");
        } finally {
            Files.deleteIfExists(archivo);
        }

        Partida experiencia = new Partida();
        ganarCiclo(experiencia);
        experiencia.elegirMejora(TipoMejora.EXPERIENCIA);
        simularVictoria(experiencia.iniciarSiguienteCombate());
        experiencia.procesarFinCombate();
        verificar(experiencia.obtenerEquipo().obtenerEmpleados().get(0).obtenerExperiencia() == 48
                        && experiencia.obtenerEquipo().obtenerInventario().obtenerCreditos() == 260,
                "Mejorar experiencia aumenta XP futura sin cambiar los creditos");
        for (int i = 0; i < 5; i++) {
            simularVictoria(experiencia.iniciarSiguienteCombate());
            experiencia.procesarFinCombate();
        }
        experiencia.elegirMejora(TipoMejora.EXPERIENCIA);
        verificar(experiencia.obtenerCiclo() == 3
                        && Math.abs(experiencia.obtenerMultiplicadorExperiencia() - 1.40) < 0.000001,
                "La mejora es aditiva: 1.00, 1.20, 1.40");

        Partida derrota = new Partida();
        derrota.iniciarSiguienteCombate();
        for (Empleado empleado : derrota.obtenerEquipo().obtenerEmpleados()) {
            empleado.recibirDanio(10000);
        }
        derrota.procesarFinCombate();
        derrota.procesarFinCombate();
        verificar(derrota.estaFinalizada()
                        && derrota.obtenerEquipo().obtenerInventario().obtenerCreditos() == 0,
                "Perder a todos finaliza la partida sin recompensa");
        derrota.iniciarNueva();
        verificar(derrota.obtenerEquipo().obtenerActivos().size() == 4
                        && derrota.obtenerEtapa() == EtapaPartida.PREPARACION
                        && derrota.obtenerBatallaActual() == null,
                "Iniciar otra partida crea un equipo y una progresion nuevos");
        System.out.println("Progresion de partida: OK.");
    }

    private static void ganarCiclo(Partida partida) {
        for (int i = 0; i < 6; i++) {
            simularVictoria(partida.iniciarSiguienteCombate());
            partida.procesarFinCombate();
        }
    }

    private static void simularVictoria(Batalla batalla) {
        // EXCLUSIVO DE LA PRUEBA: simula el resultado, no representa turnos jugables.
        for (Enemigo enemigo : batalla.obtenerEnemigos()) {
            enemigo.recibirDanio(Integer.MAX_VALUE);
        }
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalStateException("FALLO: " + mensaje);
        }
        System.out.println("OK: " + mensaje);
    }
}
