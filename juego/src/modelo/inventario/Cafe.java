package modelo.inventario;

import modelo.combatientes.Empleado;

public class Cafe extends Consumible {

    private static final long serialVersionUID = 1L;

    private int recuperacionEnergia;

    public Cafe() {
        this(20);
    }

    public Cafe(int recuperacionEnergia) {
        super("Cafe");

        if (recuperacionEnergia <= 0) {
            throw new IllegalArgumentException("La recuperacion debe ser positiva");
        }

        this.recuperacionEnergia = recuperacionEnergia;
    }

    @Override
    public boolean puedeAplicarse(Empleado objetivo) {
        return objetivo != null && !objetivo.estaDespedido()
                && objetivo.obtenerEnergia() < objetivo.obtenerEnergiaMaxima();
    }

    @Override
    public void aplicar(Empleado objetivo) {
        if (puedeAplicarse(objetivo)) {
            objetivo.recuperarEnergia(recuperacionEnergia);
        }
    }

    public int obtenerRecuperacionEnergia() {
        return recuperacionEnergia;
    }
}
