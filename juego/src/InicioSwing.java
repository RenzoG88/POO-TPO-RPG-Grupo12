import controlador.ControladorInicio;
import vista.organizador.PantallaInicio;

import javax.swing.SwingUtilities;

public class InicioSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PantallaInicio pantalla = new PantallaInicio();
            ControladorInicio controlador = new ControladorInicio(pantalla);
            pantalla.conectarControlador(controlador);
            pantalla.setVisible(true);
        });
    }
}
