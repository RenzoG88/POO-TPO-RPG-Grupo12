package modelo.combatientes;

import java.util.List;

public abstract class Enemigo extends Combatiente {

    private static final long serialVersionUID = 1L;

    public Enemigo(String nombre, int vidaMaxima, int energiaMaxima,
                   int ataque, int defensa, int velocidad) {
        super(nombre, vidaMaxima, energiaMaxima, ataque, defensa, velocidad);
    }

    public abstract void ejecutarAccion(Equipo equipo);

    protected Empleado elegirObjetivo(Equipo equipo) {
        if (equipo == null) {
            return null;
        }

        List<Empleado> activos = equipo.obtenerActivos();

        if (activos.isEmpty()) {
            return null;
        }

        // Elegimos el primero que siga activo en el orden del equipo.
        return activos.get(0);
    }
}
