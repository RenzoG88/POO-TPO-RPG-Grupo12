package vista.paneles;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/** Composición visual del menú. Los botones delegan sus acciones al controlador. */
public class PanelInicio extends JPanel {
    public PanelInicio(Runnable nuevaPartida, Runnable cargarPartida,
                       Runnable comoJugar, Runnable salir) {
        setLayout(new BorderLayout(20, 20));
        setPreferredSize(new Dimension(880, 600));
        setBackground(new Color(240, 243, 247));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

        JPanel cabecera = new JPanel(new GridLayout(2, 1, 0, 4));
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("RPG INFORMÁTICO");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        titulo.setForeground(new Color(28, 48, 74));
        JLabel subtitulo = new JLabel("Sobrevivir también es un proyecto.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitulo.setForeground(new Color(75, 90, 110));
        cabecera.add(titulo);
        cabecera.add(subtitulo);
        add(cabecera, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(24, 0));
        centro.setOpaque(false);
        JPanel equipo = new JPanel(new GridLayout(2, 2, 12, 12));
        equipo.setOpaque(false);
        equipo.add(crearPersonaje("Ciberseguridad", "ciberseguridad.png", "Protege al equipo"));
        equipo.add(crearPersonaje("Infraestructura", "infraestructura.png", "Recupera vida"));
        equipo.add(crearPersonaje("Backend", "backend.png", "Golpe potente"));
        equipo.add(crearPersonaje("Frontend", "frontend.png", "Ataque doble"));
        centro.add(equipo, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new GridLayout(4, 1, 0, 14));
        acciones.setOpaque(false);
        acciones.setPreferredSize(new Dimension(230, 300));
        acciones.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        acciones.add(crearBoton("Nueva partida", nuevaPartida));
        acciones.add(crearBoton("Cargar partida", cargarPartida));
        acciones.add(crearBoton("Cómo jugar", comoJugar));
        acciones.add(crearBoton("Salir", salir));
        centro.add(acciones, BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        JLabel pie = new JLabel("Superá cinco combates, enfrentá al jefe y mejorá tu equipo para el próximo ciclo.");
        pie.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pie.setForeground(new Color(75, 90, 110));
        add(pie, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.addActionListener(evento -> accion.run());
        return boton;
    }

    private JPanel crearPersonaje(String nombre, String archivo, String funcion) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 4));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JLabel titulo = new JLabel(nombre, SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        tarjeta.add(titulo, BorderLayout.NORTH);

        JLabel imagen = new JLabel("", SwingConstants.CENTER);
        URL recurso = getClass().getResource("/recursos/personajes/" + archivo);
        if (recurso != null) {
            ImageIcon original = new ImageIcon(recurso);
            if (original.getIconWidth() > 0 && original.getIconHeight() > 0) {
                double escala = Math.min(80.0 / original.getIconWidth(),
                        120.0 / original.getIconHeight());
                imagen.setIcon(new ImageIcon(original.getImage().getScaledInstance(
                        Math.max(1, (int) (original.getIconWidth() * escala)),
                        Math.max(1, (int) (original.getIconHeight() * escala)), Image.SCALE_SMOOTH)));
            }
        }
        tarjeta.add(imagen, BorderLayout.CENTER);
        JLabel descripcion = new JLabel(funcion, SwingConstants.CENTER);
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descripcion.setForeground(new Color(75, 90, 110));
        tarjeta.add(descripcion, BorderLayout.SOUTH);
        return tarjeta;
    }
}
