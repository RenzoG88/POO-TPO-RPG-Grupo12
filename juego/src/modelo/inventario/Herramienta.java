package modelo.inventario;

public class Herramienta extends Equipamiento {

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
}
