package modelo.combatientes;

import modelo.inventario.Inventario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Empleado> empleados;
    private Inventario inventario;

    public Equipo() {
        empleados = new ArrayList<>();
        empleados.add(new Ciberseguridad());
        empleados.add(new Infraestructura());
        empleados.add(new Backend());
        empleados.add(new Frontend());

        inventario = new Inventario();
    }

    public List<Empleado> obtenerEmpleados() {
        return new ArrayList<>(empleados);
    }

    public List<Empleado> obtenerActivos() {
        List<Empleado> activos = new ArrayList<>();

        for (Empleado empleado : empleados) {
            if (empleado.estaActivo()) {
                activos.add(empleado);
            }
        }

        return activos;
    }

    public boolean pertenece(Empleado empleado) {
        return empleados.contains(empleado);
    }

    public boolean estanTodosDespedidos() {
        return obtenerActivos().isEmpty();
    }

    public void repartirExperiencia(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La experiencia no puede ser negativa"
            );
        }

        for (Empleado empleado : obtenerActivos()) {
            empleado.ganarExperiencia(cantidad);
        }
    }

    public Inventario obtenerInventario() {
        return inventario;
    }
}