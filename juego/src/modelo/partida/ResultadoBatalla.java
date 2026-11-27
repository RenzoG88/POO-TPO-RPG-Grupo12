package modelo.partida;

/** Resumen inmutable de lo que Partida acaba de resolver y acreditar. */
public final class ResultadoBatalla {
    private final boolean victoria;
    private final boolean jefe;
    private final int ciclo;
    private final int experiencia;
    private final int creditos;

    public ResultadoBatalla(boolean victoria, boolean jefe, int ciclo,
                            int experiencia, int creditos) {
        this.victoria = victoria;
        this.jefe = jefe;
        this.ciclo = ciclo;
        this.experiencia = experiencia;
        this.creditos = creditos;
    }

    public boolean fueVictoria() { return victoria; }
    public boolean eraJefe() { return jefe; }
    public int obtenerCiclo() { return ciclo; }
    public int obtenerExperiencia() { return experiencia; }
    public int obtenerCreditos() { return creditos; }
}
