package modelo.inventario;

import modelo.combatientes.Empleado;

public abstract class Equipamiento extends Item {

    // UID de la version entregada: conserva la compatibilidad de los archivos .rpg.
    private static final long serialVersionUID = 1770908829640945715L;

    public Equipamiento(String nombre) {
        super(nombre);
    }

    // Equipo valida pertenencia e inventario; cada objeto conoce su propia casilla.
    public abstract Equipamiento equiparEn(Empleado empleado);
    public abstract boolean estaEquipadoEn(Empleado empleado);
    public abstract boolean desequiparDe(Empleado empleado);
    public abstract String obtenerDescripcion();
}
