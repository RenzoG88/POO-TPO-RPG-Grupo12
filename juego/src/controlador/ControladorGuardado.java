package controlador;

import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;
import vista.organizador.PantallaEquipo;
import java.io.IOException;

/** Se encarga de guardar/cargar y conserva la sesion si falla una carga. */
public class ControladorGuardado {
    private final SesionPartida sesion;
    private final PantallaEquipo vista;
    private final Runnable alCambiar;
    private final PersistenciaPartida persistencia = new PersistenciaPartida();

    public ControladorGuardado(SesionPartida sesion, PantallaEquipo vista, Runnable alCambiar) {
        this.sesion = sesion;
        this.vista = vista;
        this.alCambiar = alCambiar;
    }

    public void guardarPartida() {
        Partida partida = sesion.obtenerPartida();
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
            sesion.reemplazarPartida(cargada);
            alCambiar.run();
            vista.mostrarMensaje("Partida cargada correctamente.");
        } catch (IOException | ClassNotFoundException | IllegalArgumentException error) {
            vista.mostrarMensaje("No se pudo cargar la partida. " + error.getMessage());
        }
    }

}
