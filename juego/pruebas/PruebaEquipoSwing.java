import controlador.ControladorEquipo;
import modelo.combatientes.Equipo;
import modelo.inventario.Herramienta;
import modelo.inventario.Proteccion;
import modelo.partida.Partida;
import vista.organizador.PantallaEquipo;

import javax.swing.SwingUtilities;

public class PruebaEquipoSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Partida partida = new Partida();
            Equipo equipo = partida.obtenerEquipo();

            // Objetos para probar la interfaz. No son regalos de una partida nueva.
            equipo.obtenerInventario().agregarItem(
                    new Herramienta("Teclado mecanico", 10)
            );
            equipo.obtenerInventario().agregarItem(
                    new Herramienta("Teclado mejorado", 20)
            );
            equipo.obtenerInventario().agregarItem(
                    new Proteccion("Auriculares aislantes", 5)
            );

            PantallaEquipo pantalla = new PantallaEquipo();
            pantalla.setTitle("RPG informatico - Prueba de equipamiento");

            ControladorEquipo controlador = new ControladorEquipo(partida, pantalla);
            pantalla.conectarControlador(controlador);
            controlador.refrescarVista();

            pantalla.setVisible(true);
        });
    }
}
