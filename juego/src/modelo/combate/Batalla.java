package modelo.combate;

import modelo.combatientes.Empleado;
import modelo.combatientes.Enemigo;
import modelo.combatientes.Equipo;
import modelo.partida.Recompensa;
import java.util.ArrayList;
import java.util.List;

/**
 * Base de integracion para el modulo de combate.
 * El responsable de Batalla debe completar AQUI los turnos y las acciones del UML.
 * Esta version conserva participantes y consulta el resultado; no ejecuta combates.
 */
public class Batalla {
    private final Equipo equipo;
    private final List<Enemigo> enemigos;
    private final boolean esJefe;
    private final Recompensa recompensa;
    private boolean iniciada;

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
        // Integracion pendiente: crear el orden de turnos y preparar el primer actor.
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
