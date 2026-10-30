import controlador.ControladorEquipo;
import modelo.combatientes.Empleado;
import modelo.partida.Partida;
import vista.PantallaEquipo;

import javax.swing.SwingUtilities;

public class PruebaTiendaSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Partida partida = new Partida();

            // Datos exclusivos de esta prueba, para comprar y usar consumibles.
            partida.obtenerEquipo().obtenerInventario().agregarCreditos(200);
            Empleado empleado = partida.obtenerEquipo().obtenerEmpleados().get(0);
            empleado.recibirDanio(40);
            empleado.gastarEnergia(25);

            PantallaEquipo pantalla = new PantallaEquipo();
            pantalla.setTitle("RPG informatico - Prueba de tienda");
            ControladorEquipo controlador = new ControladorEquipo(partida, pantalla);
            pantalla.conectarControlador(controlador);
            controlador.refrescarVista();
            pantalla.setVisible(true);
        });
    }
}
