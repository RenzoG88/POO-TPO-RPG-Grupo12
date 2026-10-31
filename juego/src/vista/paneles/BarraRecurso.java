package vista.paneles;

import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/** Barra compartida por preparación y batalla, con texto legible en ambas zonas. */
public class BarraRecurso extends JProgressBar {
    public BarraRecurso(String nombre, int actual, int maximo, Color color) {
        super(0, Math.max(1, maximo));

        // Fijamos el aspecto para que el tema de macOS no cambie los colores.
        setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE; // Texto sobre la parte coloreada.
            }

            @Override
            protected Color getSelectionBackground() {
                return new Color(25, 30, 40); // Texto sobre la parte vacía.
            }
        });

        setForeground(color);
        setBackground(new Color(235, 238, 242));
        setOpaque(true);
        setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 11));
        setPreferredSize(new Dimension(120, 24));
        setValue(actual);
        setStringPainted(true);
        setString(nombre + ": " + actual + "/" + maximo);
        setToolTipText(getString());
    }
}
