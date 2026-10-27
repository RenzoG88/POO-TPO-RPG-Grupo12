package dto;

public class EmpleadoDTO {

    private final String nombre;
    private final int nivel;
    private final int experiencia;
    private final int vidaActual;
    private final int vidaMaxima;
    private final int energiaActual;
    private final int energiaMaxima;
    private final boolean despedido;

    public EmpleadoDTO(String nombre, int nivel, int experiencia,
                       int vidaActual, int vidaMaxima,
                       int energiaActual, int energiaMaxima,
                       boolean despedido) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.experiencia = experiencia;
        this.vidaActual = vidaActual;
        this.vidaMaxima = vidaMaxima;
        this.energiaActual = energiaActual;
        this.energiaMaxima = energiaMaxima;
        this.despedido = despedido;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getEnergiaActual() {
        return energiaActual;
    }

    public int getEnergiaMaxima() {
        return energiaMaxima;
    }

    public boolean isDespedido() {
        return despedido;
    }
}