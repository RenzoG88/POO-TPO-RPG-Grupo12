package vista.recursos;

import modelo.combatientes.TipoPersonaje;
import java.util.Map;

/** La presentacion decide que archivo representa a cada identidad del juego. */
public final class SpritesPersonajes {
    private static final Map<TipoPersonaje, String> ARCHIVOS = Map.of(
            TipoPersonaje.BACKEND, "backend.png",
            TipoPersonaje.FRONTEND, "frontend.png",
            TipoPersonaje.CIBERSEGURIDAD, "ciberseguridad.png",
            TipoPersonaje.INFRAESTRUCTURA, "infraestructura.png",
            TipoPersonaje.PHISHING, "usuariophishing.png",
            TipoPersonaje.CLIENTE, "clienteexigente.png",
            TipoPersonaje.BUROCRATA, "burocrata.png",
            TipoPersonaje.JEFE, "jefeempresa.png");

    private SpritesPersonajes() { }

    public static String archivoPara(TipoPersonaje tipo) {
        return tipo == null ? "" : ARCHIVOS.getOrDefault(tipo, "");
    }
}
