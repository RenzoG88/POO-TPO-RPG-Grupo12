package modelo.partida;

import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
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
        etapa = EtapaPartida.PREPARACION;
        ciclo = 1;
        estres = 0;
        multiplicadorExperiencia = 1.0;
        multiplicadorCreditos = 1.0;
        tienda = new Tienda();
        tienda.renovarOfertas(ciclo);
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

        if (tienda == null) {
            if (ciclo < 1) {
                throw new IOException("El guardado tiene un ciclo invalido.");
            }
            tienda = new Tienda();
            tienda.renovarOfertas(ciclo);
        }
    }
}
