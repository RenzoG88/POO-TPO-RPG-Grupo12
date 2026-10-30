import modelo.combate.Batalla;
import modelo.combate.GestorTurnos;
import modelo.combate.Habilidad;
import modelo.combatientes.*;
import modelo.estadostemporales.Burnout;
import modelo.estadostemporales.TicketPendiente;
import modelo.inventario.*;
import modelo.partida.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** Prueba el motor real. El ciclo se gana con acciones, sin forzar victorias. */
public class PruebaBatallaJugable {
    public static void main(String[] args) throws Exception {
        probarTurnos();
        probarEstados();
        probarHabilidadesYObjetos();
        probarDerrota();
        jugarCiclo(true);
        System.out.println("Juego completo: 5 combates y jefe, OK.");
    }

    private static Batalla crearBatalla(Equipo equipo, Enemigo enemigo) {
        Batalla batalla = new Batalla(equipo, Arrays.asList(enemigo), false, new Recompensa(40, 30));
        batalla.iniciar();
        return batalla;
    }

    private static void probarTurnos() {
        Equipo equipo = new Equipo();
        UsuarioPhishing enemigo = new UsuarioPhishing();
        Batalla batalla = crearBatalla(equipo, enemigo);
        Combatiente primero = batalla.obtenerCombatienteActual();
        verificar(primero instanceof Frontend, "Comienza el empleado con mayor velocidad");
        verificar(!batalla.atacar(null) && !batalla.atacar(equipo.obtenerEmpleados().get(0))
                        && !batalla.atacar(new UsuarioPhishing())
                        && batalla.obtenerCombatienteActual() == primero,
                "Objetivos nulos, aliados y ajenos se rechazan sin perder turno");
        batalla.ejecutarTurnoEnemigo();
        verificar(batalla.obtenerCombatienteActual() == primero,
                "No se puede ejecutar un enemigo durante el turno del jugador");
        verificar(batalla.atacar(enemigo) && enemigo.obtenerVida() == 63
                        && batalla.obtenerCombatienteActual() instanceof Backend,
                "Un ataque válido produce daño y avanza una sola vez");
        batalla.defender();
        verificar(batalla.obtenerCombatienteActual() instanceof Infraestructura,
                "Infraestructura sigue a Backend");
        batalla.defender();
        verificar(batalla.obtenerCombatienteActual() == enemigo && !batalla.atacar(enemigo),
                "Durante el turno enemigo se rechazan acciones del jugador");
        batalla.ejecutarTurnoEnemigo();
        verificar(equipo.obtenerEmpleados().get(0).obtenerVida() == 154
                        && batalla.obtenerCombatienteActual() instanceof Ciberseguridad,
                "El rival ejecuta su conducta y después actúa Ciberseguridad");
        batalla.defender();
        verificar(batalla.obtenerRonda() == 2 && batalla.obtenerCombatienteActual() == primero,
                "La siguiente ronda vuelve a ordenar los participantes activos");

        GestorTurnos gestor = new GestorTurnos();
        UsuarioPhishing uno = new UsuarioPhishing();
        UsuarioPhishing dos = new UsuarioPhishing();
        gestor.iniciar(Arrays.asList(uno, dos));
        verificar(gestor.obtenerActual() == uno, "Los empates de velocidad conservan el orden");
        dos.recibirDanio(1000);
        gestor.avanzar();
        verificar(gestor.obtenerActual() == uno && gestor.obtenerRonda() == 2,
                "Un derrotado no recibe turno");
    }

    private static void probarEstados() {
        Equipo equipo = new Equipo();
        Empleado frontend = equipo.obtenerEmpleados().get(3);
        frontend.agregarEstado(new Burnout(2, 5));
        frontend.agregarEstado(new TicketPendiente(1));
        Batalla batalla = crearBatalla(equipo, new UsuarioPhishing());
        verificar(frontend.obtenerVida() == 95 && frontend.obtenerEstados().size() == 1
                        && batalla.obtenerCombatienteActual() instanceof Backend,
                "Burnout se aplica una vez y el ticket pierde y cierra el turno");
        batalla.defender();
        batalla.defender();
        batalla.ejecutarTurnoEnemigo();
        batalla.defender();
        verificar(batalla.obtenerCombatienteActual() == frontend && frontend.obtenerVida() == 90,
                "El ticket expiró y el siguiente inicio aplica el segundo burnout");
        batalla.obtenerCombatienteActual();
        batalla.obtenerCombatienteActual();
        verificar(frontend.obtenerVida() == 90, "Consultar el turno no vuelve a aplicar efectos");
        batalla.defender();
        verificar(frontend.obtenerEstados().isEmpty(), "Los estados vencen al cerrar el turno");

        UsuarioPhishing rival = new UsuarioPhishing(4, 22, 5);
        rival.agregarEstado(new Burnout(1, 5));
        Batalla muerte = crearBatalla(new Equipo(), rival);
        muerte.defender();
        muerte.defender();
        muerte.defender();
        verificar(muerte.estaFinalizada() && muerte.fueVictoria()
                        && muerte.obtenerCombatienteActual() == null && !muerte.defender(),
                "Morir por burnout puede finalizar la batalla antes de actuar");
    }

    private static void probarHabilidadesYObjetos() {
        Equipo equipo = new Equipo();
        Empleado frontend = equipo.obtenerEmpleados().get(3);
        UsuarioPhishing rival = new UsuarioPhishing();
        Batalla batalla = crearBatalla(equipo, rival);
        Habilidad habilidad = frontend.obtenerHabilidades().get(0);
        verificar(!batalla.usarHabilidad(habilidad, new UsuarioPhishing())
                        && frontend.obtenerEnergia() == 80,
                "No se gastan recursos en una habilidad con objetivo ajeno");
        verificar(batalla.usarHabilidad(habilidad, rival) && frontend.obtenerEnergia() == 70
                        && rival.obtenerVida() == 56,
                "Doble golpe descuenta energía una vez y consume un turno");
        verificar(!batalla.usarHabilidad(habilidad, rival), "Otro héroe no puede usar esa habilidad");

        Equipo otro = new Equipo();
        Empleado herido = otro.obtenerEmpleados().get(3);
        herido.recibirDanio(30);
        Snack snack = new Snack();
        otro.obtenerInventario().agregarItem(snack);
        Batalla objetos = crearBatalla(otro, new UsuarioPhishing());
        verificar(!objetos.usarItem(snack, equipo.obtenerEmpleados().get(0))
                        && !objetos.usarItem(snack, otro.obtenerEmpleados().get(1))
                        && otro.obtenerInventario().contiene(snack),
                "Un consumible inválido se conserva y no gasta el turno");
        verificar(objetos.usarItem(snack, herido) && herido.obtenerVida() == 100
                        && !otro.obtenerInventario().contiene(snack)
                        && objetos.obtenerCombatienteActual() instanceof Backend,
                "Consumir recupera vida, retira el objeto y avanza el turno");
        verificar(!objetos.usarItem(snack, herido), "El mismo consumible no se usa dos veces");
    }

    private static void probarDerrota() {
        Equipo equipo = new Equipo();
        for (Empleado empleado : equipo.obtenerEmpleados()) {
            empleado.recibirDanio(empleado.obtenerVida() - 1);
        }
        Batalla batalla = crearBatalla(equipo, new JefeEmpresa());
        int acciones = 0;
        while (!batalla.estaFinalizada() && acciones++ < 100) {
            if (batalla.obtenerCombatienteActual() instanceof Enemigo) {
                batalla.ejecutarTurnoEnemigo();
            } else {
                batalla.defender();
            }
        }
        verificar(batalla.estaFinalizada() && !batalla.fueVictoria()
                        && equipo.estanTodosDespedidos(),
                "Una derrota real termina el combate sin bloquear los turnos");
    }

    public static void jugarCiclo(boolean imprimir) throws Exception {
        Partida partida = new Partida();
        Path archivo = Files.createTempFile("ciclo-jugable-", ".rpg");
        try {
            for (int numero = 1; numero <= 6; numero++) {
                prepararEquipo(partida);
                int saldoAntes = partida.obtenerEquipo().obtenerInventario().obtenerCreditos();
                Batalla batalla = partida.iniciarSiguienteCombate();
                exigir(batalla.esCombateJefe() == (numero == 6), "El jefe debe ser el sexto encuentro");
                exigir(!partida.puedeGuardar(), "No se guarda durante el combate");
                int acciones = 0;
                while (!batalla.estaFinalizada() && acciones++ < 1000) {
                    jugarTurno(batalla);
                }
                exigir(batalla.fueVictoria(), "No se ganó el encuentro " + numero);
                partida.procesarFinCombate();
                partida.procesarFinCombate();
                exigir(partida.obtenerEquipo().obtenerInventario().obtenerCreditos()
                                == saldoAntes + (numero == 6 ? 80 : 30),
                        "La recompensa se entrega una sola vez");
                if (numero <= 5) {
                    exigir(partida.obtenerEstres() == numero
                                    && partida.obtenerEtapa() == EtapaPartida.PREPARACION,
                            "Se vuelve a preparación después de cada victoria normal");
                }
                if (imprimir) {
                    System.out.println("OK: " + (numero == 6 ? "Jefe" : "Combate " + numero + "/5")
                            + " ganado con turnos reales. Acciones: " + acciones);
                }
                if (numero == 2 || numero == 6) {
                    PersistenciaPartida persistencia = new PersistenciaPartida();
                    persistencia.guardar(partida, archivo.toString());
                    partida = persistencia.cargar(archivo.toString());
                }
            }
            exigir(partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA,
                    "El jefe deja pendiente la elección de mejora");
            exigir(partida.elegirMejora(TipoMejora.CREDITOS) && partida.obtenerCiclo() == 2
                            && partida.obtenerEstres() == 0 && !partida.elegirMejora(TipoMejora.CREDITOS),
                    "La mejora abre el ciclo 2 y no se puede elegir dos veces");
            exigir(!partida.iniciarSiguienteCombate().esCombateJefe(), "El ciclo 2 comienza con normales");
            partida.iniciarNueva();
            exigir(partida.obtenerCiclo() == 1 && partida.obtenerEquipo().obtenerActivos().size() == 4
                            && partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == 0,
                    "Una partida nueva reinicia el equipo y la progresión");
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    private static void prepararEquipo(Partida partida) {
        // Compra real con las recompensas ganadas. No agrega créditos artificiales.
        Empleado tanque = partida.obtenerEquipo().obtenerActivos().get(0);
        for (Oferta oferta : partida.obtenerTienda().obtenerOfertas()) {
            if (oferta.obtenerItem() instanceof Snack && tanque.obtenerVidaMaxima() - tanque.obtenerVida() >= 30) {
                if (partida.comprar(oferta)) {
                    partida.usarConsumible((Consumible) oferta.obtenerItem(), tanque);
                }
            }
        }
    }

    private static void jugarTurno(Batalla batalla) {
        Combatiente actor = batalla.obtenerCombatienteActual();
        exigir(actor != null, "Una batalla en curso debe tener un actor");
        if (actor instanceof Enemigo) {
            batalla.ejecutarTurnoEnemigo();
            return;
        }
        Habilidad habilidad = actor.obtenerHabilidades().get(0);
        if (actor instanceof Infraestructura && actor.obtenerEnergia() >= habilidad.obtenerCostoEnergia()) {
            for (Empleado aliado : batalla.obtenerEquipo().obtenerActivos()) {
                if (aliado.obtenerVidaMaxima() - aliado.obtenerVida() >= 25
                        && batalla.usarHabilidad(habilidad, aliado)) { return; }
            }
        }
        Enemigo objetivo = null;
        for (Enemigo enemigo : batalla.obtenerEnemigos()) {
            if (enemigo.estaActivo() && (objetivo == null || enemigo.obtenerVida() < objetivo.obtenerVida())) {
                objetivo = enemigo;
            }
        }
        if ((actor instanceof Backend || actor instanceof Frontend)
                && objetivo.obtenerVida() > Math.max(1, actor.obtenerAtaqueTotal() - objetivo.obtenerDefensaTotal())
                && batalla.usarHabilidad(habilidad, objetivo)) { return; }
        exigir(batalla.atacar(objetivo), "El ataque del turno debe ser válido");
    }

    private static void verificar(boolean condicion, String mensaje) {
        exigir(condicion, mensaje);
        System.out.println("OK: " + mensaje);
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) { throw new IllegalStateException("FALLO: " + mensaje); }
    }
}
