package modelo.partida;

import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.combatientes.Enemigo;
import modelo.combate.Batalla;
import modelo.inventario.Equipamiento;
import modelo.inventario.Consumible;
import modelo.inventario.Oferta;
import modelo.inventario.Tienda;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;

    private Equipo equipo;
    private Tienda tienda;
    // Solo se guarda en preparacion o eleccion de mejora, nunca durante una batalla.
    private transient Batalla batallaActual;
    private transient GeneradorEncuentros generador;
    private EtapaPartida etapa;
    private int ciclo;
    private int estres;
    private final int limiteEstres = 5;
    private double multiplicadorExperiencia;
    private double multiplicadorCreditos;

    public Partida() {
        iniciarNueva();
    }

    public void iniciarNueva() {
        equipo = new Equipo();
        batallaActual = null;
        generador = new GeneradorEncuentros();
        etapa = EtapaPartida.PREPARACION;
        ciclo = 1;
        estres = 0;
        multiplicadorExperiencia = 1.0;
        multiplicadorCreditos = 1.0;
        tienda = new Tienda();
        tienda.renovarOfertas(ciclo);
    }

    public Batalla iniciarSiguienteCombate() {
        if (etapa != EtapaPartida.PREPARACION) {
            throw new IllegalStateException("Solo se inicia un combate desde preparacion");
        }
        if (equipo.estanTodosDespedidos()) {
            etapa = EtapaPartida.FINALIZADA;
            throw new IllegalStateException("No quedan empleados activos");
        }

        boolean esJefe = estres >= limiteEstres;
        // Recompensas iniciales de balance: normal 40 XP/30 creditos; jefe 100 XP/80.
        Recompensa recompensa = new Recompensa(esJefe ? 100 : 40, esJefe ? 80 : 30);
        Batalla nueva = new Batalla(equipo, generador.crearEnemigos(ciclo, esJefe),
                esJefe, recompensa);
        nueva.iniciar();
        batallaActual = nueva;
        etapa = EtapaPartida.COMBATE;
        return batallaActual;
    }

    public void procesarFinCombate() {
        if (etapa != EtapaPartida.COMBATE || batallaActual == null
                || !batallaActual.estaFinalizada()) {
            return;
        }

        if (!batallaActual.fueVictoria() || equipo.estanTodosDespedidos()) {
            limpiarEstadosDelCombate();
            batallaActual = null;
            etapa = EtapaPartida.FINALIZADA;
            return;
        }

        Recompensa recompensa = batallaActual.obtenerRecompensa();
        int experiencia = recompensa.calcularExperiencia(multiplicadorExperiencia);
        int creditos = recompensa.calcularCreditos(multiplicadorCreditos);
        boolean eraJefe = batallaActual.esCombateJefe();
        limpiarEstadosDelCombate();
        equipo.repartirExperiencia(experiencia);
        equipo.obtenerInventario().agregarCreditos(creditos);
        batallaActual = null;

        // Cambiar la etapa y quitar la batalla impide cobrar el mismo resultado dos veces.
        if (eraJefe) {
            etapa = EtapaPartida.ELECCION_MEJORA;
        } else {
            estres++;
            entrarEnPreparacion();
        }
    }

    public boolean elegirMejora(TipoMejora tipo) {
        if (etapa != EtapaPartida.ELECCION_MEJORA || tipo == null) {
            return false;
        }
        if (tipo == TipoMejora.EXPERIENCIA) {
            multiplicadorExperiencia += 0.20;
        } else {
            multiplicadorCreditos += 0.20;
        }
        ciclo++;
        estres = 0;
        entrarEnPreparacion();
        return true;
    }

    private void entrarEnPreparacion() {
        etapa = EtapaPartida.PREPARACION;
        tienda.renovarOfertas(ciclo);
    }

    private void limpiarEstadosDelCombate() {
        for (Empleado empleado : equipo.obtenerEmpleados()) {
            empleado.limpiarEstados();
        }
        for (Enemigo enemigo : batallaActual.obtenerEnemigos()) {
            enemigo.limpiarEstados();
        }
    }

    public Batalla obtenerBatallaActual() {
        return batallaActual;
    }

    public boolean comprar(Oferta oferta) {
        if (etapa != EtapaPartida.PREPARACION) {
            return false;
        }

        return tienda.comprar(oferta, equipo.obtenerInventario());
    }

    public boolean usarConsumible(Consumible item, Empleado objetivo) {
        if (etapa != EtapaPartida.PREPARACION) {
            return false;
        }

        return equipo.usarConsumible(item, objetivo);
    }

    public boolean equipar(Empleado empleado, Equipamiento objeto) {
        if (etapa != EtapaPartida.PREPARACION) {
            return false;
        }

        return equipo.equipar(empleado, objeto);
    }

    public boolean desequipar(Empleado empleado, Equipamiento objeto) {
        if (etapa != EtapaPartida.PREPARACION) {
            return false;
        }

        return equipo.desequipar(empleado, objeto);
    }

    public boolean puedeGuardar() {
        return etapa == EtapaPartida.PREPARACION
                || etapa == EtapaPartida.ELECCION_MEJORA;
    }

    public boolean estaFinalizada() {
        return etapa == EtapaPartida.FINALIZADA;
    }

    public Equipo obtenerEquipo() {
        return equipo;
    }

    public Tienda obtenerTienda() {
        return tienda;
    }

    public EtapaPartida obtenerEtapa() {
        return etapa;
    }

    public int obtenerCiclo() {
        return ciclo;
    }

    public int obtenerEstres() {
        return estres;
    }

    public int obtenerLimiteEstres() {
        return limiteEstres;
    }

    public double obtenerMultiplicadorExperiencia() {
        return multiplicadorExperiencia;
    }

    public double obtenerMultiplicadorCreditos() {
        return multiplicadorCreditos;
    }
    // Compatibilidad con los guardados del paso 4, que todavia no tenian tienda.
    private void readObject(ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        generador = new GeneradorEncuentros();

        if (tienda == null) {
            if (ciclo < 1) {
                throw new IOException("El guardado tiene un ciclo invalido.");
            }
            tienda = new Tienda();
            tienda.renovarOfertas(ciclo);
        }
    }
}
