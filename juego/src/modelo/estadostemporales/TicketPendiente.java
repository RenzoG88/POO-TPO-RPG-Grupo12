package modelo.estadostemporales;

public class TicketPendiente extends EfectoEstado {
    private static final long serialVersionUID = 1L;

    public TicketPendiente() {
        this(1);
    }

    public TicketPendiente(int duracion) {
        super(duracion);
    }

    @Override
    public TipoEstado obtenerTipo() {
        return TipoEstado.TICKET_PENDIENTE;
    }

    @Override
    public boolean permiteActuar() {
        return false;
    }
}
