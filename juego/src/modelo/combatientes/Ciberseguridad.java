package modelo.combatientes;

import modelo.combate.AplicarEstado;
import modelo.estadostemporales.TipoEstado;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Ciberseguridad extends Empleado {

    private static final long serialVersionUID = 1L;

    public Ciberseguridad() {
        super("Ciberseguridad", 160, 60, 18, 16, 8);
        registrarHabilidadInicial();
    }

    private void registrarHabilidadInicial() {
        if (obtenerHabilidades().isEmpty()) {
            agregarHabilidad(new AplicarEstado("Firewall activo", 10,
                    TipoEstado.FIREWALL_ACTIVO, 2, 25));
        }
    }

    private void readObject(ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        // Al cargar un guardado anterior, completar la habilidad sin reiniciar al empleado.
        registrarHabilidadInicial();
    }
}
