package modelo.combatientes;

import modelo.combate.Reparacion;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Infraestructura extends Empleado {

    private static final long serialVersionUID = 1L;

    public Infraestructura() {
        super("Infraestructura", 120, 100, 12, 8, 10);
        registrarHabilidadInicial();
    }

    private void registrarHabilidadInicial() {
        if (obtenerHabilidades().isEmpty()) {
            agregarHabilidad(new Reparacion("Reiniciar servicio", 10, 30));
        }
    }

    private void readObject(ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        registrarHabilidadInicial();
    }
}
