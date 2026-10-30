package modelo.combatientes;

import modelo.combate.AtaquePotenciado;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Backend extends Empleado {

    private static final long serialVersionUID = 1L;

    public Backend() {
        super("Backend", 110, 80, 28, 6, 12);
        registrarHabilidadInicial();
    }

    private void registrarHabilidadInicial() {
        if (obtenerHabilidades().isEmpty()) {
            agregarHabilidad(new AtaquePotenciado("Proceso pesado", 10, 1.8, 1));
        }
    }

    private void readObject(ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        registrarHabilidadInicial();
    }
}
