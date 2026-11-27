package controlador;

import modelo.combatientes.Equipo;
import modelo.partida.Partida;
import modelo.partida.EtapaPartida;
import vista.organizador.PantallaEquipo;

/** Presenta el equipo. Los eventos se delegan a controladores especializados. */
public class ControladorEquipo {
    private final SesionPartida sesion;
    private final PantallaEquipo vista;
    private final ControladorEquipamiento equipamiento;
    private final ControladorConsumibles consumibles;
    private final ControladorGuardado guardado;
    private final ControladorPartida progreso;

    public ControladorEquipo(Partida partida, PantallaEquipo vista) {
        this.sesion = new SesionPartida(partida);
        this.vista = vista;
        equipamiento = new ControladorEquipamiento(sesion, vista, this::refrescarVista);
        consumibles = new ControladorConsumibles(sesion, vista, this::refrescarVista);
        guardado = new ControladorGuardado(sesion, vista, this::refrescarVista);
        progreso = new ControladorPartida(sesion, vista, this::refrescarVista);
    }

    // Mantiene los eventos que ya utiliza PantallaEquipo y las pruebas Swing.
    public void gestionarEquipamiento(int indice) { equipamiento.gestionarEquipamiento(indice); }
    public void gestionarConsumibles() { consumibles.gestionarConsumibles(); }
    public void guardarPartida() { guardado.guardarPartida(); }
    public void cargarPartida() { guardado.cargarPartida(); }
    public void iniciarCombate() { progreso.iniciarCombate(); }
    public void iniciarNuevaPartida() { progreso.iniciarNuevaPartida(); }
    public void abrirTienda() { progreso.abrirTienda(); }
    public void mostrarInventario() { progreso.mostrarInventario(); }

    public void refrescarVista() {
        Partida partida = sesion.obtenerPartida();
        Equipo equipo = partida.obtenerEquipo();
        vista.mostrarEquipo(DatosEquipo.crear(equipo), equipo.obtenerInventario().obtenerCreditos());
        vista.mostrarProgreso(partida.obtenerCiclo(), partida.obtenerEstres(),
                partida.obtenerLimiteEstres());
        String accion = partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA
                ? "Elegir mejora" : partida.obtenerEstres() >= partida.obtenerLimiteEstres()
                ? "Enfrentar al jefe" : "Iniciar combate " + (partida.obtenerEstres() + 1) + "/5";
        vista.mostrarEtapa(partida.obtenerEtapa().name(), accion,
                partida.obtenerEtapa() == EtapaPartida.PREPARACION,
                partida.puedeGuardar(), !partida.estaFinalizada());
    }

}
