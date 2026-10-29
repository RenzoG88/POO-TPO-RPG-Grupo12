package modelo.partida;

import java.io.Serializable;

public class Recompensa implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int experiencia;
    private final int creditos;

    public Recompensa(int experiencia, int creditos) {
        if (experiencia < 0 || creditos < 0) {
            throw new IllegalArgumentException(
                    "La experiencia y los creditos no pueden ser negativos"
            );
        }

        this.experiencia = experiencia;
        this.creditos = creditos;
    }

    public int calcularExperiencia(double multiplicador) {
        return calcularCantidad(experiencia, multiplicador);
    }

    public int calcularCreditos(double multiplicador) {
        return calcularCantidad(creditos, multiplicador);
    }

    private int calcularCantidad(int cantidadBase, double multiplicador) {
        if (!Double.isFinite(multiplicador) || multiplicador < 0) {
            throw new IllegalArgumentException(
                    "El multiplicador debe ser un numero finito no negativo"
            );
        }

        double resultado = cantidadBase * multiplicador;

        if (!Double.isFinite(resultado) || resultado > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "La recompensa supera el valor maximo permitido"
            );
        }

        // Las recompensas se expresan en unidades enteras.
        return (int) Math.round(resultado);
    }
}

