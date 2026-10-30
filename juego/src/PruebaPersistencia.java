import modelo.combatientes.Ciberseguridad;
import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.inventario.Herramienta;
import modelo.inventario.Inventario;
import modelo.inventario.Proteccion;
import modelo.partida.EtapaPartida;
import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PruebaPersistencia {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path carpeta = Files.createTempDirectory("prueba-rpg-");
        Path archivo = carpeta.resolve("partida.rpg");
        Path archivoDanado = carpeta.resolve("danado.rpg");

        try {
            Partida original = new Partida();
            Equipo equipo = original.obtenerEquipo();
            Empleado empleado = equipo.obtenerEmpleados().get(0);
            Inventario inventario = equipo.obtenerInventario();

            Herramienta teclado = new Herramienta("Teclado mecanico", 10);
            Herramienta reserva = new Herramienta("Teclado mejorado", 20);
            Proteccion auriculares = new Proteccion("Auriculares aislantes", 5);

            inventario.agregarCreditos(120);
            inventario.agregarItem(teclado);
            inventario.agregarItem(reserva);
            inventario.agregarItem(auriculares);
            original.equipar(empleado, teclado);
            original.equipar(empleado, auriculares);

            empleado.recibirDanio(25);
            empleado.gastarEnergia(10);
            empleado.ganarExperiencia(150);
            equipo.obtenerEmpleados().get(3).recibirDanio(1000);

            PersistenciaPartida persistencia = new PersistenciaPartida();
            persistencia.guardar(original, archivo.toString());

            // Cambiamos la partida abierta despues de guardarla.
            inventario.agregarCreditos(99);
            empleado.recibirDanio(1);
            original.desequipar(empleado, teclado);

            Partida cargada = persistencia.cargar(archivo.toString());
            Empleado recuperado = cargada.obtenerEquipo().obtenerEmpleados().get(0);
            Inventario inventarioCargado = cargada.obtenerEquipo().obtenerInventario();

            verificar(cargada != original, "La carga crea otra instancia de Partida");
            verificar(cargada.obtenerEtapa() == EtapaPartida.PREPARACION
                            && cargada.obtenerCiclo() == 1 && cargada.obtenerEstres() == 0,
                    "Se conserva la etapa, el ciclo y el estres");
            verificar(cargada.obtenerMultiplicadorExperiencia() == 1.0
                            && cargada.obtenerMultiplicadorCreditos() == 1.0,
                    "Se conservan los multiplicadores");
            verificar(recuperado instanceof Ciberseguridad,
                    "Se conserva el tipo de empleado");
            verificar(recuperado.obtenerNivel() == 2
                            && recuperado.obtenerExperiencia() == 50,
                    "Nivel 2 y experiencia 50");
            verificar(recuperado.obtenerVida() == 135
                            && recuperado.obtenerVidaMaxima() == 170,
                    "Vida 135/170, sin curacion al cargar");
            verificar(recuperado.obtenerEnergia() == 50
                            && recuperado.obtenerEnergiaMaxima() == 65,
                    "Energia 50/65, sin recarga al cargar");
            verificar(inventarioCargado.obtenerCreditos() == 120,
                    "Se recuperan los 120 creditos guardados");
            verificar(recuperado.obtenerAtaqueTotal() == 30
                            && recuperado.obtenerDefensaTotal() == 22,
                    "Se conservan herramienta y proteccion equipadas");
            verificar(inventarioCargado.obtenerItems().size() == 1
                            && !inventarioCargado.contiene(recuperado.obtenerHerramienta())
                            && !inventarioCargado.contiene(recuperado.obtenerProteccion()),
                    "El equipamiento no se duplica en el inventario");
            verificar(cargada.obtenerEquipo().obtenerEmpleados().get(3).estaDespedido()
                            && cargada.obtenerEquipo().obtenerActivos().size() == 3,
                    "El empleado despedido sigue despedido");

            Herramienta herramientaCargada = recuperado.obtenerHerramienta();
            verificar(cargada.desequipar(recuperado, herramientaCargada)
                            && inventarioCargado.contiene(herramientaCargada),
                    "Se puede recuperar el equipo despues de cargar");

            // Volver a guardar en la misma ruta tambien debe funcionar.
            persistencia.guardar(cargada, archivo.toString());
            Partida segundaCarga = persistencia.cargar(archivo.toString());
            verificar(segundaCarga.obtenerEquipo().obtenerInventario()
                            .obtenerItems().size() == 2,
                    "Se puede actualizar un guardado existente");

            Files.writeString(archivoDanado, "Esto no es una partida serializada");
            Partida enUso = cargada;
            boolean rechazado = false;

            try {
                // La asignacion ocurre solamente si cargar termina correctamente.
                enUso = persistencia.cargar(archivoDanado.toString());
            } catch (IOException error) {
                rechazado = true;
            }

            verificar(rechazado && enUso == cargada,
                    "Una carga fallida conserva la partida abierta");
            System.out.println("Prueba de persistencia completada correctamente.");
        } finally {
            Files.deleteIfExists(archivo);
            Files.deleteIfExists(archivoDanado);
            Files.deleteIfExists(carpeta);
        }
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) {
            throw new IllegalStateException("FALLO: " + descripcion);
        }

        System.out.println("OK: " + descripcion);
    }
}
