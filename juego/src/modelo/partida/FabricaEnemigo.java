package modelo.partida;

import modelo.combatientes.Enemigo;

/** Crea un rival nuevo con las estadisticas del ciclo indicado. */
@FunctionalInterface
public interface FabricaEnemigo {
    Enemigo crear(int ciclo);
}
