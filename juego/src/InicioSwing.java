import controlador.ControladorEquipo;
import modelo.combatientes.Equipo;
import vista.PantallaEquipo;

import javax.swing.SwingUtilities;

public class InicioSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Equipo equipo = new Equipo();

            PantallaEquipo pantalla = new PantallaEquipo();

            ControladorEquipo controlador =
                    new ControladorEquipo(equipo, pantalla);

            // Esta es la línea nueva:
            pantalla.conectarControlador(controlador);

            controlador.refrescarVista();
            pantalla.setVisible(true);
        });
    }
}