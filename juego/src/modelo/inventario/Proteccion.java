package modelo.inventario;

public class Proteccion extends Equipamiento {

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
}
