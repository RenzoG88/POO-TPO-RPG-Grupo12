package modelo.combatientes;

import modelo.combate.AplicarEstado;
import modelo.combate.Habilidad;
import modelo.estadostemporales.TipoEstado;

public class ClienteExigente extends Enemigo {
    private static final long serialVersionUID = 1L;
    public static final int VIDA_BASE = 144;
    public static final int ATAQUE_BASE = 22;
    public static final int DEFENSA_BASE = 4;
    private final Habilidad burnout;
    private boolean usarEspecial;

    public ClienteExigente() {
        this(VIDA_BASE, ATAQUE_BASE, DEFENSA_BASE);
    }

    public ClienteExigente(int vidaMaxima, int ataque, int defensa) {
        super("Cliente exigente", vidaMaxima, 40, ataque, defensa, 7);
        burnout = new AplicarEstado("Lo necesito para ayer", 10, TipoEstado.BURNOUT, 3, 5);
        agregarHabilidad(burnout);
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
        if (!usarEspecial || !usarHabilidad(burnout, objetivo)) {
            atacar(objetivo);
        }
        usarEspecial = !usarEspecial;
    }
}
