import controlador.ControladorEquipo;
import modelo.partida.Partida;
import vista.PantallaEquipo;

import javax.swing.SwingUtilities;

public class InicioSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Partida partida = new Partida();

            PantallaEquipo pantalla = new PantallaEquipo();

            ControladorEquipo controlador =
                    new ControladorEquipo(partida, pantalla);

            pantalla.conectarControlador(controlador);

            controlador.refrescarVista();
            pantalla.setVisible(true);
        });
    }
}
