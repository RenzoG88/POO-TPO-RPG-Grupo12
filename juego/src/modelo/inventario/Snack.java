package modelo.inventario;

import modelo.combatientes.Empleado;

public class Snack extends Consumible {

    private static final long serialVersionUID = 1L;

    private int recuperacionVida;

    public Snack() {
        this(30);
    }

    public Snack(int recuperacionVida) {
        super("Snack");

        if (recuperacionVida <= 0) {
            throw new IllegalArgumentException("La recuperacion debe ser positiva");
        }

        this.recuperacionVida = recuperacionVida;
    }

    @Override
    public boolean puedeAplicarse(Empleado objetivo) {
        return objetivo != null && !objetivo.estaDespedido()
                && objetivo.obtenerVida() < objetivo.obtenerVidaMaxima();
    }

    @Override
    public void aplicar(Empleado objetivo) {
        if (puedeAplicarse(objetivo)) {
            objetivo.curar(recuperacionVida);
        }
    }

    public int obtenerRecuperacionVida() {
        return recuperacionVida;
    }
}
