package modelo.inventario;

import modelo.combatientes.Empleado;

public class Herramienta extends Equipamiento {

    private static final long serialVersionUID = -4574430451126906796L;

    private int bonusAtaque;

    public Herramienta(String nombre, int bonusAtaque) {
        super(nombre);
        if (bonusAtaque < 0) {
            throw new IllegalArgumentException("El bonus de ataque no puede ser negativo");
        }

        this.bonusAtaque = bonusAtaque;
    }

    public int obtenerBonusAtaque() {
        return bonusAtaque;
    }

    @Override
    public Equipamiento equiparEn(Empleado empleado) {
        return empleado.cambiarHerramienta(this);
    }

    @Override
    public boolean estaEquipadoEn(Empleado empleado) {
        return empleado.obtenerHerramienta() == this;
    }

    @Override
    public boolean desequiparDe(Empleado empleado) {
        if (!estaEquipadoEn(empleado)) { return false; }
        empleado.cambiarHerramienta(null);
        return true;
    }

    @Override
    public String obtenerDescripcion() {
        return obtenerNombre() + " | Ataque +" + bonusAtaque;
    }
}
