package controlador;

import modelo.combatientes.Empleado;
import modelo.inventario.Consumible;
import modelo.inventario.Item;
import modelo.inventario.Snack;
import modelo.inventario.Cafe;
import java.util.ArrayList;
import java.util.List;
import modelo.partida.Partida;
import vista.organizador.PantallaEquipo;

/** Gestiona la seleccion y el uso de consumibles en preparacion. */
public class ControladorConsumibles {
    private final SesionPartida sesion;
    private final PantallaEquipo vista;
    private final Runnable alCambiar;

    public ControladorConsumibles(SesionPartida sesion, PantallaEquipo vista, Runnable alCambiar) {
        this.sesion = sesion;
        this.vista = vista;
        this.alCambiar = alCambiar;
    }

    public void gestionarConsumibles() {
        Partida partida = sesion.obtenerPartida();
        List<Consumible> disponibles = new ArrayList<>();
        for (Item item : partida.obtenerEquipo().obtenerInventario().obtenerItems()) {
            if (item instanceof Consumible) {
                disponibles.add((Consumible) item);
            }
        }

        if (disponibles.isEmpty()) {
            vista.mostrarMensaje("No hay consumibles en el inventario.");
            return;
        }

        String[] opciones = new String[disponibles.size()];
        for (int i = 0; i < disponibles.size(); i++) {
            opciones[i] = (i + 1) + ". " + describirItem(disponibles.get(i));
        }
        int indiceItem = vista.elegirConsumible(opciones);
        if (indiceItem < 0 || indiceItem >= disponibles.size()) {
            return;
        }

        List<Empleado> empleados = partida.obtenerEquipo().obtenerEmpleados();
        String[] nombres = new String[empleados.size()];
        for (int i = 0; i < empleados.size(); i++) {
            Empleado empleado = empleados.get(i);
            nombres[i] = empleado.obtenerNombre()
                    + (empleado.estaDespedido() ? " (despedido)" : "");
        }
        int indiceEmpleado = vista.elegirEmpleado(nombres);
        if (indiceEmpleado < 0 || indiceEmpleado >= empleados.size()) {
            return;
        }

        if (partida.usarConsumible(disponibles.get(indiceItem), empleados.get(indiceEmpleado))) {
            alCambiar.run();
            vista.mostrarMensaje("Consumible aplicado.");
        } else {
            vista.mostrarMensaje("No se uso el objeto. El empleado debe estar activo "
                    + "y necesitar la recuperacion, y la partida debe estar en preparacion.");
        }
    }

    private String describirItem(Item item) {
        String detalle = "";
        if (item instanceof Snack) {
            detalle = "Recupera " + ((Snack) item).obtenerRecuperacionVida() + " de vida";
        } else if (item instanceof Cafe) {
            detalle = "Recupera " + ((Cafe) item).obtenerRecuperacionEnergia() + " de energia";
        }
        return item.obtenerNombre() + (detalle.isEmpty() ? "" : " | " + detalle);
    }

}
