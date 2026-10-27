package vista.paneles;

import dto.EmpleadoDTO;

import javax.swing.*;
import java.awt.*;

public class PanelEmpleado extends JPanel {

    public PanelEmpleado(EmpleadoDTO empleado) {
        setLayout(new GridLayout(5, 1, 0, 8));
        setBackground(Color.WHITE);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel nombre = new JLabel(empleado.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(nombre);

        add(new JLabel("Nivel " + empleado.getNivel()
                + " | Experiencia: " + empleado.getExperiencia()));

        JLabel estado = new JLabel(
                empleado.isDespedido() ? "Despedido" : "Activo"
        );

        estado.setForeground(empleado.isDespedido()
                ? new Color(170, 40, 40)
                : new Color(30, 110, 65));

        add(estado);

        add(crearBarra("Vida", empleado.getVidaActual(),
                empleado.getVidaMaxima(), new Color(55, 150, 90)));

        add(crearBarra("Energia", empleado.getEnergiaActual(),
                empleado.getEnergiaMaxima(), new Color(55, 125, 200)));
    }

    private JProgressBar crearBarra(String nombre, int actual,
                                    int maximo, Color color) {
        JProgressBar barra = new JProgressBar(0, maximo);
        barra.setValue(actual);
        barra.setStringPainted(true);
        barra.setString(nombre + ": " + actual + "/" + maximo);
        barra.setForeground(color);
        return barra;
    }
}