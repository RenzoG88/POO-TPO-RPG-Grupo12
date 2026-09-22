package modelo.inventario;

public class Proteccion extends Equipamiento {

    private int bonusDefensa;

    public Proteccion(String nombre, int bonusDefensa) {
        super(nombre);
        this.bonusDefensa = bonusDefensa;
    }

    public int obtenerBonusDefensa() {
        return bonusDefensa;
    }
}
