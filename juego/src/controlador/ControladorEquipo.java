package controlador;

import dto.EmpleadoDTO;
import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.inventario.Equipamiento;
import modelo.inventario.Consumible;
import modelo.inventario.Snack;
import modelo.inventario.Cafe;
import modelo.inventario.Oferta;
import modelo.inventario.Herramienta;
import modelo.inventario.Item;
import modelo.inventario.Proteccion;
import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;
import modelo.partida.EtapaPartida;
import modelo.partida.TipoMejora;
import modelo.combate.Batalla;

import java.io.IOException;
import vista.organizador.PantallaEquipo;
import vista.organizador.PantallaBatalla;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.List;

public class ControladorEquipo {

    private Partida partida;
    private final PersistenciaPartida persistencia;
    private final PantallaEquipo vista;

    public ControladorEquipo(Partida partida, PantallaEquipo vista) {
        this.partida = partida;
        this.persistencia = new PersistenciaPartida();
        this.vista = vista;
    }

    public void iniciarCombate() {
        if (partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA) {
            elegirMejora();
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
            refrescarVista();
            vista.mostrarMensaje(error.getMessage());
            return;
        }
        PantallaBatalla pantalla = new PantallaBatalla(vista);
        ControladorBatalla controladorBatalla = new ControladorBatalla(batalla, pantalla);
        // El diálogo modal sigue atendiendo botones y el Timer en el hilo de Swing.
        SwingUtilities.invokeLater(controladorBatalla::iniciar);
        pantalla.setVisible(true);

        int experiencia = batalla.obtenerRecompensa()
                .calcularExperiencia(partida.obtenerMultiplicadorExperiencia());
        int creditos = batalla.obtenerRecompensa()
                .calcularCreditos(partida.obtenerMultiplicadorCreditos());
        partida.procesarFinCombate();
        refrescarVista();
        if (batalla.fueVictoria()) {
            vista.mostrarMensaje((batalla.esCombateJefe() ? "¡Venciste al jefe!" : "¡Victoria!")
                    + "\nRecompensa: " + creditos + " créditos y " + experiencia
                    + " XP para cada empleado activo.");
            if (partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA) {
                elegirMejora();
            }
        } else {
            vista.mostrarMensaje("El equipo quedó sin empleados activos. Fin de la partida."
                    + "\nPodés iniciar una nueva partida o cargar un guardado.");
        }
    }

    private void elegirMejora() {
        int eleccion = vista.elegirMejora();
        if (eleccion == 0 || eleccion == 1) {
            partida.elegirMejora(eleccion == 0 ? TipoMejora.EXPERIENCIA : TipoMejora.CREDITOS);
        }
        refrescarVista();
    }

    public void iniciarNuevaPartida() {
        if (vista.confirmarNuevaPartida()) {
            partida.iniciarNueva();
            refrescarVista();
        }
    }

    public void abrirTienda() {
        new ControladorObjetos(partida, vista, true, this::refrescarVista).mostrar();
    }

    public void mostrarInventario() {
        new ControladorObjetos(partida, vista, false, this::refrescarVista).mostrar();
    }

    public void gestionarConsumibles() {
        List<Consumible> disponibles = new ArrayList<>();
        for (Item item : partida.obtenerEquipo().obtenerInventario().obtenerItems()) {
            if (item instanceof Consumible) {
                disponibles.add((Consumible) item);
            }
        }

        if (disponibles.isEmpty()) {
            vista.mostrarMensaje("No hay consumibles en el inventario.");
            return;
        }

        String[] opciones = new String[disponibles.size()];
        for (int i = 0; i < disponibles.size(); i++) {
            opciones[i] = (i + 1) + ". " + describirItem(disponibles.get(i));
        }
        int indiceItem = vista.elegirConsumible(opciones);
        if (indiceItem < 0 || indiceItem >= disponibles.size()) {
            return;
        }

        List<Empleado> empleados = partida.obtenerEquipo().obtenerEmpleados();
        String[] nombres = new String[empleados.size()];
        for (int i = 0; i < empleados.size(); i++) {
            Empleado empleado = empleados.get(i);
            nombres[i] = empleado.obtenerNombre()
                    + (empleado.estaDespedido() ? " (despedido)" : "");
        }
        int indiceEmpleado = vista.elegirEmpleado(nombres);
        if (indiceEmpleado < 0 || indiceEmpleado >= empleados.size()) {
            return;
        }

        if (partida.usarConsumible(disponibles.get(indiceItem), empleados.get(indiceEmpleado))) {
            refrescarVista();
            vista.mostrarMensaje("Consumible aplicado.");
        } else {
            vista.mostrarMensaje("No se uso el objeto. El empleado debe estar activo "
                    + "y necesitar la recuperacion, y la partida debe estar en preparacion.");
        }
    }

    private String describirItem(Item item) {
        String detalle = "";
        if (item instanceof Herramienta) {
            detalle = "Ataque +" + ((Herramienta) item).obtenerBonusAtaque();
        } else if (item instanceof Proteccion) {
            detalle = "Defensa +" + ((Proteccion) item).obtenerBonusDefensa();
        } else if (item instanceof Snack) {
            detalle = "Recupera " + ((Snack) item).obtenerRecuperacionVida() + " de vida";
        } else if (item instanceof Cafe) {
            detalle = "Recupera " + ((Cafe) item).obtenerRecuperacionEnergia() + " de energia";
        }
        return item.obtenerNombre() + (detalle.isEmpty() ? "" : " | " + detalle);
    }

    public void guardarPartida() {
        if (!partida.puedeGuardar()) {
            vista.mostrarMensaje("No se puede guardar en esta etapa de la partida.");
            return;
        }

        String ruta = vista.elegirRutaGuardado();

        if (ruta == null) {
            return;
        }

        try {
            persistencia.guardar(partida, ruta);
            vista.mostrarMensaje("Partida guardada correctamente.");
        } catch (IOException | IllegalArgumentException | IllegalStateException error) {
            vista.mostrarMensaje("No se pudo guardar la partida. " + error.getMessage());
        }
    }

    public void cargarPartida() {
        String ruta = vista.elegirRutaCarga();

        if (ruta == null || !vista.confirmarCarga()) {
            return;
        }

        try {
            Partida cargada = persistencia.cargar(ruta);
            // Se reemplaza la partida abierta solo despues de una carga correcta.
            partida = cargada;
            refrescarVista();
            vista.mostrarMensaje("Partida cargada correctamente.");
        } catch (IOException | ClassNotFoundException | IllegalArgumentException error) {
            vista.mostrarMensaje("No se pudo cargar la partida. " + error.getMessage());
        }
    }

    public void refrescarVista() {
        Equipo equipo = partida.obtenerEquipo();
        List<EmpleadoDTO> datos = new ArrayList<>();

        for (Empleado empleado : equipo.obtenerEmpleados()) {
            String herramienta = empleado.obtenerHerramienta() == null
                    ? "Sin herramienta" : empleado.obtenerHerramienta().obtenerNombre();
            String proteccion = empleado.obtenerProteccion() == null
                    ? "Sin proteccion" : empleado.obtenerProteccion().obtenerNombre();

            datos.add(new EmpleadoDTO(
                    empleado.obtenerNombre(),
                    empleado.obtenerNivel(),
                    empleado.obtenerExperiencia(),
                    empleado.obtenerVida(),
                    empleado.obtenerVidaMaxima(),
                    empleado.obtenerEnergia(),
                    empleado.obtenerEnergiaMaxima(),
                    empleado.estaDespedido(),
                    empleado.obtenerAtaqueTotal(),
                    empleado.obtenerDefensaTotal(),
                    herramienta,
                    proteccion,
                    empleado.obtenerExperienciaSiguienteNivel()
            ));
        }

        vista.mostrarEquipo(datos, equipo.obtenerInventario().obtenerCreditos());
        vista.mostrarProgreso(partida.obtenerCiclo(), partida.obtenerEstres(),
                partida.obtenerLimiteEstres());
        String accion = partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA
                ? "Elegir mejora" : partida.obtenerEstres() >= partida.obtenerLimiteEstres()
                ? "Enfrentar al jefe" : "Iniciar combate " + (partida.obtenerEstres() + 1) + "/5";
        vista.mostrarEtapa(partida.obtenerEtapa().name(), accion,
                partida.obtenerEtapa() == EtapaPartida.PREPARACION,
                partida.puedeGuardar(), !partida.estaFinalizada());
    }

    public void gestionarEquipamiento(int indiceEmpleado) {
        List<Empleado> empleados = partida.obtenerEquipo().obtenerEmpleados();

        if (indiceEmpleado < 0 || indiceEmpleado >= empleados.size()) {
            vista.mostrarMensaje("No se encontro el empleado seleccionado.");
            return;
        }

        Empleado empleado = empleados.get(indiceEmpleado);
        int accion = vista.elegirAccionEquipamiento(empleado.obtenerNombre());

        switch (accion) {
            case 0:
                equiparDesdeInventario(empleado);
                break;
            case 1:
                quitarEquipamiento(empleado, empleado.obtenerHerramienta());
                break;
            case 2:
                quitarEquipamiento(empleado, empleado.obtenerProteccion());
                break;
            default:
                return;
        }

        refrescarVista();
    }

    private void equiparDesdeInventario(Empleado empleado) {
        List<Equipamiento> disponibles = new ArrayList<>();

        for (Item item : partida.obtenerEquipo().obtenerInventario().obtenerItems()) {
            if (item instanceof Equipamiento) {
                disponibles.add((Equipamiento) item);
            }
        }

        if (disponibles.isEmpty()) {
            vista.mostrarMensaje("No hay equipamiento disponible en el inventario.");
            return;
        }

        String[] opciones = new String[disponibles.size()];

        for (int i = 0; i < disponibles.size(); i++) {
            Equipamiento objeto = disponibles.get(i);
            String detalle = "";

            if (objeto instanceof Herramienta) {
                detalle = " | Ataque +" + ((Herramienta) objeto).obtenerBonusAtaque();
            } else if (objeto instanceof Proteccion) {
                detalle = " | Defensa +" + ((Proteccion) objeto).obtenerBonusDefensa();
            }

            opciones[i] = (i + 1) + ". " + objeto.obtenerNombre() + detalle;
        }

        int seleccion = vista.elegirObjeto(empleado.obtenerNombre(), opciones);

        if (seleccion < 0 || seleccion >= disponibles.size()) {
            return;
        }

        // Usamos el objeto de la misma lista que se mostro en el selector.
        Equipamiento elegido = disponibles.get(seleccion);

        if (!partida.equipar(empleado, elegido)) {
            vista.mostrarMensaje("No se pudo equipar el objeto. El empleado debe estar "
                    + "activo, el objeto disponible y la partida en preparacion.");
        }
    }

    private void quitarEquipamiento(Empleado empleado, Equipamiento objeto) {
        if (objeto == null) {
            vista.mostrarMensaje("El empleado no tiene un objeto equipado en esa casilla.");
            return;
        }

        if (!partida.desequipar(empleado, objeto)) {
            vista.mostrarMensaje("No se pudo retirar el equipamiento. El objeto debe "
                    + "estar equipado y la partida debe estar en preparacion.");
        }
    }
}
