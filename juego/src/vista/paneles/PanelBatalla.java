package vista.paneles;

import dto.CombatienteDTO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pantalla simple: muestra datos y avisa qué botón se presionó. */
public class PanelBatalla extends JPanel {
    private final JLabel titulo = new JLabel();
    private final JLabel turno = new JLabel();
    private final JPanel aliados = new JPanel(new GridLayout(2, 2, 8, 8));
    private final JPanel enemigos = new JPanel(new GridLayout(0, 1, 8, 8));
    private final JTextArea registro = new JTextArea(5, 60);
    private final JButton atacar = new JButton("Atacar");
    private final JButton defender = new JButton("Defender");
    private final JButton habilidad = new JButton("Habilidad");
    private final JButton objeto = new JButton("Objeto");
    private final Map<String, ImageIcon> imagenes = new HashMap<>();

    public PanelBatalla() {
        setLayout(new BorderLayout(12, 12));
        setPreferredSize(new Dimension(1120, 680));
        setBackground(new Color(240, 243, 247));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel cabecera = new JPanel(new GridLayout(2, 1, 0, 6));
        cabecera.setOpaque(false);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        turno.setFont(new Font("SansSerif", Font.BOLD, 16));
        cabecera.add(titulo);
        cabecera.add(turno);
        add(cabecera, BorderLayout.NORTH);

        JPanel bandos = new JPanel(new GridLayout(1, 2, 12, 0));
        bandos.setOpaque(false);
        aliados.setOpaque(false);
        enemigos.setOpaque(false);
        aliados.setBorder(BorderFactory.createTitledBorder("Tu equipo"));
        enemigos.setBorder(BorderFactory.createTitledBorder("Rivales"));
        bandos.add(aliados);
        bandos.add(enemigos);
        add(bandos, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(0, 8));
        inferior.setOpaque(false);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        acciones.setOpaque(false);
        acciones.add(atacar);
        acciones.add(defender);
        acciones.add(habilidad);
        acciones.add(objeto);
        inferior.add(acciones, BorderLayout.NORTH);

        registro.setEditable(false);
        registro.setLineWrap(true);
        registro.setWrapStyleWord(true);
        registro.setFont(new Font("SansSerif", Font.PLAIN, 12));
        inferior.add(new JScrollPane(registro), BorderLayout.CENTER);
        inferior.add(new JLabel("Una acción por turno. Volvés a preparación al terminar el combate."),
                BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);
        habilitarAcciones(false);
    }

    public void conectarAcciones(Runnable alAtacar, Runnable alDefender,
                                Runnable alUsarHabilidad, Runnable alUsarObjeto) {
        atacar.addActionListener(evento -> alAtacar.run());
        defender.addActionListener(evento -> alDefender.run());
        habilidad.addActionListener(evento -> alUsarHabilidad.run());
        objeto.addActionListener(evento -> alUsarObjeto.run());
    }

    public void actualizar(String titulo, String turno, List<CombatienteDTO> equipo,
                           List<CombatienteDTO> rivales, String registro, boolean puedeElegir) {
        this.titulo.setText(titulo);
        this.turno.setText(turno);
        aliados.removeAll();
        enemigos.removeAll();
        for (CombatienteDTO dato : equipo) {
            aliados.add(crearTarjeta(dato));
        }
        for (CombatienteDTO dato : rivales) {
            enemigos.add(crearTarjeta(dato));
        }
        if (!this.registro.getText().equals(registro)) {
            this.registro.setText(registro);
            this.registro.setCaretPosition(this.registro.getDocument().getLength());
        }
        habilitarAcciones(puedeElegir);
        revalidate();
        repaint();
    }

    private void habilitarAcciones(boolean habilitar) {
        atacar.setEnabled(habilitar);
        defender.setEnabled(habilitar);
        habilidad.setEnabled(habilitar);
        objeto.setEnabled(habilitar);
    }

    private JPanel crearTarjeta(CombatienteDTO dato) {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 6));
        tarjeta.setBackground(Color.WHITE);
        Color borde = dato.isTurnoActual() ? new Color(45, 115, 205) : new Color(210, 215, 225);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, dato.isTurnoActual() ? 3 : 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel nombre = new JLabel(dato.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        tarjeta.add(nombre, BorderLayout.NORTH);
        JLabel retrato = new JLabel(obtenerImagen(dato.getImagen()));
        retrato.setPreferredSize(new Dimension(72, 108));
        retrato.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(retrato, BorderLayout.WEST);

        JPanel datos = new JPanel(new GridLayout(4, 1, 0, 5));
        datos.setOpaque(false);
        String estado = dato.getVida() == 0 ? "Fuera de combate"
                : dato.isTurnoActual() ? "TURNO ACTUAL" : "Activo";
        JLabel etiquetaEstado = new JLabel(estado);
        etiquetaEstado.setForeground(dato.getVida() == 0 ? Color.GRAY : new Color(35, 95, 165));
        etiquetaEstado.setFont(new Font("SansSerif", Font.BOLD, 11));
        datos.add(etiquetaEstado);
        datos.add(new JLabel("ATQ " + dato.getAtaque() + " | DEF " + dato.getDefensa()));
        datos.add(crearBarra("Vida", dato.getVida(), dato.getVidaMaxima(), new Color(180, 60, 65)));
        datos.add(crearBarra("EN", dato.getEnergia(), dato.getEnergiaMaxima(), new Color(50, 120, 200)));
        tarjeta.add(datos, BorderLayout.CENTER);
        JLabel estados = new JLabel(dato.getEstados().isEmpty() ? "Sin estados" : dato.getEstados());
        estados.setFont(new Font("SansSerif", Font.PLAIN, 11));
        estados.setToolTipText(estados.getText());
        tarjeta.add(estados, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JProgressBar crearBarra(String nombre, int actual, int maximo, Color color) {
        JProgressBar barra = new JProgressBar(0, Math.max(1, maximo));
        barra.setUI(new BasicProgressBarUI());
        barra.setForeground(color);
        barra.setBackground(new Color(235, 238, 242));
        barra.setValue(actual);
        barra.setStringPainted(true);
        barra.setString(nombre + ": " + actual + "/" + maximo);
        barra.setFont(new Font("SansSerif", Font.BOLD, 11));
        return barra;
    }

    private ImageIcon obtenerImagen(String archivo) {
        if (!imagenes.containsKey(archivo)) {
            URL ruta = getClass().getResource("/recursos/personajes/" + archivo);
            ImageIcon icono = null;
            if (ruta != null) {
                ImageIcon original = new ImageIcon(ruta);
                if (original.getIconWidth() > 0 && original.getIconHeight() > 0) {
                    double escala = Math.min(72.0 / original.getIconWidth(),
                            108.0 / original.getIconHeight());
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
