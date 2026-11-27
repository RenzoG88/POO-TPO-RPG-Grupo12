package modelo.partida;

import modelo.combatientes.Enemigo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GeneradorEncuentros {
    private final List<FabricaEnemigo> normales;
    private final FabricaEnemigo jefe;
    private final Random azar = new Random();

    public GeneradorEncuentros() {
        this(FabricasEnemigos.normales(), FabricasEnemigos::crearJefe);
    }

    // Se agrega otro tipo de rival sin modificar el algoritmo del generador.
    public GeneradorEncuentros(List<FabricaEnemigo> normales, FabricaEnemigo jefe) {
        if (normales == null || normales.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos una fabrica de enemigos");
        }
        this.normales = List.copyOf(normales);
        this.jefe = Objects.requireNonNull(jefe, "Falta la fabrica del jefe");
    }

    public List<Enemigo> crearEnemigos(int ciclo, boolean esJefe) {
        if (ciclo < 1) { throw new IllegalArgumentException("El ciclo debe ser positivo"); }
        List<Enemigo> enemigos = new ArrayList<>();
        if (esJefe) {
            enemigos.add(jefe.crear(ciclo));
        } else {
            for (int i = 0; i < 2; i++) {
                FabricaEnemigo fabrica = normales.get(azar.nextInt(normales.size()));
                enemigos.add(fabrica.crear(ciclo));
            }
        }
        return enemigos;
    }
}
