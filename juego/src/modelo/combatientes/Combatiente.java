package modelo.combatientes;

import java.io.Serializable;

public abstract class Combatiente implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int vida;
    private int vidaMaxima;
    private int energia;
    private int energiaMaxima;
    private int ataque;
    private int defensa;
    private int velocidad;
    private boolean defendiendo;

    public Combatiente(String nombre, int vidaMaxima,
                       int energiaMaxima, int ataque,
                       int defensa, int velocidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (vidaMaxima <= 0) {
            throw new IllegalArgumentException("La vida maxima debe ser positiva");
        }
        if (energiaMaxima < 0 || ataque < 0 || defensa < 0 || velocidad < 0) {
            throw new IllegalArgumentException("Las estadisticas no pueden ser negativas");
        }

        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.energiaMaxima = energiaMaxima;
        this.energia = energiaMaxima;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }

    public boolean estaActivo() {
        return vida > 0;
    }

    public void recibirDanio(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("El danio no puede ser negativo");
        }

        if (!estaActivo() || cantidad == 0) {
            return;
        }

        if (defendiendo) {
            cantidad = Math.max(1, cantidad / 2);
        }

        vida = Math.max(0, vida - cantidad);
    }

    public void atacar(Combatiente objetivo) {
        if (!estaActivo() || objetivo == null || objetivo == this
                || !objetivo.estaActivo()) {
            return;
        }

        int danio = Math.max(1,
                obtenerAtaqueTotal() - objetivo.obtenerDefensaTotal());

        objetivo.recibirDanio(danio);
    }

    public void defender() {
        if (estaActivo()) {
            defendiendo = true;
        }
    }

    public void iniciarTurno() {
        // La defensa anterior termina al comenzar el siguiente turno propio.
        defendiendo = false;
    }

    public void curar(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La curacion no puede ser negativa");
        }
        if (!estaActivo()) {
            return;
        }
        vida += Math.min(cantidad, vidaMaxima - vida);
    }

    public void recuperarEnergia(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La recuperacion no puede ser negativa");
        }
        if (!estaActivo()) {
            return;
        }
        energia += Math.min(cantidad, energiaMaxima - energia);
    }

    public boolean gastarEnergia(int cantidad) {
        if (!estaActivo() || cantidad < 0 || cantidad > energia) {
            return false;
        }
        energia -= cantidad;
        return true;
    }

    public int obtenerAtaqueTotal() {
        return ataque;
    }

    public int obtenerDefensaTotal() {
        return defensa;
    }

    public int obtenerVelocidad() {
        return velocidad;
    }

    protected void mejorarEstadisticas(int aumentoVida, int aumentoEnergia,
                                       int aumentoAtaque, int aumentoDefensa) {
        if (aumentoVida < 0 || aumentoEnergia < 0
                || aumentoAtaque < 0 || aumentoDefensa < 0) {
            throw new IllegalArgumentException("Los aumentos no pueden ser negativos");
        }

        vidaMaxima += aumentoVida;
        energiaMaxima += aumentoEnergia;
        ataque += aumentoAtaque;
        defensa += aumentoDefensa;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerVida() {
        return vida;
    }

    public int obtenerVidaMaxima() {
        return vidaMaxima;
    }

    public int obtenerEnergia() {
        return energia;
    }

    public int obtenerEnergiaMaxima() {
        return energiaMaxima;
    }
}
