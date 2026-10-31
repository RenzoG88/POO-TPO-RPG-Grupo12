package vista.paneles;

import dto.ObjetoDTO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

/** Tarjetas compartidas por tienda e inventario; no aplica reglas del juego. */
public class PanelObjetos extends JPanel {
    private final JLabel saldo = new JLabel();
    private final JLabel detalle = new JLabel();
    private final JLabel mensaje = new JLabel(" ");
    private final JPanel contenido = new JPanel(new BorderLayout());
    private final Map<String, ImageIcon> imagenes = new HashMap<>();
    private IntConsumer alElegir = indice -> { };

    public PanelObjetos(String titulo) {
        setLayout(new BorderLayout(12, 14));
        setBackground(new Color(240, 243, 247));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));
        setPreferredSize(new Dimension(900, 610));

        JPanel cabecera = new JPanel(new BorderLayout(12, 8));
        cabecera.setOpaque(false);
        JLabel nombre = new JLabel(titulo);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 25));
        saldo.setFont(new Font("SansSerif", Font.BOLD, 16));
        saldo.setForeground(new Color(40, 95, 155));
        detalle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cabecera.add(nombre, BorderLayout.WEST);
        cabecera.add(saldo, BorderLayout.EAST);
        cabecera.add(detalle, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        contenido.setOpaque(false);
        JScrollPane desplazamiento = new JScrollPane(contenido);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getViewport().setBackground(getBackground());
        desplazamiento.getVerticalScrollBar().setUnitIncrement(20);
        desplazamiento.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(desplazamiento, BorderLayout.CENTER);
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 12));
        mensaje.setForeground(new Color(40, 95, 155));
        add(mensaje, BorderLayout.SOUTH);
    }

    public void conectarAccion(IntConsumer alElegir) {
        this.alElegir = alElegir;
    }

    public void mostrarMensaje(String texto) {
        mensaje.setText(texto);
    }

    public void actualizar(List<ObjetoDTO> objetos, int creditos, String explicacion) {
        saldo.setText("Créditos: " + creditos);
        detalle.setText(explicacion);
        contenido.removeAll();
        if (objetos.isEmpty()) {
            JLabel vacio = new JLabel("No hay objetos disponibles. Podés conseguirlos en la tienda.",
                    SwingConstants.CENTER);
            vacio.setForeground(Color.DARK_GRAY);
            contenido.add(vacio, BorderLayout.CENTER);
        } else {
            JPanel tarjetas = new JPanel(new GridLayout(0, 2, 12, 12));
            tarjetas.setOpaque(false);
            for (int i = 0; i < objetos.size(); i++) {
                tarjetas.add(crearTarjeta(objetos.get(i), i));
            }
            contenido.add(tarjetas, BorderLayout.NORTH);
        }
        revalidate();
        repaint();
    }

    private JPanel crearTarjeta(ObjetoDTO dato, int indice) {
        JPanel tarjeta = new JPanel(new BorderLayout(14, 10));
        tarjeta.setPreferredSize(new Dimension(410, 218));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        JLabel nombre = new JLabel(dato.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 17));
        tarjeta.add(nombre, BorderLayout.NORTH);
        ImageIcon icono = obtenerImagen(dato.getImagen());
        JLabel dibujo = new JLabel(icono);
        if (icono == null) { dibujo.setText("Objeto"); }
        dibujo.setHorizontalAlignment(SwingConstants.CENTER);
        dibujo.setPreferredSize(new Dimension(112, 112));
        tarjeta.add(dibujo, BorderLayout.WEST);

        JPanel informacion = new JPanel(new GridLayout(3, 1, 0, 6));
        informacion.setOpaque(false);
        JLabel descripcion = new JLabel(dato.getDescripcion());
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        informacion.add(descripcion);
        JLabel precio = new JLabel(dato.getPrecio() < 0 ? "En tu inventario"
                : dato.getPrecio() + " créditos");
        precio.setFont(new Font("SansSerif", Font.BOLD, 16));
        informacion.add(precio);
        JLabel estado = new JLabel(dato.getEstado());
        estado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        estado.setForeground(dato.isHabilitado() ? new Color(35, 125, 80) : Color.GRAY);
        informacion.add(estado);
        tarjeta.add(informacion, BorderLayout.CENTER);

        JButton accion = new JButton(dato.getAccion());
        accion.setEnabled(dato.isHabilitado());
        accion.addActionListener(evento -> alElegir.accept(indice));
        tarjeta.add(accion, BorderLayout.SOUTH);
        return tarjeta;
    }

    private ImageIcon obtenerImagen(String archivo) {
        if (!imagenes.containsKey(archivo)) {
            URL ruta = getClass().getResource("/recursos/objetos/" + archivo);
            ImageIcon icono = null;
            if (ruta != null) {
                ImageIcon original = new ImageIcon(ruta);
                if (original.getIconWidth() > 0 && original.getIconHeight() > 0) {
                    double escala = Math.min(112.0 / original.getIconWidth(),
                            112.0 / original.getIconHeight());
                    icono = new ImageIcon(original.getImage().getScaledInstance(
                            Math.max(1, (int) (original.getIconWidth() * escala)),
                            Math.max(1, (int) (original.getIconHeight() * escala)), Image.SCALE_SMOOTH));
                }
            }
            imagenes.put(archivo, icono);
        }
        return imagenes.get(archivo);
    }
}
