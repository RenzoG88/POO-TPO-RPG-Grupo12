package modelo.combatientes;

public class UsuarioPhishing extends Enemigo {

    private static final long serialVersionUID = 1L;

    public UsuarioPhishing() {
        this(80, 22, 5);
    }

    public UsuarioPhishing(int vidaMaxima, int ataque, int defensa) {
        super("Usuario phishing", vidaMaxima, 0, ataque, defensa, 9);
    }

    @Override
    public void ejecutarAccion(Equipo equipo) {
        if (!estaActivo()) {
            return;
        }

        Empleado objetivo = elegirObjetivo(equipo);

        if (objetivo != null) {
            atacar(objetivo);
        }
    }
}
