package modelo.estadostemporales;

import modelo.combatientes.Combatiente;

public class Burnout extends EfectoEstado {
    private static final long serialVersionUID = 1L;
    private final int danioPorTurno;

    public Burnout() {
        this(3, 5);
    }

    public Burnout(int duracion, int danioPorTurno) {
        super(duracion);
        if (danioPorTurno <= 0) {
            throw new IllegalArgumentException("El danio de burnout debe ser positivo");
        }
        this.danioPorTurno = danioPorTurno;
    }

    @Override
    public TipoEstado obtenerTipo() {
        return TipoEstado.BURNOUT;
    }

    @Override
    public void alIniciarTurno(Combatiente afectado) {
        if (afectado != null && afectado.estaActivo()) {
            // No resta la defensa numerica; recibirDanio aplica las reducciones.
            afectado.recibirDanio(danioPorTurno);
        }
    }
}
