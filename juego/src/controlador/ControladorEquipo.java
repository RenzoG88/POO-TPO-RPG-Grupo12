package controlador;

import dto.EmpleadoDTO;
import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.inventario.Equipamiento;
import modelo.inventario.Herramienta;
import modelo.inventario.Item;
import modelo.inventario.Proteccion;
import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;

import java.io.IOException;
import vista.PantallaEquipo;

import java.util.ArrayList;
import java.util.List;

public class ControladorEquipo {

    private Partida partida;
    private final PersistenciaPartida persistencia;
    private final PantallaEquipo vista;

    public ControladorEquipo(Partida partida, PantallaEquipo vista) {
        this.partida = partida;
        this.persistencia = new PersistenciaPartida();
        this.vista = vista;
    }

    public void guardarPartida() {
        if (!partida.puedeGuardar()) {
            vista.mostrarMensaje("No se puede guardar en esta etapa de la partida.");
            return;
        }

        String ruta = vista.elegirRutaGuardado();

        if (ruta == null) {
            return;
        }

        try {
            persistencia.guardar(partida, ruta);
            vista.mostrarMensaje("Partida guardada correctamente.");
        } catch (IOException | IllegalArgumentException | IllegalStateException error) {
            vista.mostrarMensaje("No se pudo guardar la partida. " + error.getMessage());
        }
    }

    public void cargarPartida() {
        String ruta = vista.elegirRutaCarga();

        if (ruta == null || !vista.confirmarCarga()) {
            return;
        }

        try {
            Partida cargada = persistencia.cargar(ruta);
            // Se reemplaza la partida abierta solo despues de una carga correcta.
            partida = cargada;
            refrescarVista();
            vista.mostrarMensaje("Partida cargada correctamente.");
        } catch (IOException | ClassNotFoundException | IllegalArgumentException error) {
            vista.mostrarMensaje("No se pudo cargar la partida. " + error.getMessage());
        }
    }

    public void refrescarVista() {
        Equipo equipo = partida.obtenerEquipo();
        List<EmpleadoDTO> datos = new ArrayList<>();

        for (Empleado empleado : equipo.obtenerEmpleados()) {
            String herramienta = empleado.obtenerHerramienta() == null
                    ? "Sin herramienta" : empleado.obtenerHerramienta().obtenerNombre();
            String proteccion = empleado.obtenerProteccion() == null
                    ? "Sin proteccion" : empleado.obtenerProteccion().obtenerNombre();

            datos.add(new EmpleadoDTO(
                    empleado.obtenerNombre(),
                    empleado.obtenerNivel(),
                    empleado.obtenerExperiencia(),
                    empleado.obtenerVida(),
                    empleado.obtenerVidaMaxima(),
                    empleado.obtenerEnergia(),
                    empleado.obtenerEnergiaMaxima(),
                    empleado.estaDespedido(),
                    empleado.obtenerAtaqueTotal(),
                    empleado.obtenerDefensaTotal(),
                    herramienta,
                    proteccion
            ));
        }

        vista.mostrarEquipo(datos, equipo.obtenerInventario().obtenerCreditos());
        vista.mostrarProgreso(partida.obtenerCiclo(), partida.obtenerEstres(),
                partida.obtenerLimiteEstres());
    }

    public void gestionarEquipamiento(int indiceEmpleado) {
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

        refrescarVista();
    }

    private void equiparDesdeInventario(Empleado empleado) {
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
            String detalle = "";

            if (objeto instanceof Herramienta) {
                detalle = " | Ataque +" + ((Herramienta) objeto).obtenerBonusAtaque();
            } else if (objeto instanceof Proteccion) {
                detalle = " | Defensa +" + ((Proteccion) objeto).obtenerBonusDefensa();
            }

            opciones[i] = (i + 1) + ". " + objeto.obtenerNombre() + detalle;
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
