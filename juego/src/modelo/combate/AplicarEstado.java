package modelo.combate;

import modelo.combatientes.Combatiente;
import modelo.combatientes.Empleado;
import modelo.combatientes.Enemigo;
import modelo.estadostemporales.TipoEstado;

public class AplicarEstado extends Habilidad {
    private static final long serialVersionUID = 1L;
    private final TipoEstado tipo;
    private final int duracion;
    private final int potencia;

    public AplicarEstado(String nombre, int costoEnergia, TipoEstado tipo,
                         int duracion, int potencia) {
        super(nombre, costoEnergia);
        if (tipo == null || duracion <= 0 || potencia < 0
                || (tipo == TipoEstado.BURNOUT && potencia == 0)
                || (tipo == TipoEstado.FIREWALL_ACTIVO && potencia > 100)) {
            throw new IllegalArgumentException("Configuracion de estado invalida");
        }
        this.tipo = tipo;
        this.duracion = duracion;
        this.potencia = potencia;
    }

    @Override
    public boolean objetivoValido(Combatiente emisor, Combatiente objetivo) {
        if (emisor == null || objetivo == null
                || !emisor.estaActivo() || !objetivo.estaActivo()) {
            return false;
        }
        if (tipo == TipoEstado.FIREWALL_ACTIVO) {
            return emisor instanceof Empleado && objetivo instanceof Empleado;
        }
        // Burnout y TicketPendiente son ataques de los rivales al equipo.
        return emisor instanceof Enemigo && objetivo instanceof Empleado;
    }

    @Override
    public void aplicar(Combatiente emisor, Combatiente objetivo) {
        if (objetivoValido(emisor, objetivo)) {
            objetivo.agregarEstado(tipo.crearEfecto(duracion, potencia));
        }
    }

}
