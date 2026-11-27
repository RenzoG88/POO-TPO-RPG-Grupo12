package modelo.partida;

import modelo.combatientes.Burocrata;
import modelo.combatientes.ClienteExigente;
import modelo.combatientes.Enemigo;
import modelo.combatientes.JefeEmpresa;
import modelo.combatientes.UsuarioPhishing;
import java.util.List;

/** Catalogo de fabricas. Conserva el balance de la version entregada. */
public final class FabricasEnemigos {
    private FabricasEnemigos() { }

    public static List<FabricaEnemigo> normales() {
        return List.of(FabricasEnemigos::crearPhishing,
                FabricasEnemigos::crearCliente, FabricasEnemigos::crearBurocrata);
    }

    public static Enemigo crearPhishing(int ciclo) {
        return new UsuarioPhishing(escalar(UsuarioPhishing.VIDA_BASE, ciclo),
                escalar(UsuarioPhishing.ATAQUE_BASE, ciclo),
                escalar(UsuarioPhishing.DEFENSA_BASE, ciclo));
    }

    public static Enemigo crearCliente(int ciclo) {
        return new ClienteExigente(escalar(ClienteExigente.VIDA_BASE, ciclo),
                escalar(ClienteExigente.ATAQUE_BASE, ciclo),
                escalar(ClienteExigente.DEFENSA_BASE, ciclo));
    }

    public static Enemigo crearBurocrata(int ciclo) {
        return new Burocrata(escalar(Burocrata.VIDA_BASE, ciclo),
                escalar(Burocrata.ATAQUE_BASE, ciclo),
                escalar(Burocrata.DEFENSA_BASE, ciclo));
    }

    public static Enemigo crearJefe(int ciclo) {
        return new JefeEmpresa(escalar(JefeEmpresa.VIDA_BASE, ciclo),
                escalar(JefeEmpresa.ATAQUE_BASE, ciclo),
                escalar(JefeEmpresa.DEFENSA_BASE, ciclo));
    }

    private static int escalar(int base, int ciclo) {
        if (ciclo < 1) { throw new IllegalArgumentException("El ciclo debe ser positivo"); }
        long porcentaje = 100L + 15L * (ciclo - 1);
        long resultado = (base * porcentaje + 99) / 100;
        if (resultado > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("El ciclo excede las estadisticas admitidas");
        }
        return (int) resultado;
    }
}
