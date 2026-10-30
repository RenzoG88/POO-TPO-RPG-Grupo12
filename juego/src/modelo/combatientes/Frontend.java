package modelo.combatientes;

import modelo.combate.AtaquePotenciado;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Frontend extends Empleado {

    private static final long serialVersionUID = 1L;

    public Frontend() {
        super("Frontend", 100, 80, 22, 5, 18);
        registrarHabilidadInicial();
    }

    private void registrarHabilidadInicial() {
        if (obtenerHabilidades().isEmpty()) {
            agregarHabilidad(new AtaquePotenciado("Doble renderizado", 10, 0.8, 2));
        }
    }

    private void readObject(ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        registrarHabilidadInicial();
    }
}
