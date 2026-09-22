package modelo.inventario;

public class Herramienta extends Equipamiento {

    private int bonusAtaque;

    public Herramienta(String nombre, int bonusAtaque) {
        super(nombre);
        this.bonusAtaque = bonusAtaque;
    }

    public int obtenerBonusAtaque() {
        return bonusAtaque;
    }
}