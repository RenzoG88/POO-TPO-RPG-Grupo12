package modelo.inventario;

import modelo.combatientes.Empleado;

public class Proteccion extends Equipamiento {

    private static final long serialVersionUID = -5377099091413624084L;

    private int bonusDefensa;

    public Proteccion(String nombre, int bonusDefensa) {
        super(nombre);
        if (bonusDefensa < 0) {
            throw new IllegalArgumentException("El bonus de defensa no puede ser negativo");
        }

        this.bonusDefensa = bonusDefensa;
    }

    public int obtenerBonusDefensa() {
        return bonusDefensa;
    }

    @Override
    public Equipamiento equiparEn(Empleado empleado) {
        return empleado.cambiarProteccion(this);
    }

    @Override
    public boolean estaEquipadoEn(Empleado empleado) {
        return empleado.obtenerProteccion() == this;
    }

    @Override
    public boolean desequiparDe(Empleado empleado) {
        if (!estaEquipadoEn(empleado)) { return false; }
        empleado.cambiarProteccion(null);
        return true;
    }

    @Override
    public String obtenerDescripcion() {
        return obtenerNombre() + " | Defensa +" + bonusDefensa;
    }
}
