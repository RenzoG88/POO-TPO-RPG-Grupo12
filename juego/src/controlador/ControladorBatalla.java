package controlador;

import dto.CombatienteDTO;
import dto.TurnoDTO;
import modelo.combate.Batalla;
import modelo.combate.Habilidad;
import modelo.combatientes.Combatiente;
import modelo.combatientes.Empleado;
import modelo.combatientes.Enemigo;
import modelo.estadostemporales.EfectoEstado;
import modelo.inventario.Consumible;
import modelo.inventario.Item;
import vista.organizador.PantallaBatalla;

import javax.swing.Timer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ControladorBatalla {
    private final Batalla batalla;
    private final PantallaBatalla vista;
    private final Timer turnoEnemigo;

    public ControladorBatalla(Batalla batalla, PantallaBatalla vista) {
        this.batalla = batalla;
        this.vista = vista;
        turnoEnemigo = new Timer(500, evento -> {
            batalla.ejecutarTurnoEnemigo();
            actualizar();
        });
        turnoEnemigo.setRepeats(false);
        vista.obtenerPanel().conectarAcciones(this::atacar, this::defender,
                this::usarHabilidad, this::usarObjeto);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evento) {
                turnoEnemigo.stop();
            }
        });
    }

    public void iniciar() {
        actualizar();
    }

    public void atacar() {
        if (!esTurnoDelJugador()) { return; }
        List<Combatiente> objetivos = new ArrayList<>();
        for (Enemigo enemigo : batalla.obtenerEnemigos()) {
            if (enemigo.estaActivo()) { objetivos.add(enemigo); }
        }
        Combatiente objetivo = elegirObjetivo(objetivos);
        if (objetivo != null && batalla.atacar(objetivo)) {
            actualizar();
        }
    }

    public void defender() {
        if (batalla.defender()) { actualizar(); }
    }

    public void usarHabilidad() {
        if (!esTurnoDelJugador()) { return; }
        Combatiente actor = batalla.obtenerCombatienteActual();
        List<Habilidad> habilidades = actor.obtenerHabilidades();
        if (habilidades.isEmpty()) {
            vista.mostrarMensaje("Este personaje no tiene habilidades.");
            return;
        }
        String[] nombres = new String[habilidades.size()];
        for (int i = 0; i < habilidades.size(); i++) {
            nombres[i] = habilidades.get(i).obtenerNombre() + " | Costo: "
                    + habilidades.get(i).obtenerCostoEnergia() + " EN";
        }
        int indice = vista.elegir("Habilidades", "Energía disponible: " + actor.obtenerEnergia(), nombres);
        if (indice < 0) { return; }
        Habilidad habilidad = habilidades.get(indice);
        if (actor.obtenerEnergia() < habilidad.obtenerCostoEnergia()) {
            vista.mostrarMensaje("No alcanza la energía. Podés elegir otra acción.");
            return;
        }
        List<Combatiente> participantes = new ArrayList<>(batalla.obtenerEquipo().obtenerEmpleados());
        participantes.addAll(batalla.obtenerEnemigos());
        List<Combatiente> objetivos = new ArrayList<>();
        for (Combatiente participante : participantes) {
            if (habilidad.objetivoValido(actor, participante)) { objetivos.add(participante); }
        }
        Combatiente objetivo = elegirObjetivo(objetivos);
        if (objetivo != null) {
            if (batalla.usarHabilidad(habilidad, objetivo)) {
                actualizar();
            } else {
                vista.mostrarMensaje("No se pudo usar la habilidad. Conservás el turno.");
            }
        }
    }

    public void usarObjeto() {
        if (!esTurnoDelJugador()) { return; }
        List<Consumible> disponibles = new ArrayList<>();
        for (Item item : batalla.obtenerEquipo().obtenerInventario().obtenerItems()) {
            if (item instanceof Consumible) { disponibles.add((Consumible) item); }
        }
        if (disponibles.isEmpty()) {
            vista.mostrarMensaje("No hay consumibles. Podés comprarlos entre combates.");
            return;
        }
        String[] nombres = new String[disponibles.size()];
        for (int i = 0; i < disponibles.size(); i++) {
            nombres[i] = (i + 1) + ". " + disponibles.get(i).obtenerNombre();
        }
        int indice = vista.elegir("Consumibles", "Usar un objeto consume el turno actual.", nombres);
        if (indice < 0) { return; }
        Consumible item = disponibles.get(indice);
        List<Combatiente> objetivos = new ArrayList<>();
        for (Empleado empleado : batalla.obtenerEquipo().obtenerActivos()) {
            if (item.puedeAplicarse(empleado)) { objetivos.add(empleado); }
        }
        Combatiente objetivo = elegirObjetivo(objetivos);
        if (objetivo != null && batalla.usarItem(item, (Empleado) objetivo)) {
            actualizar();
        }
    }

    private Combatiente elegirObjetivo(List<Combatiente> objetivos) {
        if (objetivos.isEmpty()) {
            vista.mostrarMensaje("No hay objetivos válidos para esa acción. Conservás el turno.");
            return null;
        }
        String[] nombres = new String[objetivos.size()];
        for (int i = 0; i < objetivos.size(); i++) {
            Combatiente objetivo = objetivos.get(i);
            nombres[i] = (i + 1) + ". " + nombreVisible(objetivo)
                    + " | Vida " + objetivo.obtenerVida() + "/" + objetivo.obtenerVidaMaxima()
                    + " | EN " + objetivo.obtenerEnergia() + "/" + objetivo.obtenerEnergiaMaxima();
        }
        int indice = vista.elegir("Objetivo", "Elegí el personaje que recibirá la acción:", nombres);
        return indice < 0 ? null : objetivos.get(indice);
    }

    private boolean esTurnoDelJugador() {
        return batalla.obtenerCombatienteActual() instanceof Empleado;
    }

    private void actualizar() {
        Combatiente actor = batalla.obtenerCombatienteActual();
        List<CombatienteDTO> empleados = new ArrayList<>();
        List<CombatienteDTO> enemigos = new ArrayList<>();
        for (Empleado empleado : batalla.obtenerEquipo().obtenerEmpleados()) {
            empleados.add(crearDatos(empleado, empleado == actor));
        }
        for (Enemigo enemigo : batalla.obtenerEnemigos()) {
            enemigos.add(crearDatos(enemigo, enemigo == actor));
        }
        String titulo = (batalla.esCombateJefe() ? "Jefe de la empresa" : "Combate")
                + " | Ronda " + batalla.obtenerRonda();
        String turno = actor == null ? "Combate terminado" : "Turno de " + nombreVisible(actor)
                + (actor instanceof Empleado ? " — Elegí una acción" : " — Actúa automáticamente");
        vista.obtenerPanel().actualizar(titulo, turno, empleados, enemigos,
                batalla.obtenerRegistro(), actor instanceof Empleado);
        vista.obtenerPanel().mostrarOrdenTurnos(obtenerOrdenVisible());
        if (batalla.estaFinalizada()) {
            turnoEnemigo.stop();
            vista.dispose();
        } else if (actor instanceof Enemigo) {
            turnoEnemigo.restart();
        }
    }

    private List<TurnoDTO> obtenerOrdenVisible() {
        List<TurnoDTO> datos = new ArrayList<>();
        List<Combatiente> orden = batalla.obtenerOrdenTurnos();
        Combatiente actual = batalla.obtenerCombatienteActual();
        for (int i = 0; i < orden.size(); i++) {
            Combatiente participante = orden.get(i);
            boolean esActual = participante == actual;
            String estado = !participante.estaActivo() ? "Fuera de combate"
                    : esActual ? "TURNO ACTUAL"
                    : i < batalla.obtenerIndiceTurno() ? "Turno terminado" : "Pendiente";
            datos.add(new TurnoDTO(i + 1, nombreVisible(participante),
                    participante.obtenerVelocidad(), estado, esActual));
        }
        return datos;
    }

    private String nombreVisible(Combatiente personaje) {
        int indice = batalla.obtenerEnemigos().indexOf(personaje);
        // El número coincide con la tarjeta: distingue dos enemigos del mismo tipo.
        return indice < 0 ? personaje.obtenerNombre() : personaje.obtenerNombre() + " #" + (indice + 1);
    }

    private CombatienteDTO crearDatos(Combatiente personaje, boolean actual) {
        StringBuilder estados = new StringBuilder();
        for (EfectoEstado estado : personaje.obtenerEstados()) {
            if (estados.length() > 0) { estados.append(" | "); }
            estados.append(estado.obtenerTipo()).append(" (")
                    .append(estado.obtenerDuracion()).append(")");
        }
        return new CombatienteDTO(nombreVisible(personaje),
                personaje.getClass().getSimpleName().toLowerCase(Locale.ROOT) + ".png",
                personaje.obtenerVida(), personaje.obtenerVidaMaxima(),
                personaje.obtenerEnergia(), personaje.obtenerEnergiaMaxima(),
                personaje.obtenerAtaqueTotal(), personaje.obtenerDefensaTotal(),
                estados.toString(), actual);
    }
}
