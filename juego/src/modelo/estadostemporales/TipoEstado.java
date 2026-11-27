package modelo.estadostemporales;

public enum TipoEstado {
    BURNOUT {
        @Override
        public EfectoEstado crearEfecto(int duracion, int potencia) {
            return new Burnout(duracion, potencia);
        }
    },
    TICKET_PENDIENTE {
        @Override
        public EfectoEstado crearEfecto(int duracion, int potencia) {
            return new TicketPendiente(duracion);
        }
    },
    FIREWALL_ACTIVO {
        @Override
        public EfectoEstado crearEfecto(int duracion, int potencia) {
            return new FirewallActivo(duracion, potencia);
        }
    };

    // Cada tipo fabrica una instancia nueva; las habilidades no conocen las clases concretas.
    public abstract EfectoEstado crearEfecto(int duracion, int potencia);
}
