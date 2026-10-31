package dto;

public class EmpleadoDTO {

    private final String nombre;
    private final int nivel;
    private final int experiencia;
    private final int experienciaSiguienteNivel;
    private final int vidaActual;
    private final int vidaMaxima;
    private final int energiaActual;
    private final int energiaMaxima;
    private final boolean despedido;
    private final int ataque;
    private final int defensa;
    private final String herramienta;
    private final String proteccion;

    public EmpleadoDTO(String nombre, int nivel, int experiencia,
                       int vidaActual, int vidaMaxima,
                       int energiaActual, int energiaMaxima,
                       boolean despedido, int ataque, int defensa,
                       String herramienta, String proteccion,
                       int experienciaSiguienteNivel) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.experiencia = experiencia;
        this.experienciaSiguienteNivel = experienciaSiguienteNivel;
        this.vidaActual = vidaActual;
        this.vidaMaxima = vidaMaxima;
        this.energiaActual = energiaActual;
        this.energiaMaxima = energiaMaxima;
        this.despedido = despedido;
        this.ataque = ataque;
        this.defensa = defensa;
        this.herramienta = herramienta;
        this.proteccion = proteccion;
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

    public int getExperienciaSiguienteNivel() {
        return experienciaSiguienteNivel;
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

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public String getHerramienta() {
        return herramienta;
    }

    public String getProteccion() {
        return proteccion;
    }
}
