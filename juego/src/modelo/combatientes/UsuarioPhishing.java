package modelo.combatientes;

public class UsuarioPhishing extends Enemigo {

    private static final long serialVersionUID = 1L;
    public static final int VIDA_BASE = 80;
    public static final int ATAQUE_BASE = 22;
    public static final int DEFENSA_BASE = 5;

    public UsuarioPhishing() {
        this(VIDA_BASE, ATAQUE_BASE, DEFENSA_BASE);
    }

    public UsuarioPhishing(int vidaMaxima, int ataque, int defensa) {
        super("Usuario phishing", vidaMaxima, 0, ataque, defensa, 9);
    }

    @Override
    public void ejecutarAccion(Equipo equipo) {
        if (!puedeActuar()) {
            return;
        }

        Empleado objetivo = elegirObjetivo(equipo);

        if (objetivo != null) {
            atacar(objetivo);
        }
    }
}
