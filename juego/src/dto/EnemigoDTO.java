package dto;

/** Datos que necesita la vista para mostrar un enemigo. */
public class EnemigoDTO {
    private final String nombre;
    private final String archivoImagen;
    private final int vidaActual;
    private final int vidaMaxima;
    private final int energiaActual;
    private final int energiaMaxima;
    private final int ataque;
    private final int defensa;
    private final int velocidad;

    public EnemigoDTO(String nombre, String archivoImagen,
                      int vidaActual, int vidaMaxima,
                      int energiaActual, int energiaMaxima,
                      int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.archivoImagen = archivoImagen;
        this.vidaActual = vidaActual;
        this.vidaMaxima = vidaMaxima;
        this.energiaActual = energiaActual;
        this.energiaMaxima = energiaMaxima;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }

    public String getNombre() { return nombre; }
    public String getArchivoImagen() { return archivoImagen; }
    public int getVidaActual() { return vidaActual; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getEnergiaActual() { return energiaActual; }
    public int getEnergiaMaxima() { return energiaMaxima; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }
}
