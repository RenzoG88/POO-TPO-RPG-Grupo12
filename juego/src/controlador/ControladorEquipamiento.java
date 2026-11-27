package controlador;

import modelo.combatientes.Empleado;
import modelo.inventario.Equipamiento;
import modelo.inventario.Item;
import java.util.ArrayList;
import java.util.List;
import modelo.partida.Partida;
import vista.organizador.PantallaEquipo;

/** Gestiona la eleccion y el cambio de equipamiento. */
public class ControladorEquipamiento {
    private final SesionPartida sesion;
    private final PantallaEquipo vista;
    private final Runnable alCambiar;

    public ControladorEquipamiento(SesionPartida sesion, PantallaEquipo vista, Runnable alCambiar) {
        this.sesion = sesion;
        this.vista = vista;
        this.alCambiar = alCambiar;
    }

    public void gestionarEquipamiento(int indiceEmpleado) {
        Partida partida = sesion.obtenerPartida();
        List<Empleado> empleados = partida.obtenerEquipo().obtenerEmpleados();

        if (indiceEmpleado < 0 || indiceEmpleado >= empleados.size()) {
            vista.mostrarMensaje("No se encontro el empleado seleccionado.");
            return;
        }

        Empleado empleado = empleados.get(indiceEmpleado);
        int accion = vista.elegirAccionEquipamiento(empleado.obtenerNombre());

        switch (accion) {
            case 0:
                equiparDesdeInventario(empleado);
                break;
            case 1:
                quitarEquipamiento(empleado, empleado.obtenerHerramienta());
                break;
            case 2:
                quitarEquipamiento(empleado, empleado.obtenerProteccion());
                break;
            default:
                return;
        }

        alCambiar.run();
    }

    private void equiparDesdeInventario(Empleado empleado) {
        Partida partida = sesion.obtenerPartida();
        List<Equipamiento> disponibles = new ArrayList<>();

        for (Item item : partida.obtenerEquipo().obtenerInventario().obtenerItems()) {
            if (item instanceof Equipamiento) {
                disponibles.add((Equipamiento) item);
            }
        }

        if (disponibles.isEmpty()) {
            vista.mostrarMensaje("No hay equipamiento disponible en el inventario.");
            return;
        }

        String[] opciones = new String[disponibles.size()];

        for (int i = 0; i < disponibles.size(); i++) {
            Equipamiento objeto = disponibles.get(i);
            opciones[i] = (i + 1) + ". " + objeto.obtenerDescripcion();
        }

        int seleccion = vista.elegirObjeto(empleado.obtenerNombre(), opciones);

        if (seleccion < 0 || seleccion >= disponibles.size()) {
            return;
        }

        // Usamos el objeto de la misma lista que se mostro en el selector.
        Equipamiento elegido = disponibles.get(seleccion);

        if (!partida.equipar(empleado, elegido)) {
            vista.mostrarMensaje("No se pudo equipar el objeto. El empleado debe estar "
                    + "activo, el objeto disponible y la partida en preparacion.");
        }
    }

    private void quitarEquipamiento(Empleado empleado, Equipamiento objeto) {
        Partida partida = sesion.obtenerPartida();
        if (objeto == null) {
            vista.mostrarMensaje("El empleado no tiene un objeto equipado en esa casilla.");
            return;
        }

        if (!partida.desequipar(empleado, objeto)) {
            vista.mostrarMensaje("No se pudo retirar el equipamiento. El objeto debe "
                    + "estar equipado y la partida debe estar en preparacion.");
        }
    }
}
