package vista.organizador;

import vista.paneles.PanelBatalla;
import javax.swing.*;
import java.awt.*;

/** Es modal: la preparación queda bloqueada mientras se está combatiendo. */
public class PantallaBatalla extends JDialog {
    private final PanelBatalla panel = new PanelBatalla();

    public PantallaBatalla(JFrame propietaria) {
        super(propietaria, "RPG informático - Batalla", true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setContentPane(panel);
        pack();
        setMinimumSize(new Dimension(1020, 650));
        setLocationRelativeTo(propietaria);
    }

    public PanelBatalla obtenerPanel() {
        return panel;
    }

    public int elegir(String titulo, String mensaje, String[] opciones) {
        JComboBox<String> selector = new JComboBox<>(opciones);
        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.add(new JLabel(mensaje), BorderLayout.NORTH);
        contenido.add(selector, BorderLayout.CENTER);
        int respuesta = JOptionPane.showConfirmDialog(this, contenido, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return respuesta == JOptionPane.OK_OPTION ? selector.getSelectedIndex() : -1;
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Batalla", JOptionPane.INFORMATION_MESSAGE);
    }
}
