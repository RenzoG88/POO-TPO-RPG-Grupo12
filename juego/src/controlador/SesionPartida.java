package controlador;

import modelo.partida.Partida;
import java.util.Objects;

/** Referencia compartida: al cargar, todos los controladores usan la nueva partida. */
public final class SesionPartida {
    private Partida partida;

    public SesionPartida(Partida partida) {
        reemplazarPartida(partida);
    }

    public Partida obtenerPartida() { return partida; }

    public void reemplazarPartida(Partida partida) {
        this.partida = Objects.requireNonNull(partida, "La partida es obligatoria");
    }
}
