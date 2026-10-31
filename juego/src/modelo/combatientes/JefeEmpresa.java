package modelo.combatientes;

import modelo.combate.AplicarEstado;
import modelo.combate.AtaquePotenciado;
import modelo.combate.Habilidad;
import modelo.estadostemporales.TipoEstado;

public class JefeEmpresa extends Enemigo {
    private static final long serialVersionUID = 1L;
    public static final int VIDA_BASE = 336;
    public static final int ATAQUE_BASE = 35;
    public static final int DEFENSA_BASE = 10;
    private final Habilidad ataquePotente;
    private final Habilidad burnout;
    private boolean usarBurnout;

    public JefeEmpresa() {
        this(VIDA_BASE, ATAQUE_BASE, DEFENSA_BASE);
    }

    public JefeEmpresa(int vidaMaxima, int ataque, int defensa) {
        super("Jefe de la empresa", vidaMaxima, 60, ataque, defensa, 11);
        ataquePotente = new AtaquePotenciado("Reunion de directorio", 10, 1.5, 1);
        burnout = new AplicarEstado("Hay que ponerse la camiseta", 10,
                TipoEstado.BURNOUT, 3, 5);
        agregarHabilidad(ataquePotente);
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
        Habilidad elegida = usarBurnout ? burnout : ataquePotente;
        if (!usarHabilidad(elegida, objetivo)) {
            atacar(objetivo);
        }
        usarBurnout = !usarBurnout;
    }
}
