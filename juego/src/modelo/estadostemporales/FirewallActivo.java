package modelo.estadostemporales;

public class FirewallActivo extends EfectoEstado {
    private static final long serialVersionUID = 1L;
    private final int porcentajeReduccion;

    public FirewallActivo() {
        this(2, 25);
    }

    public FirewallActivo(int duracion, int porcentajeReduccion) {
        super(duracion);
        if (porcentajeReduccion < 0 || porcentajeReduccion > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        this.porcentajeReduccion = porcentajeReduccion;
    }

    @Override
    public TipoEstado obtenerTipo() {
        return TipoEstado.FIREWALL_ACTIVO;
    }

    @Override
    public int modificarDanioRecibido(int cantidad) {
        if (cantidad <= 0) {
            return cantidad;
        }
        return (int) Math.max(1, (long) cantidad * (100 - porcentajeReduccion) / 100);
    }
}
