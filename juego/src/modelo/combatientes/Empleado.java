package modelo.combatientes;

import modelo.inventario.Herramienta;
import modelo.inventario.Proteccion;

public abstract class Empleado extends Combatiente {

    private static final long serialVersionUID = 1L;

    private int nivel;
    private int experiencia;
    private Herramienta herramienta;
    private Proteccion proteccion;

    public Empleado(String nombre, int vidaMaxima, int energiaMaxima,
                    int ataque, int defensa, int velocidad) {

        super(nombre, vidaMaxima, energiaMaxima,
                ataque, defensa, velocidad);

        nivel = 1;
        experiencia = 0;
    }

    public void ganarExperiencia(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La experiencia no puede ser negativa"
            );
        }

        if (estaDespedido()) {
            return;
        }

        experiencia += cantidad;

        while (experiencia >= nivel * 100) {
            experiencia -= nivel * 100;
            subirNivel();
        }
    }

    private void subirNivel() {
        nivel++;
        mejorarEstadisticas(10, 5, 2, 1);
    }

    public boolean estaDespedido() {
        return !estaActivo();
    }

    @Override
    public int obtenerAtaqueTotal() {
        int total = super.obtenerAtaqueTotal();

        if (herramienta != null) {
            total += herramienta.obtenerBonusAtaque();
        }

        return total;
    }

    @Override
    public int obtenerDefensaTotal() {
        int total = super.obtenerDefensaTotal();

        if (proteccion != null) {
            total += proteccion.obtenerBonusDefensa();
        }

        return total;
    }

    public Herramienta cambiarHerramienta(Herramienta nueva) {
        Herramienta anterior = herramienta;
        herramienta = nueva;
        return anterior;
    }

    public Proteccion cambiarProteccion(Proteccion nueva) {
        Proteccion anterior = proteccion;
        proteccion = nueva;
        return anterior;
    }

    public int obtenerNivel() {
        return nivel;
    }

    public int obtenerExperiencia() {
        return experiencia;
    }
}