package vista.paneles;

import dto.TurnoDTO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Mantiene las posiciones de la ronda y destaca al participante actual. */
public class PanelOrdenTurnos extends JPanel {
    public PanelOrdenTurnos() {
        setLayout(new GridLayout(1, 0, 6, 0));
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder("Orden de la ronda · mayor velocidad primero"));
        setPreferredSize(new Dimension(900, 86));
    }

    public void actualizar(List<TurnoDTO> turnos) {
        removeAll();
        for (TurnoDTO dato : turnos) {
            JPanel casilla = new JPanel(new GridLayout(3, 1, 0, 1));
            casilla.setBackground(dato.isActual() ? new Color(219, 235, 255) : Color.WHITE);
            casilla.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(dato.isActual()
                            ? new Color(45, 115, 205) : new Color(210, 215, 225), dato.isActual() ? 2 : 1),
                    BorderFactory.createEmptyBorder(3, 5, 3, 5)));
            JLabel nombre = new JLabel(dato.getPosicion() + ". " + dato.getNombre());
            nombre.setFont(new Font("SansSerif", Font.BOLD, 11));
            nombre.setToolTipText(nombre.getText());
            JLabel velocidad = new JLabel("Velocidad: " + dato.getVelocidad());
            velocidad.setFont(new Font("SansSerif", Font.PLAIN, 10));
            JLabel estado = new JLabel(dato.getEstado());
            estado.setFont(new Font("SansSerif", Font.BOLD, 10));
            estado.setForeground(dato.isActual() ? new Color(25, 85, 165) : Color.DARK_GRAY);
            casilla.add(nombre);
            casilla.add(velocidad);
            casilla.add(estado);
            add(casilla);
        }
        revalidate();
        repaint();
    }
}
