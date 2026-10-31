package dto;

/** Información para mostrar el orden sin darle combatientes a la vista. */
public class TurnoDTO {
    private final int posicion;
    private final String nombre;
    private final int velocidad;
    private final String estado;
    private final boolean actual;

    public TurnoDTO(int posicion, String nombre, int velocidad, String estado, boolean actual) {
        this.posicion = posicion;
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.estado = estado;
        this.actual = actual;
    }

    public int getPosicion() { return posicion; }
    public String getNombre() { return nombre; }
    public int getVelocidad() { return velocidad; }
    public String getEstado() { return estado; }
    public boolean isActual() { return actual; }
}
