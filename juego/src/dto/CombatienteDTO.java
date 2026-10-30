package dto;

/** Datos de una tarjeta de combate; la vista no recibe objetos del modelo. */
public class CombatienteDTO {
    private final String nombre;
    private final String imagen;
    private final int vida, vidaMaxima, energia, energiaMaxima, ataque, defensa;
    private final String estados;
    private final boolean turnoActual;

    public CombatienteDTO(String nombre, String imagen, int vida, int vidaMaxima,
                          int energia, int energiaMaxima, int ataque, int defensa,
                          String estados, boolean turnoActual) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.vida = vida;
        this.vidaMaxima = vidaMaxima;
        this.energia = energia;
        this.energiaMaxima = energiaMaxima;
        this.ataque = ataque;
        this.defensa = defensa;
        this.estados = estados;
        this.turnoActual = turnoActual;
    }

    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getEnergia() { return energia; }
    public int getEnergiaMaxima() { return energiaMaxima; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public String getEstados() { return estados; }
    public boolean isTurnoActual() { return turnoActual; }
}
