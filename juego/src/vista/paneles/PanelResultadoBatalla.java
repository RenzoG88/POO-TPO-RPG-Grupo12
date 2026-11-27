package vista.paneles;

import dto.EmpleadoDTO;
import dto.ResultadoBatallaDTO;
import javax.swing.*;
import java.awt.*;

/** Muestra el resumen acreditado y permite continuar o elegir la mejora del jefe. */
public class PanelResultadoBatalla extends JPanel {
    private final JButton continuar = new JButton();
    private final JButton mejorarExperiencia = new JButton("Mejorar experiencia (+20%)");
    private final JButton mejorarCreditos = new JButton("Mejorar créditos (+20%)");
    private static final Color FONDO = new Color(240, 243, 247);
    private static final Color TEXTO = new Color(32, 42, 57);
    private static final Color ROJO = new Color(180, 55, 64);
    private static final Color AZUL = new Color(47, 118, 200);

    public PanelResultadoBatalla(ResultadoBatallaDTO datos) {
        setLayout(new BorderLayout(0, 16));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));
        boolean derrota = datos.hayResultado() && !datos.esVictoria();
        Color acento = derrota ? ROJO : new Color(31, 115, 80);

        JPanel cabecera = columna(FONDO);
        cabecera.add(texto("RESULTADO · CICLO " + datos.getCiclo(), 12, true, acento));
        cabecera.add(Box.createVerticalStrut(6));
        String titulo = !datos.hayResultado() ? "Elegí tu mejora"
                : derrota ? "Fin de la partida"
                : datos.esJefe() ? "¡Jefe derrotado!" : "¡Victoria!";
        cabecera.add(texto(titulo, 30, true, TEXTO));
        cabecera.add(Box.createVerticalStrut(8));
        String mensaje = !datos.hayResultado()
                ? "Las recompensas ya se acreditaron. Falta elegir la mejora del siguiente ciclo."
                : derrota ? "No quedan empleados activos. Podés volver al equipo para cargar o empezar de nuevo."
                : "Recompensas acreditadas. Así quedó tu equipo al terminar el combate.";
        cabecera.add(texto(mensaje, 13, false, TEXTO));
        add(cabecera, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setOpaque(false);
        if (datos.hayResultado()) {
            JPanel premios = new JPanel(new GridLayout(1, 2, 14, 0));
            premios.setOpaque(false);
            premios.add(premio("CRÉDITOS PARA EL EQUIPO", "+" + datos.getCreditos(), acento));
            premios.add(premio("XP POR EMPLEADO ACTIVO", "+" + datos.getExperiencia(), acento));
            premios.setPreferredSize(new Dimension(600, 80));
            contenido.add(premios, BorderLayout.NORTH);
        }
        JPanel estadoEquipo = new JPanel(new BorderLayout(0, 10));
        estadoEquipo.setOpaque(false);
        estadoEquipo.add(texto("Estado del equipo", 18, true, TEXTO), BorderLayout.NORTH);
        JPanel empleados = new JPanel(new GridLayout(0, 2, 12, 12));
        empleados.setOpaque(false);
        for (EmpleadoDTO empleado : datos.getEmpleados()) {
            empleados.add(tarjetaEmpleado(empleado, datos));
        }
        estadoEquipo.add(empleados, BorderLayout.CENTER);
        String progreso = "Combates normales ganados: " + datos.getEstres() + "/" + datos.getLimiteEstres();
        if (datos.hayMejoraPendiente()) {
            progreso += " · Jefe derrotado · Mejora pendiente";
        } else if (!derrota && datos.getEstres() >= datos.getLimiteEstres()) {
            progreso += " · Próximo combate: jefe";
        }
        estadoEquipo.add(texto(progreso, 13, false, TEXTO), BorderLayout.SOUTH);
        JScrollPane scrollEquipo = new JScrollPane(estadoEquipo);
        scrollEquipo.setBorder(null);
        scrollEquipo.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollEquipo.getVerticalScrollBar().setUnitIncrement(16);
        scrollEquipo.getViewport().setBackground(FONDO);
        contenido.add(scrollEquipo, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);

        JPanel pie = columna(FONDO);
        if (datos.hayMejoraPendiente()) {
            pie.add(texto("Elegí una mejora para iniciar el próximo ciclo. Se aplica a futuras recompensas.",
                    13, false, TEXTO));
            pie.add(Box.createVerticalStrut(10));
        }
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.setAlignmentX(LEFT_ALIGNMENT);
        if (datos.hayMejoraPendiente()) {
            botones.add(mejorarExperiencia);
            botones.add(mejorarCreditos);
        }
        continuar.setText(datos.hayMejoraPendiente() ? "Elegir después"
                : derrota ? "Volver al equipo" : "Continuar");
        botones.add(continuar);
        pie.add(botones);
        add(pie, BorderLayout.SOUTH);
    }

    private JPanel tarjetaEmpleado(EmpleadoDTO empleado, ResultadoBatallaDTO resultado) {
        JPanel panel = columna(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 221, 231)),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        panel.add(texto(empleado.getNombre(), 17, true, TEXTO));
        panel.add(Box.createVerticalStrut(4));
        String estado = empleado.isDespedido() ? "Despedido · no recibe XP"
                : resultado.hayResultado() ? "Activo · +" + resultado.getExperiencia() + " XP recibida"
                : "Activo";
        panel.add(texto(estado, 12, true,
                empleado.isDespedido() ? ROJO : new Color(31, 115, 80)));
        panel.add(Box.createVerticalStrut(4));
        panel.add(texto("Nivel " + empleado.getNivel() + " · Experiencia "
                + empleado.getExperiencia() + "/" + empleado.getExperienciaSiguienteNivel(),
                13, false, TEXTO));
        panel.add(Box.createVerticalStrut(6));
        BarraRecurso vida = new BarraRecurso("Vida", empleado.getVidaActual(), empleado.getVidaMaxima(), ROJO);
        vida.setAlignmentX(LEFT_ALIGNMENT);
        fijarAltoBarra(vida);
        panel.add(vida);
        panel.add(Box.createVerticalStrut(4));
        BarraRecurso energia = new BarraRecurso("Energía", empleado.getEnergiaActual(), empleado.getEnergiaMaxima(), AZUL);
        energia.setAlignmentX(LEFT_ALIGNMENT);
        fijarAltoBarra(energia);
        panel.add(energia);
        return panel;
    }

    private static void fijarAltoBarra(BarraRecurso barra) {
        barra.setMinimumSize(new Dimension(60, 20));
        barra.setPreferredSize(new Dimension(120, 20));
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
    }

    private JPanel premio(String titulo, String valor, Color acento) {
        JPanel panel = columna(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        panel.add(texto(titulo, 11, true, TEXTO));
        panel.add(texto(valor, 28, true, acento));
        return panel;
    }

    private static JPanel columna(Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(color);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    private static JLabel texto(String texto, int tamano, boolean negrita, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", negrita ? Font.BOLD : Font.PLAIN, tamano));
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        return etiqueta;
    }

    public void conectarContinuar(Runnable accion) {
        continuar.addActionListener(evento -> accion.run());
    }

    public void conectarMejoras(Runnable experiencia, Runnable creditos) {
        mejorarExperiencia.addActionListener(evento -> experiencia.run());
        mejorarCreditos.addActionListener(evento -> creditos.run());
    }
}
