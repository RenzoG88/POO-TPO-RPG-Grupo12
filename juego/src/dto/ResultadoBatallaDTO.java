package dto;

import java.util.List;

/** Datos de presentacion; la pantalla no reparte recompensas ni modifica la partida. */
public final class ResultadoBatallaDTO {
    private final boolean hayResultado;
    private final boolean victoria;
    private final boolean jefe;
    private final int ciclo;
    private final int estres;
    private final int limiteEstres;
    private final int creditos;
    private final int experiencia;
    private final boolean mejoraPendiente;
    private final List<EmpleadoDTO> empleados;

    public ResultadoBatallaDTO(boolean hayResultado, boolean victoria, boolean jefe,
            int ciclo, int estres, int limiteEstres, int creditos, int experiencia,
            boolean mejoraPendiente, List<EmpleadoDTO> empleados) {
        this.hayResultado = hayResultado;
        this.victoria = victoria;
        this.jefe = jefe;
        this.ciclo = ciclo;
        this.estres = estres;
        this.limiteEstres = limiteEstres;
        this.creditos = creditos;
        this.experiencia = experiencia;
        this.mejoraPendiente = mejoraPendiente;
        this.empleados = List.copyOf(empleados);
    }

    public boolean hayResultado() { return hayResultado; }
    public boolean esVictoria() { return victoria; }
    public boolean esJefe() { return jefe; }
    public int getCiclo() { return ciclo; }
    public int getEstres() { return estres; }
    public int getLimiteEstres() { return limiteEstres; }
    public int getCreditos() { return creditos; }
    public int getExperiencia() { return experiencia; }
    public boolean hayMejoraPendiente() { return mejoraPendiente; }
    public List<EmpleadoDTO> getEmpleados() { return empleados; }
}
