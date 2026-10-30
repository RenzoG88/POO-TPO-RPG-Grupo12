package modelo.combate;

import modelo.combatientes.Combatiente;
import modelo.combatientes.Empleado;
import modelo.combatientes.Enemigo;

public class AtaquePotenciado extends Habilidad {
    private static final long serialVersionUID = 1L;
    private final double multiplicador;
    private final int cantidadGolpes;

    public AtaquePotenciado(String nombre, int costoEnergia,
                            double multiplicador, int cantidadGolpes) {
        super(nombre, costoEnergia);
        if (!Double.isFinite(multiplicador) || multiplicador <= 0 || cantidadGolpes <= 0) {
            throw new IllegalArgumentException("Configuracion de ataque invalida");
        }
        this.multiplicador = multiplicador;
        this.cantidadGolpes = cantidadGolpes;
    }

    @Override
    public boolean objetivoValido(Combatiente emisor, Combatiente objetivo) {
        if (emisor == null || objetivo == null
                || !emisor.estaActivo() || !objetivo.estaActivo()) {
            return false;
        }
        return (emisor instanceof Empleado && objetivo instanceof Enemigo)
                || (emisor instanceof Enemigo && objetivo instanceof Empleado);
    }

    @Override
    public void aplicar(Combatiente emisor, Combatiente objetivo) {
        if (!objetivoValido(emisor, objetivo)) {
            return;
        }
        int ataqueBruto = (int) Math.min(Integer.MAX_VALUE,
                Math.floor(emisor.obtenerAtaqueTotal() * multiplicador));
        int danio = Math.max(1, ataqueBruto - objetivo.obtenerDefensaTotal());
        for (int i = 0; i < cantidadGolpes && objetivo.estaActivo(); i++) {
            objetivo.recibirDanio(danio);
        }
    }
}
