package vista.organizador;

import vista.paneles.PanelObjetos;
import javax.swing.*;
import java.awt.*;

public class PantallaObjetos extends JDialog {
    private final PanelObjetos panel;

    public PantallaObjetos(JFrame propietario, String titulo) {
        super(propietario, titulo, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        panel = new PanelObjetos(titulo);
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(panel, BorderLayout.CENTER);
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.setBackground(new Color(240, 243, 247));
        JButton cerrar = new JButton("Volver al equipo");
        cerrar.addActionListener(evento -> dispose());
        pie.add(cerrar);
        contenido.add(pie, BorderLayout.SOUTH);
        setContentPane(contenido);
        pack();
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(propietario);
    }

    public PanelObjetos obtenerPanel() { return panel; }

    public int elegirEmpleado(String[] nombres) {
        if (nombres.length == 0) { return -1; }
        JComboBox<String> selector = new JComboBox<>(nombres);
        int respuesta = JOptionPane.showConfirmDialog(this, selector, "Elegí un empleado activo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return respuesta == JOptionPane.OK_OPTION ? selector.getSelectedIndex() : -1;
    }
}
