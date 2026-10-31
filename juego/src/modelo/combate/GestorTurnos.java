package modelo.combate;

import modelo.combatientes.Combatiente;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Ordena participantes; Batalla se ocupa de ejecutar y cerrar sus turnos. */
public class GestorTurnos {
    private List<Combatiente> participantes = new ArrayList<>();
    private List<Combatiente> ordenTurnos = new ArrayList<>();
    private int indiceTurno;
    private int ronda;

    public void iniciar(List<Combatiente> participantes) {
        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("Faltan participantes");
        }
        this.participantes = new ArrayList<>(participantes);
        ronda = 0;
        iniciarRonda();
    }

    public Combatiente obtenerActual() {
        return indiceTurno < ordenTurnos.size() ? ordenTurnos.get(indiceTurno) : null;
    }

    public void avanzar() {
        indiceTurno++;
        while (indiceTurno < ordenTurnos.size()
                && !ordenTurnos.get(indiceTurno).estaActivo()) {
            indiceTurno++;
        }
        if (indiceTurno >= ordenTurnos.size()) {
            iniciarRonda();
        }
    }

    public int obtenerRonda() {
        return ronda;
    }

    /** Copia del orden de esta ronda; incluye los turnos ya terminados. */
    public List<Combatiente> obtenerOrdenTurnos() {
        return new ArrayList<>(ordenTurnos);
    }

    public int obtenerIndiceTurno() {
        return indiceTurno;
    }

    private void iniciarRonda() {
        ordenTurnos = new ArrayList<>();
        for (Combatiente participante : participantes) {
            if (participante.estaActivo()) {
                ordenTurnos.add(participante);
            }
        }
        // sort es estable: en empates conserva equipo y luego enemigos.
        ordenTurnos.sort(Comparator.comparingInt(Combatiente::obtenerVelocidad).reversed());
        indiceTurno = 0;
        ronda++;
    }
}
