package modelo.combatientes;

import modelo.combate.AplicarEstado;
import modelo.combate.Habilidad;
import modelo.estadostemporales.TipoEstado;

public class Burocrata extends Enemigo {
    private static final long serialVersionUID = 1L;
    public static final int VIDA_BASE = 120;
    public static final int ATAQUE_BASE = 20;
    public static final int DEFENSA_BASE = 8;
    private final Habilidad ticket;
    private boolean usarEspecial;

    public Burocrata() {
        this(VIDA_BASE, ATAQUE_BASE, DEFENSA_BASE);
    }

    public Burocrata(int vidaMaxima, int ataque, int defensa) {
        super("Burocrata", vidaMaxima, 30, ataque, defensa, 6);
        ticket = new AplicarEstado("Primero carga un ticket", 10,
                TipoEstado.TICKET_PENDIENTE, 1, 0);
        agregarHabilidad(ticket);
    }

    @Override
    public void ejecutarAccion(Equipo equipo) {
        if (!puedeActuar()) {
            return;
        }
        Empleado objetivo = elegirObjetivo(equipo);
        if (objetivo == null) {
            return;
        }
        if (!usarEspecial || !usarHabilidad(ticket, objetivo)) {
            atacar(objetivo);
        }
        usarEspecial = !usarEspecial;
    }
}
