package vista;

import controlador.ControladorEquipo;
import dto.EmpleadoDTO;
import vista.paneles.PanelEmpleado;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PantallaEquipo extends JFrame {

    private final JLabel etiquetaCreditos;
    private final JPanel panelEmpleados;
    private ControladorEquipo controlador;

    public PantallaEquipo() {
        setTitle("RPG informatico - Equipo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 760);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(16, 16));
        principal.setBackground(new Color(240, 243, 247));
        principal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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

    public void conectarControlador(ControladorEquipo controlador) {
        this.controlador = controlador;
    }

    public void mostrarEquipo(List<EmpleadoDTO> empleados, int creditos) {
        etiquetaCreditos.setText("Creditos: " + creditos);
        panelEmpleados.removeAll();

        for (int i = 0; i < empleados.size(); i++) {
            final int indiceEmpleado = i;

            panelEmpleados.add(new PanelEmpleado(empleados.get(i), evento -> {
                if (controlador != null) {
                    controlador.gestionarEquipamiento(indiceEmpleado);
                }
            }));
        }

        panelEmpleados.revalidate();
        panelEmpleados.repaint();
    }

    public int elegirAccionEquipamiento(String nombreEmpleado) {
        String[] opciones = {
                "Equipar objeto", "Quitar herramienta", "Quitar proteccion"
        };

        return JOptionPane.showOptionDialog(
                this,
                "Elegi una accion para " + nombreEmpleado + ":",
                "Equipamiento",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
    }

    public int elegirObjeto(String nombreEmpleado, String[] opciones) {
        JComboBox<String> selector = new JComboBox<>(opciones);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.add(new JLabel("Elegi un objeto del inventario:"), BorderLayout.NORTH);
        contenido.add(selector, BorderLayout.CENTER);

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                contenido,
                "Equipar a " + nombreEmpleado,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (respuesta == JOptionPane.OK_OPTION) {
            return selector.getSelectedIndex();
        }

        return -1;
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "Equipamiento", JOptionPane.INFORMATION_MESSAGE
        );
    }
}