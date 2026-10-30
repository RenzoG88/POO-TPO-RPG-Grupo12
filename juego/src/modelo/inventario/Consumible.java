package modelo.inventario;

import modelo.combatientes.Empleado;

public abstract class Consumible extends Item {

    private static final long serialVersionUID = 1L;

    public Consumible(String nombre) {
        super(nombre);
    }

    public abstract boolean puedeAplicarse(Empleado objetivo);

    public abstract void aplicar(Empleado objetivo);
}
