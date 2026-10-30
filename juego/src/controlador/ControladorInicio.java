package controlador;

import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;
import vista.organizador.PantallaEquipo;
import vista.organizador.PantallaInicio;
import java.io.IOException;

/** Abre una partida nueva o recupera una guardada antes de mostrar el equipo. */
public class ControladorInicio {
    private final PantallaInicio vista;
    private final PersistenciaPartida persistencia = new PersistenciaPartida();

    public ControladorInicio(PantallaInicio vista) {
        this.vista = vista;
    }

    public void nuevaPartida() {
        abrirPartida(new Partida());
    }

    public void cargarPartida() {
        String ruta = vista.elegirRutaCarga();
        if (ruta == null) {
            return;
        }
        try {
            Partida partida = persistencia.cargar(ruta);
            abrirPartida(partida);
        } catch (IOException | ClassNotFoundException | IllegalArgumentException error) {
            // Si falla la carga, el menú continúa abierto para volver a intentar.
            vista.mostrarMensaje("No se pudo cargar la partida. " + error.getMessage());
        }
    }

    public void mostrarAyuda() {
        vista.mostrarAyuda();
    }

    public void salir() {
        vista.dispose();
    }

    private void abrirPartida(Partida partida) {
        PantallaEquipo pantalla = new PantallaEquipo();
        ControladorEquipo controlador = new ControladorEquipo(partida, pantalla);
        pantalla.conectarControlador(controlador);
        controlador.refrescarVista();
        pantalla.setVisible(true);
        vista.dispose();
    }
}
