package modelo.combate;

import modelo.combatientes.Empleado;
import modelo.combatientes.Combatiente;
import modelo.combatientes.Enemigo;
import modelo.combatientes.Equipo;
import modelo.inventario.Consumible;
import modelo.partida.Recompensa;
import java.util.ArrayList;
import java.util.List;

/** Combate por turnos. Partida sigue siendo responsable de las recompensas. */
public class Batalla {
    private final Equipo equipo;
    private final List<Enemigo> enemigos;
    private final boolean esJefe;
    private final Recompensa recompensa;
    private boolean iniciada;
    private final GestorTurnos gestorTurnos = new GestorTurnos();
    private final StringBuilder registro = new StringBuilder();

    public Batalla(Equipo equipo, List<Enemigo> enemigos,
                   boolean esJefe, Recompensa recompensa) {
        if (equipo == null || enemigos == null || enemigos.isEmpty() || recompensa == null) {
            throw new IllegalArgumentException("Faltan datos del encuentro");
        }
        this.enemigos = new ArrayList<>();
        for (Enemigo enemigo : enemigos) {
            if (enemigo == null || !enemigo.estaActivo() || this.enemigos.contains(enemigo)) {
                throw new IllegalArgumentException("Los enemigos deben ser activos y distintos");
            }
            this.enemigos.add(enemigo);
        }
        this.equipo = equipo;
        this.esJefe = esJefe;
        this.recompensa = recompensa;
    }

    public void iniciar() {
        if (iniciada || equipo.estanTodosDespedidos()) {
            throw new IllegalStateException("La batalla no puede iniciarse");
        }
        iniciada = true;
        List<Combatiente> participantes = new ArrayList<>(equipo.obtenerEmpleados());
        participantes.addAll(enemigos);
        gestorTurnos.iniciar(participantes);
        registrar(esJefe ? "Comienza el combate contra el jefe." : "Comienza el combate.");
        prepararTurno();
    }

    public Combatiente obtenerCombatienteActual() {
        return !iniciada || estaFinalizada() ? null : gestorTurnos.obtenerActual();
    }

    public int obtenerRonda() {
        return gestorTurnos.obtenerRonda();
    }

    public String obtenerRegistro() {
        return registro.toString();
    }

    public boolean atacar(Combatiente objetivo) {
        Combatiente actor = obtenerCombatienteActual();
        if (!esTurnoDelJugador() || !enemigos.contains(objetivo) || !objetivo.estaActivo()) {
            return false;
        }
        int vidaAntes = objetivo.obtenerVida();
        actor.atacar(objetivo);
        registrar(actor.obtenerNombre() + " ataca a " + objetivo.obtenerNombre()
                + ": " + (vidaAntes - objetivo.obtenerVida()) + " de daño.");
        cerrarTurno();
        return true;
    }

    public boolean defender() {
        if (!esTurnoDelJugador()) {
            return false;
        }
        Combatiente actor = obtenerCombatienteActual();
        actor.defender();
        registrar(actor.obtenerNombre() + " se defiende hasta su próximo turno.");
        cerrarTurno();
        return true;
    }

    public boolean usarHabilidad(Habilidad habilidad, Combatiente objetivo) {
        if (!esTurnoDelJugador() || !esParticipante(objetivo)) {
            return false;
        }
        Combatiente actor = obtenerCombatienteActual();
        int vidaAntes = objetivo.obtenerVida();
        if (!actor.usarHabilidad(habilidad, objetivo)) {
            return false;
        }
        registrar(actor.obtenerNombre() + " usa " + habilidad.obtenerNombre()
                + " sobre " + objetivo.obtenerNombre() + ".");
        registrarCambioVida(objetivo, vidaAntes);
        cerrarTurno();
        return true;
    }

    public boolean usarItem(Consumible item, Empleado objetivo) {
        if (!esTurnoDelJugador() || !equipo.usarConsumible(item, objetivo)) {
            return false;
        }
        registrar(obtenerCombatienteActual().obtenerNombre() + " usa " + item.obtenerNombre()
                + " sobre " + objetivo.obtenerNombre() + ".");
        cerrarTurno();
        return true;
    }

    public void ejecutarTurnoEnemigo() {
        Combatiente actor = obtenerCombatienteActual();
        if (!(actor instanceof Enemigo)) {
            return;
        }
        List<Empleado> empleados = equipo.obtenerEmpleados();
        int[] vidas = new int[empleados.size()];
        for (int i = 0; i < empleados.size(); i++) {
            vidas[i] = empleados.get(i).obtenerVida();
        }
        registrar(actor.obtenerNombre() + " realiza su acción.");
        ((Enemigo) actor).ejecutarAccion(equipo);
        for (int i = 0; i < empleados.size(); i++) {
            registrarCambioVida(empleados.get(i), vidas[i]);
        }
        cerrarTurno();
    }

    private boolean esTurnoDelJugador() {
        Combatiente actual = obtenerCombatienteActual();
        return actual instanceof Empleado && actual.puedeActuar();
    }

    private boolean esParticipante(Combatiente objetivo) {
        return objetivo != null && (enemigos.contains(objetivo)
                || equipo.obtenerEmpleados().contains(objetivo));
    }

    private void prepararTurno() {
        while (!estaFinalizada()) {
            Combatiente actor = gestorTurnos.obtenerActual();
            if (actor == null) {
                return;
            }
            if (!actor.estaActivo()) {
                gestorTurnos.avanzar();
                continue;
            }
            int vidaAntes = actor.obtenerVida();
            actor.iniciarTurno();
            registrarCambioVida(actor, vidaAntes);
            if (actor.puedeActuar()) {
                return;
            }
            registrar(actor.obtenerNombre() + " pierde el turno.");
            // También vence la duración del ticket si no pudo actuar.
            actor.finalizarTurno();
            if (!estaFinalizada()) {
                gestorTurnos.avanzar();
            }
        }
    }

    private void cerrarTurno() {
        // Usamos el gestor: la acción pudo haber terminado la batalla.
        gestorTurnos.obtenerActual().finalizarTurno();
        if (!estaFinalizada()) {
            gestorTurnos.avanzar();
            prepararTurno();
        }
    }

    private void registrarCambioVida(Combatiente objetivo, int vidaAntes) {
        int diferencia = objetivo.obtenerVida() - vidaAntes;
        if (diferencia != 0) {
            registrar(objetivo.obtenerNombre() + (diferencia > 0 ? " recupera " : " pierde ")
                    + Math.abs(diferencia) + " de vida. Vida: " + objetivo.obtenerVida() + ".");
        }
    }

    private void registrar(String mensaje) {
        registro.append(mensaje).append('\n');
    }

    public boolean estaFinalizada() {
        return iniciada && (equipo.estanTodosDespedidos() || !quedanEnemigosActivos());
    }

    public boolean fueVictoria() {
        // Si ambos bandos quedan sin activos, prevalece la derrota del equipo.
        return estaFinalizada() && !equipo.estanTodosDespedidos();
    }

    public boolean esCombateJefe() {
        return esJefe;
    }

    public Recompensa obtenerRecompensa() {
        // Solo consulta: entregar experiencia/creditos corresponde a Partida.
        return recompensa;
    }

    public Equipo obtenerEquipo() {
        return equipo;
    }

    public List<Enemigo> obtenerEnemigos() {
        return new ArrayList<>(enemigos);
    }

    private boolean quedanEnemigosActivos() {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.estaActivo()) {
                return true;
            }
        }
        return false;
    }
}
