import dto.EnemigoDTO;
import modelo.combatientes.Burocrata;
import modelo.combatientes.ClienteExigente;
import modelo.combatientes.Enemigo;
import modelo.combatientes.JefeEmpresa;
import modelo.combatientes.UsuarioPhishing;
import vista.paneles.PanelEnemigo;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/** Vista previa de los cuatro enemigos. No inicia ni simula una batalla. */
public class PruebaEnemigosSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("RPG informático - Enemigos");
            ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventana.setContentPane(crearContenido());
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }

    public static JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(16, 16));
        contenido.setBackground(new Color(240, 243, 247));
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contenido.setPreferredSize(new Dimension(1080, 630));

        JLabel titulo = new JLabel("Enemigos del RPG informático");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        contenido.add(titulo, BorderLayout.NORTH);

        JPanel tarjetas = new JPanel(new GridLayout(2, 2, 16, 16));
        tarjetas.setOpaque(false);
        Enemigo[] enemigos = {
                new UsuarioPhishing(), new ClienteExigente(),
                new Burocrata(), new JefeEmpresa()
        };

        for (Enemigo enemigo : enemigos) {
            tarjetas.add(new PanelEnemigo(crearDatos(enemigo)));
        }
        contenido.add(tarjetas, BorderLayout.CENTER);

        JLabel ciclo = new JLabel("Estadísticas iniciales · Ciclo 1");
        ciclo.setForeground(new Color(80, 90, 105));
        contenido.add(ciclo, BorderLayout.SOUTH);
        return contenido;
    }

    private static EnemigoDTO crearDatos(Enemigo enemigo) {
        // UsuarioPhishing -> usuariophishing.png, JefeEmpresa -> jefeempresa.png.
        String archivo = enemigo.getClass().getSimpleName().toLowerCase(Locale.ROOT) + ".png";
        return new EnemigoDTO(enemigo.obtenerNombre(), archivo,
                enemigo.obtenerVida(), enemigo.obtenerVidaMaxima(),
                enemigo.obtenerEnergia(), enemigo.obtenerEnergiaMaxima(),
                enemigo.obtenerAtaqueTotal(), enemigo.obtenerDefensaTotal(),
                enemigo.obtenerVelocidad());
    }
}
