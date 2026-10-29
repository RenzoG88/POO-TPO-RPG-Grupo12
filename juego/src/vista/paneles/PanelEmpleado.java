package vista.paneles;

import dto.EmpleadoDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Locale;

public class PanelEmpleado extends JPanel {

    public PanelEmpleado(EmpleadoDTO empleado, ActionListener accionEquipamiento) {
        setLayout(new BorderLayout(12, 8));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        add(crearImagen(empleado.getNombre()), BorderLayout.WEST);

        JPanel datos = new JPanel(new GridLayout(8, 1, 0, 4));
        datos.setOpaque(false);

        JLabel nombre = new JLabel(empleado.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 18));
        datos.add(nombre);

        datos.add(new JLabel("Nivel " + empleado.getNivel()
                + " | Experiencia: " + empleado.getExperiencia()));

        JLabel estado = new JLabel(
                empleado.isDespedido() ? "Despedido" : "Activo"
        );
        estado.setForeground(empleado.isDespedido()
                ? new Color(170, 40, 40) : new Color(30, 110, 65));
        datos.add(estado);

        datos.add(new JLabel("Ataque: " + empleado.getAtaque()
                + " | Defensa: " + empleado.getDefensa()));

        JLabel herramienta = new JLabel("Herramienta: " + empleado.getHerramienta());
        herramienta.setToolTipText(empleado.getHerramienta());
        datos.add(herramienta);

        JLabel proteccion = new JLabel("Proteccion: " + empleado.getProteccion());
        proteccion.setToolTipText(empleado.getProteccion());
        datos.add(proteccion);

        datos.add(crearBarra("Vida", empleado.getVidaActual(),
                empleado.getVidaMaxima(), new Color(55, 150, 90)));

        datos.add(crearBarra("Energia", empleado.getEnergiaActual(),
                empleado.getEnergiaMaxima(), new Color(55, 125, 200)));

        add(datos, BorderLayout.CENTER);

        JButton botonEquipamiento = new JButton("Equipamiento");
        botonEquipamiento.addActionListener(accionEquipamiento);
        add(botonEquipamiento, BorderLayout.SOUTH);
    }

    private JLabel crearImagen(String nombreEmpleado) {
        String archivo = nombreEmpleado.toLowerCase(Locale.ROOT) + ".png";
        String ruta = "/recursos/personajes/" + archivo;
        URL recurso = PanelEmpleado.class.getResource(ruta);

        JLabel imagen = new JLabel();
        imagen.setHorizontalAlignment(SwingConstants.CENTER);
        imagen.setPreferredSize(new Dimension(120, 180));

        if (recurso == null) {
            imagen.setText("Sin imagen");
            imagen.setToolTipText("No se encontro: " + ruta);
            return imagen;
        }

        ImageIcon original = new ImageIcon(recurso);
        Image ajustada = original.getImage()
                .getScaledInstance(120, 180, Image.SCALE_SMOOTH);

        imagen.setIcon(new ImageIcon(ajustada));
        imagen.setToolTipText(nombreEmpleado);
        return imagen;
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

