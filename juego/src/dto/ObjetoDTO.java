package dto;

/** Datos de una tarjeta. El precio solo se muestra en la tienda. */
public class ObjetoDTO {
    private final String nombre;
    private final String descripcion;
    private final String imagen;
    private final int precio;
    private final String estado;
    private final String accion;
    private final boolean habilitado;

    public ObjetoDTO(String nombre, String descripcion, String imagen, int precio,
                     String estado, String accion, boolean habilitado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precio = precio;
        this.estado = estado;
        this.accion = accion;
        this.habilitado = habilitado;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
    public int getPrecio() { return precio; }
    public String getEstado() { return estado; }
    public String getAccion() { return accion; }
    public boolean isHabilitado() { return habilitado; }
}
