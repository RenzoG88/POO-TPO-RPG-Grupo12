import controlador.ControladorEquipo;
import modelo.combatientes.Equipo;
import modelo.inventario.Herramienta;
import modelo.inventario.Proteccion;
import vista.PantallaEquipo;

import javax.swing.SwingUtilities;

public class PruebaEquipoSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Equipo equipo = new Equipo();

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

            ControladorEquipo controlador = new ControladorEquipo(equipo, pantalla);
            pantalla.conectarControlador(controlador);
            controlador.refrescarVista();

            pantalla.setVisible(true);
        });
    }
}