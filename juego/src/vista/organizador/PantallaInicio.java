package vista.organizador;

import controlador.ControladorInicio;
import vista.paneles.PanelInicio;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class PantallaInicio extends JFrame {
    private ControladorInicio controlador;

    public PantallaInicio() {
        setTitle("RPG informático - Inicio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new PanelInicio(
                () -> { if (controlador != null) { controlador.nuevaPartida(); } },
                () -> { if (controlador != null) { controlador.cargarPartida(); } },
                () -> { if (controlador != null) { controlador.mostrarAyuda(); } },
                () -> { if (controlador != null) { controlador.salir(); } }
        ));
        pack();
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(null);
    }

    public void conectarControlador(ControladorInicio controlador) {
        this.controlador = controlador;
    }

    public String elegirRutaCarga() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Cargar partida");
        selector.setFileSelectionMode(JFileChooser.FILES_ONLY);
        selector.setFileFilter(new FileNameExtensionFilter("Partidas RPG (*.rpg)", "rpg"));
        selector.setAcceptAllFileFilterUsed(false);
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return selector.getSelectedFile().getAbsolutePath();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "RPG informático", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarAyuda() {
        String ayuda = "OBJETIVO\n"
                + "Ganá cinco combates normales y después enfrentá al jefe. Al vencerlo, "
                + "elegí una mejora de experiencia o créditos y comenzará otro ciclo.\n\n"
                + "PREPARACIÓN\n"
                + "Comprá objetos, equipá herramientas/protecciones y recuperá recursos con "
                + "snacks o café. Podés guardar y cargar la partida.\n\n"
                + "TURNOS Y DAÑO\n"
                + "Actúa primero el personaje con mayor velocidad. Tenés una acción por turno. "
                + "Atacar causa ataque total menos defensa total del objetivo, con un mínimo de 1. "
                + "Las herramientas suman ataque y las protecciones suman defensa. "
                + "Defender reduce el daño a la mitad hasta el próximo turno propio.\n\n"
                + "HABILIDADES: 10 DE ENERGÍA POR USO\n"
                + "Ciberseguridad: Firewall activo reduce un 25% el daño de un aliado o de sí mismo.\n"
                + "Infraestructura: Reiniciar servicio recupera hasta 30 de vida de un aliado activo.\n"
                + "Backend: Proceso pesado da un golpe con ataque multiplicado por 1,8.\n"
                + "Frontend: Doble renderizado da dos golpes con ataque multiplicado por 0,8 cada uno.\n"
                + "En cada golpe se redondea el ataque hacia abajo y se resta la defensa.\n\n"
                + "ESTADOS\n"
                + "Burnout: daño base de 5 al inicio de 3 turnos propios; Firewall puede reducirlo.\n"
                + "Ticket pendiente: impide actuar durante 1 turno propio.\n"
                + "Firewall activo: dura 2 cierres de turno del afectado.\n"
                + "La duración baja al cerrar el turno del afectado: al aplicárselo a sí mismo, "
                + "el turno actual ya cuenta. Reaplicar un estado suma duración. "
                + "Todos se limpian al terminar el combate.\n\n"
                + "ENEMIGOS Y CICLOS\n"
                + "Los rivales apuntan al primer empleado activo: Ciberseguridad, Infraestructura, "
                + "Backend y Frontend, en ese orden.\n"
                + "Phishing ataca; Cliente exigente alterna ataque y burnout; Burócrata alterna "
                + "ataque y ticket; el jefe alterna golpe potente y burnout.\n"
                + "Vida, ataque y defensa enemigas aumentan 15% de su valor base por ciclo "
                + "adicional: 100%, 115%, 130%... El resultado se redondea hacia arriba.\n\n"
                + "CUIDÁ AL EQUIPO\n"
                + "La vida y energía se conservan entre combates. Con vida 0, el empleado queda "
                + "despedido y no puede revivir. Podés recuperar su equipamiento entre combates. "
                + "Si todos quedan despedidos, la partida termina.\n\n"
                + "CONSEJO\n"
                + "Concentrá los ataques en un enemigo y usá la curación de Infraestructura "
                + "cuando un compañero esté herido. Podés comprar consumibles con las recompensas.";
        JTextArea texto = new JTextArea(ayuda);
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        texto.setMargin(new Insets(12, 12, 12, 12));
        texto.setCaretPosition(0);
        JScrollPane panel = new JScrollPane(texto);
        panel.setPreferredSize(new Dimension(660, 480));
        JOptionPane.showMessageDialog(this, panel, "Cómo jugar", JOptionPane.PLAIN_MESSAGE);
    }
}
