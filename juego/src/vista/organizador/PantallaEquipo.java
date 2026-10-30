package vista;

import controlador.ControladorEquipo;
import dto.EmpleadoDTO;
import vista.paneles.PanelEmpleado;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;

public class PantallaEquipo extends JFrame {

    private final JLabel etiquetaCreditos;
    private final JLabel etiquetaProgreso;
    private final JPanel panelEmpleados;
    private ControladorEquipo controlador;

    public PantallaEquipo() {
        setTitle("RPG informatico - Preparacion");
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

        JLabel titulo = new JLabel("Preparacion del equipo");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        cabecera.add(titulo, BorderLayout.WEST);

        etiquetaCreditos = new JLabel("Creditos: 0");
        cabecera.add(etiquetaCreditos, BorderLayout.EAST);

        etiquetaProgreso = new JLabel();
        etiquetaProgreso.setFont(new Font("SansSerif", Font.PLAIN, 14));
        etiquetaProgreso.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        cabecera.add(etiquetaProgreso, BorderLayout.SOUTH);
        principal.add(cabecera, BorderLayout.NORTH);

        panelEmpleados = new JPanel(new GridLayout(2, 2, 16, 16));
        panelEmpleados.setOpaque(false);
        principal.add(panelEmpleados, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.setOpaque(false);

        JButton botonGuardar = new JButton("Guardar partida");
        botonGuardar.addActionListener(evento -> {
            if (controlador != null) {
                controlador.guardarPartida();
            }
        });

        JButton botonCargar = new JButton("Cargar partida");
        botonCargar.addActionListener(evento -> {
            if (controlador != null) {
                controlador.cargarPartida();
            }
        });

        JButton botonTienda = new JButton("Tienda");
        botonTienda.addActionListener(evento -> {
            if (controlador != null) {
                controlador.abrirTienda();
            }
        });
        JButton botonInventario = new JButton("Inventario");
        botonInventario.addActionListener(evento -> {
            if (controlador != null) {
                controlador.mostrarInventario();
            }
        });
        JButton botonConsumible = new JButton("Usar consumible");
        botonConsumible.addActionListener(evento -> {
            if (controlador != null) {
                controlador.gestionarConsumibles();
            }
        });
        acciones.add(botonTienda);
        acciones.add(botonInventario);
        acciones.add(botonConsumible);
        acciones.add(botonGuardar);
        acciones.add(botonCargar);
        principal.add(acciones, BorderLayout.SOUTH);
    }

    public void conectarControlador(ControladorEquipo controlador) {
        this.controlador = controlador;
    }

    public void mostrarProgreso(int ciclo, int estres, int limiteEstres) {
        etiquetaProgreso.setText("Ciclo: " + ciclo + " | Estres: "
                + estres + "/" + limiteEstres);
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

    public int elegirOferta(String[] opciones, int creditos) {
        return elegirOpcion("Tienda", "Creditos: " + creditos + ". Elegi una oferta:", opciones);
    }

    public int elegirConsumible(String[] opciones) {
        return elegirOpcion("Consumibles", "Elegi el objeto que queres usar:", opciones);
    }

    public int elegirEmpleado(String[] nombres) {
        return elegirOpcion("Consumibles", "Elegi el empleado que lo recibira:", nombres);
    }

    private int elegirOpcion(String titulo, String mensaje, String[] opciones) {
        JComboBox<String> selector = new JComboBox<>(opciones);
        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.add(new JLabel(mensaje), BorderLayout.NORTH);
        contenido.add(selector, BorderLayout.CENTER);

        int respuesta = JOptionPane.showConfirmDialog(
                this, contenido, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        return respuesta == JOptionPane.OK_OPTION ? selector.getSelectedIndex() : -1;
    }

    public void mostrarInventario(String[] items, int creditos) {
        String[] contenido = items.length == 0 ? new String[]{"Inventario vacio"} : items;
        JList<String> lista = new JList<>(contenido);
        JScrollPane desplazamiento = new JScrollPane(lista);
        desplazamiento.setPreferredSize(new Dimension(520, 260));
        JOptionPane.showMessageDialog(this, desplazamiento,
                "Inventario | Creditos: " + creditos, JOptionPane.PLAIN_MESSAGE);
    }

    public String elegirRutaGuardado() {
        JFileChooser selector = crearSelectorArchivos();
        selector.setDialogTitle("Guardar partida");
        selector.setSelectedFile(new File("partida.rpg"));

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith(".rpg")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".rpg");
        }

        if (archivo.exists()) {
            int respuesta = JOptionPane.showConfirmDialog(
                    this, "El archivo ya existe. ¿Queres reemplazarlo?",
                    "Guardar partida", JOptionPane.YES_NO_OPTION
            );

            if (respuesta != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return archivo.getAbsolutePath();
    }

    public String elegirRutaCarga() {
        JFileChooser selector = crearSelectorArchivos();
        selector.setDialogTitle("Cargar partida");

        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return selector.getSelectedFile().getAbsolutePath();
    }

    public boolean confirmarCarga() {
        return JOptionPane.showConfirmDialog(
                this,
                "Cargar reemplazara la partida abierta y los cambios sin guardar. ¿Continuar?",
                "Cargar partida",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }

    private JFileChooser crearSelectorArchivos() {
        JFileChooser selector = new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.FILES_ONLY);
        selector.setFileFilter(new FileNameExtensionFilter("Partidas RPG (*.rpg)", "rpg"));
        selector.setAcceptAllFileFilterUsed(false);
        return selector;
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "RPG informatico", JOptionPane.INFORMATION_MESSAGE
        );
    }
}
