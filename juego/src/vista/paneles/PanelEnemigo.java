package vista.paneles;

import dto.EnemigoDTO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.net.URL;

/** Muestra datos del enemigo. Las decisiones de combate pertenecen al controlador/modelo. */
public class PanelEnemigo extends JPanel {
    private final JLabel imagen = new JLabel();
    private final JLabel nombre = new JLabel();
    private final JLabel estado = new JLabel();
    private final JLabel estadisticas = new JLabel();
    private final JLabel velocidad = new JLabel();
    private final JProgressBar vida = crearBarra(new Color(181, 63, 66));
    private final JProgressBar energia = crearBarra(new Color(55, 125, 200));
    private String archivoMostrado;

    public PanelEnemigo(EnemigoDTO enemigo) {
        setLayout(new BorderLayout(18, 8));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        imagen.setPreferredSize(new Dimension(140, 210));
        imagen.setHorizontalAlignment(SwingConstants.CENTER);
        add(imagen, BorderLayout.WEST);

        JPanel datos = new JPanel(new GridLayout(6, 1, 0, 6));
        datos.setOpaque(false);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 18));
        datos.add(nombre);
        datos.add(estado);
        datos.add(estadisticas);
        datos.add(velocidad);
        datos.add(vida);
        datos.add(energia);
        add(datos, BorderLayout.CENTER);

        actualizar(enemigo);
    }

    // El controlador puede enviar un DTO actualizado despues de cada accion.
    public void actualizar(EnemigoDTO enemigo) {
        nombre.setText(enemigo.getNombre());
        nombre.setToolTipText(enemigo.getNombre());
        boolean activo = enemigo.getVidaActual() > 0;
        estado.setText(activo ? "Activo" : "Derrotado");
        estado.setForeground(activo ? new Color(155, 65, 35) : Color.GRAY);
        estadisticas.setText("Ataque: " + enemigo.getAtaque()
                + " | Defensa: " + enemigo.getDefensa());
        velocidad.setText("Velocidad: " + enemigo.getVelocidad());
        actualizarBarra(vida, "Vida", enemigo.getVidaActual(), enemigo.getVidaMaxima());
        actualizarBarra(energia, "Energía", enemigo.getEnergiaActual(), enemigo.getEnergiaMaxima());

        if (!enemigo.getArchivoImagen().equals(archivoMostrado)) {
            cargarImagen(enemigo.getArchivoImagen());
        }
    }

    private void cargarImagen(String archivo) {
        String ruta = "/recursos/personajes/" + archivo;
        URL recurso = PanelEnemigo.class.getResource(ruta);
        archivoMostrado = archivo;
        imagen.setIcon(null);
        imagen.setText("");

        if (recurso == null) {
            imagen.setText("Sin imagen");
            imagen.setToolTipText("No se encontró: " + ruta);
            return;
        }

        ImageIcon original = new ImageIcon(recurso);
        if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
            imagen.setText("Sin imagen");
            imagen.setToolTipText("No se pudo leer: " + ruta);
            return;
        }

        // Entra en 140 x 210 sin deformar el personaje.
        double escala = Math.min(140.0 / original.getIconWidth(),
                210.0 / original.getIconHeight());
        int ancho = Math.max(1, (int) Math.round(original.getIconWidth() * escala));
        int alto = Math.max(1, (int) Math.round(original.getIconHeight() * escala));
        Image ajustada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        imagen.setIcon(new ImageIcon(ajustada));
        imagen.setToolTipText(nombre.getText());
    }

    private JProgressBar crearBarra(Color color) {
        JProgressBar barra = new JProgressBar();
        // Mantiene una barra legible tambien con la apariencia nativa de macOS.
        barra.setUI(new BasicProgressBarUI());
        barra.setStringPainted(true);
        barra.setForeground(color);
        barra.setBackground(new Color(235, 238, 242));
        barra.setFont(new Font("SansSerif", Font.BOLD, 12));
        return barra;
    }

    private void actualizarBarra(JProgressBar barra, String texto, int actual, int maximo) {
        barra.setMaximum(Math.max(1, maximo));
        barra.setValue(actual);
        barra.setString(texto + ": " + actual + "/" + maximo);
    }
}
