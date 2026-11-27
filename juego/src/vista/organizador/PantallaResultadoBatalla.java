package vista.organizador;

import dto.ResultadoBatallaDTO;
import vista.paneles.PanelResultadoBatalla;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Dimension;

/** Pantalla de resultados con contenido propio, en lugar de un mensaje emergente. */
public class PantallaResultadoBatalla extends JDialog {
    private final PanelResultadoBatalla panel;

    public PantallaResultadoBatalla(JFrame propietario, ResultadoBatallaDTO datos) {
        super(propietario, "RPG informático - Resultado de batalla", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        panel = new PanelResultadoBatalla(datos);
        panel.conectarContinuar(this::dispose);
        setContentPane(panel);
        setSize(940, 760);
        setMinimumSize(new Dimension(820, 650));
        setLocationRelativeTo(propietario);
    }

    public void conectarMejoras(Runnable experiencia, Runnable creditos) {
        panel.conectarMejoras(experiencia, creditos);
    }
}
