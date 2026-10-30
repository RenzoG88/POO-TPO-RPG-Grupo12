package modelo.estadostemporales;

import modelo.combatientes.Combatiente;
import java.io.Serializable;

public abstract class EfectoEstado implements Serializable {
    private static final long serialVersionUID = 1L;
    private int turnosRestantes;

    public EfectoEstado(int duracion) {
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duracion debe ser positiva");
        }
        turnosRestantes = duracion;
    }

    public abstract TipoEstado obtenerTipo();

    public int obtenerDuracion() {
        return turnosRestantes;
    }

    public void sumarDuracion(int turnos) {
        if (turnos <= 0) {
            throw new IllegalArgumentException("La duracion a sumar debe ser positiva");
        }
        turnosRestantes = Math.addExact(turnosRestantes, turnos);
    }

    public void alIniciarTurno(Combatiente afectado) {
        // Los estados que producen un efecto al inicio sobrescriben este metodo.
    }

    public boolean permiteActuar() {
        return true;
    }

    public int modificarDanioRecibido(int cantidad) {
        return cantidad;
    }

    public void alFinalizarTurno() {
        if (turnosRestantes > 0) {
            turnosRestantes--;
        }
    }

    public boolean haExpirado() {
        return turnosRestantes == 0;
    }
}
