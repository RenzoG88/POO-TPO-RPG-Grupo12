package controlador;

import dto.ResultadoBatallaDTO;
import modelo.combate.Batalla;
import modelo.partida.EtapaPartida;
import modelo.partida.Partida;
import modelo.partida.ResultadoBatalla;
import modelo.partida.TipoMejora;
import vista.organizador.PantallaBatalla;
import vista.organizador.PantallaEquipo;
import vista.organizador.PantallaResultadoBatalla;
import javax.swing.SwingUtilities;

/** Coordina el recorrido preparacion -> combate -> resultado -> preparacion/mejora. */
public class ControladorPartida {
    private final SesionPartida sesion;
    private final PantallaEquipo vista;
    private final Runnable alCambiar;

    public ControladorPartida(SesionPartida sesion, PantallaEquipo vista, Runnable alCambiar) {
        this.sesion = sesion;
        this.vista = vista;
        this.alCambiar = alCambiar;
    }

    public void iniciarCombate() {
        Partida partida = sesion.obtenerPartida();
        if (partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA) {
            mostrarResultado(null); // Retoma una eleccion pendiente, sin volver a dar premios.
            return;
        }
        if (partida.obtenerEtapa() != EtapaPartida.PREPARACION) {
            vista.mostrarMensaje("Para volver a jugar, iniciá una nueva partida.");
            return;
        }
        Batalla batalla;
        try {
            batalla = partida.iniciarSiguienteCombate();
        } catch (IllegalStateException error) {
            alCambiar.run();
            vista.mostrarMensaje(error.getMessage());
            return;
        }
        PantallaBatalla pantalla = new PantallaBatalla(vista);
        ControladorBatalla controlador = new ControladorBatalla(batalla, pantalla);
        SwingUtilities.invokeLater(controlador::iniciar);
        pantalla.setVisible(true);

        // El modelo entrega el resultado real y acredita los premios una sola vez.
        ResultadoBatalla resultado = partida.procesarFinCombate();
        alCambiar.run();
        if (resultado != null) { mostrarResultado(resultado); }
    }

    private void mostrarResultado(ResultadoBatalla resultado) {
        Partida partida = sesion.obtenerPartida();
        boolean mejoraPendiente = partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA;
        ResultadoBatallaDTO datos = new ResultadoBatallaDTO(
                resultado != null,
                resultado != null && resultado.fueVictoria(),
                resultado != null && resultado.eraJefe(),
                partida.obtenerCiclo(), partida.obtenerEstres(), partida.obtenerLimiteEstres(),
                resultado == null ? 0 : resultado.obtenerCreditos(),
                resultado == null ? 0 : resultado.obtenerExperiencia(),
                mejoraPendiente, DatosEquipo.crear(partida.obtenerEquipo()));
        PantallaResultadoBatalla pantalla = new PantallaResultadoBatalla(vista, datos);
        pantalla.conectarMejoras(
                () -> elegirMejora(TipoMejora.EXPERIENCIA, pantalla),
                () -> elegirMejora(TipoMejora.CREDITOS, pantalla));
        pantalla.setVisible(true);
    }

    private void elegirMejora(TipoMejora tipo, PantallaResultadoBatalla pantalla) {
        if (sesion.obtenerPartida().elegirMejora(tipo)) {
            alCambiar.run();
            pantalla.dispose();
        }
    }

    public void iniciarNuevaPartida() {
        if (vista.confirmarNuevaPartida()) {
            sesion.obtenerPartida().iniciarNueva();
            alCambiar.run();
        }
    }

    public void abrirTienda() {
        new ControladorObjetos(sesion.obtenerPartida(), vista, true, alCambiar).mostrar();
    }

    public void mostrarInventario() {
        new ControladorObjetos(sesion.obtenerPartida(), vista, false, alCambiar).mostrar();
    }
}
