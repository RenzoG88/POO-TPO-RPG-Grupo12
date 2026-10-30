package modelo.partida;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PersistenciaPartida {

    public void guardar(Partida partida, String ruta) throws IOException {
        if (partida == null) {
            throw new IllegalArgumentException("No hay una partida para guardar.");
        }

        if (!partida.puedeGuardar()) {
            throw new IllegalStateException("La etapa actual no permite guardar.");
        }

        Path destino = obtenerRuta(ruta);
        Path temporal = Files.createTempFile(destino.getParent(), "partida-", ".tmp");

        try {
            // Primero se termina de escribir; despues se reemplaza el guardado.
            try (ObjectOutputStream salida =
                         new ObjectOutputStream(Files.newOutputStream(temporal))) {
                salida.writeObject(partida);
            }

            try {
                Files.move(temporal, destino, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException error) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    public Partida cargar(String ruta) throws IOException, ClassNotFoundException {
        Path archivo = obtenerRuta(ruta);

        if (Files.size(archivo) > 5_000_000) {
            throw new IOException("El archivo supera el tamanio permitido.");
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(Files.newInputStream(archivo))) {
            // Solo admite objetos del modelo y los tipos auxiliares utilizados.
            entrada.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "maxdepth=40;maxrefs=20000;maxarray=10000;maxbytes=5000000;"
                            + "modelo.**;java.util.ArrayList;java.lang.Object;"
                            + "java.lang.String;java.lang.Enum;!*"
            ));

            Object objeto = entrada.readObject();

            if (!(objeto instanceof Partida)) {
                throw new IOException("El archivo no contiene una partida.");
            }

            Partida cargada = (Partida) objeto;

            if (!cargada.puedeGuardar() || cargada.obtenerEquipo() == null
                    || cargada.obtenerEquipo().obtenerInventario() == null
                    || cargada.obtenerTienda() == null) {
                throw new IOException("El archivo no contiene una partida valida.");
            }

            // No se llama iniciarNueva: se devuelve el estado que estaba guardado.
            return cargada;
        }
    }

    private Path obtenerRuta(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("Falta seleccionar el archivo.");
        }

        return Paths.get(ruta).toAbsolutePath();
    }
}
