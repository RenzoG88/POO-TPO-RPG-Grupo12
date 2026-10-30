package modelo.combate;

import modelo.combatientes.Combatiente;
import java.io.Serializable;

public abstract class Habilidad implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nombre;
    private final int costoEnergia;

    public Habilidad(String nombre, int costoEnergia) {
        if (nombre == null || nombre.isBlank() || costoEnergia < 0) {
            throw new IllegalArgumentException("Nombre o costo de habilidad invalido");
        }
        this.nombre = nombre;
        this.costoEnergia = costoEnergia;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerCostoEnergia() {
        return costoEnergia;
    }

    public abstract boolean objetivoValido(Combatiente emisor, Combatiente objetivo);
    public abstract void aplicar(Combatiente emisor, Combatiente objetivo);
}
