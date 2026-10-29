package modelo.combatientes;

import modelo.inventario.Inventario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.inventario.Equipamiento;
import modelo.inventario.Herramienta;
import modelo.inventario.Proteccion;

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
    public boolean equipar(Empleado empleado, Equipamiento objeto) {
        if (!pertenece(empleado) || objeto == null) {
            return false;
        }

        if (empleado.estaDespedido()) {
            return false;
        }

        if (!(objeto instanceof Herramienta)
                && !(objeto instanceof Proteccion)) {
            return false;
        }

        // El mismo objeto no puede estar equipado en dos empleados.
        for (Empleado integrante : empleados) {
            if (integrante.obtenerHerramienta() == objeto
                    || integrante.obtenerProteccion() == objeto) {
                return false;
            }
        }

        // Solo podemos equipar objetos disponibles en el inventario.
        if (!inventario.retirarItem(objeto)) {
            return false;
        }

        Equipamiento anterior;

        if (objeto instanceof Herramienta) {
            anterior = empleado.cambiarHerramienta((Herramienta) objeto);
        } else {
            anterior = empleado.cambiarProteccion((Proteccion) objeto);
        }

        if (anterior != null) {
            inventario.agregarItem(anterior);
        }

        return true;
    }

    public boolean desequipar(Empleado empleado, Equipamiento objeto) {
        if (!pertenece(empleado) || objeto == null) {
            return false;
        }

        if (empleado.obtenerHerramienta() == objeto) {
            empleado.cambiarHerramienta(null);
        } else if (empleado.obtenerProteccion() == objeto) {
            empleado.cambiarProteccion(null);
        } else {
            return false;
        }

        inventario.agregarItem(objeto);
        return true;
    }
}