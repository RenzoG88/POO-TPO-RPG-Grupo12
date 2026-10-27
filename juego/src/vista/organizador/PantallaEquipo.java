package vista;

import dto.EmpleadoDTO;
import vista.paneles.PanelEmpleado;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PantallaEquipo extends JFrame {

    private final JLabel etiquetaCreditos;
    private final JPanel panelEmpleados;

    public PantallaEquipo() {
        setTitle("RPG informatico - Equipo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 540);
        setMinimumSize(new Dimension(720, 460));
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(16, 16));
        principal.setBackground(new Color(240, 243, 247));
        principal.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        setContentPane(principal);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JLabel titulo = new JLabel("Equipo informatico");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        cabecera.add(titulo, BorderLayout.WEST);

        etiquetaCreditos = new JLabel("Creditos: 0");
        cabecera.add(etiquetaCreditos, BorderLayout.EAST);

        principal.add(cabecera, BorderLayout.NORTH);

        panelEmpleados = new JPanel(new GridLayout(2, 2, 16, 16));
        panelEmpleados.setOpaque(false);

        principal.add(panelEmpleados, BorderLayout.CENTER);
    }

    public void mostrarEquipo(List<EmpleadoDTO> empleados, int creditos) {
        etiquetaCreditos.setText("Creditos: " + creditos);

        panelEmpleados.removeAll();

        for (EmpleadoDTO empleado : empleados) {
            panelEmpleados.add(new PanelEmpleado(empleado));
        }

        panelEmpleados.revalidate();
        panelEmpleados.repaint();
    }
}