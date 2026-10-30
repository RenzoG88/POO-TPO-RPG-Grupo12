package modelo.combate;

import modelo.combatientes.Combatiente;
import modelo.combatientes.Empleado;

public class Reparacion extends Habilidad {
    private static final long serialVersionUID = 1L;
    private final int cantidad;

    public Reparacion(String nombre, int costoEnergia, int cantidad) {
        super(nombre, costoEnergia);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La recuperacion debe ser positiva");
        }
        this.cantidad = cantidad;
    }

    @Override
    public boolean objetivoValido(Combatiente emisor, Combatiente objetivo) {
        return emisor instanceof Empleado && objetivo instanceof Empleado
                && emisor.estaActivo() && objetivo.estaActivo()
                && objetivo.obtenerVida() < objetivo.obtenerVidaMaxima();
    }

    @Override
    public void aplicar(Combatiente emisor, Combatiente objetivo) {
        if (objetivoValido(emisor, objetivo)) {
            // Combatiente.curar ya respeta la vida maxima y no reincorpora despedidos.
            objetivo.curar(cantidad);
        }
    }
}
