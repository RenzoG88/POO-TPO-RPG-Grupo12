package modelo.partida;

import modelo.combatientes.Burocrata;
import modelo.combatientes.ClienteExigente;
import modelo.combatientes.Enemigo;
import modelo.combatientes.JefeEmpresa;
import modelo.combatientes.UsuarioPhishing;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneradorEncuentros {

    public List<Enemigo> crearEnemigos(int ciclo, boolean esJefe) {
        if (ciclo < 1) {
            throw new IllegalArgumentException("El ciclo debe ser positivo");
        }
        List<Enemigo> enemigos = new ArrayList<>();
        if (esJefe) {
            enemigos.add(new JefeEmpresa(escalar(JefeEmpresa.VIDA_BASE, ciclo),
                    escalar(JefeEmpresa.ATAQUE_BASE, ciclo),
                    escalar(JefeEmpresa.DEFENSA_BASE, ciclo)));
        } else {
            // Balance inicial: dos rivales normales, cada uno con su propia instancia.
            Random azar = new Random();
            for (int i = 0; i < 2; i++) {
                enemigos.add(crearNormal(azar.nextInt(3), ciclo));
            }
        }
        return enemigos;
    }

    private Enemigo crearNormal(int tipo, int ciclo) {
        switch (tipo) {
            case 0:
                return new UsuarioPhishing(escalar(UsuarioPhishing.VIDA_BASE, ciclo),
                        escalar(UsuarioPhishing.ATAQUE_BASE, ciclo),
                        escalar(UsuarioPhishing.DEFENSA_BASE, ciclo));
            case 1:
                return new ClienteExigente(escalar(ClienteExigente.VIDA_BASE, ciclo),
                        escalar(ClienteExigente.ATAQUE_BASE, ciclo),
                        escalar(ClienteExigente.DEFENSA_BASE, ciclo));
            default:
                return new Burocrata(escalar(Burocrata.VIDA_BASE, ciclo),
                        escalar(Burocrata.ATAQUE_BASE, ciclo),
                        escalar(Burocrata.DEFENSA_BASE, ciclo));
        }
    }

    private int escalar(int base, int ciclo) {
        // Porcentaje con enteros: evita errores de coma flotante al redondear hacia arriba.
        long porcentaje = 100L + 15L * (ciclo - 1);
        long resultado = (base * porcentaje + 99) / 100;
        if (resultado > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("El ciclo excede las estadisticas admitidas");
        }
        return (int) resultado;
    }
}
