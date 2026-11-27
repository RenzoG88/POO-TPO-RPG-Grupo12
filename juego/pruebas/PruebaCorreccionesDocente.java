import controlador.DatosEquipo;
import controlador.SesionPartida;
import dto.ResultadoBatallaDTO;
import modelo.combate.Batalla;
import modelo.combatientes.*;
import modelo.estadostemporales.*;
import modelo.inventario.*;
import modelo.partida.*;
import vista.paneles.PanelResultadoBatalla;
import vista.recursos.SpritesPersonajes;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Regresion de las cinco correcciones. Ejecutar como cualquier clase Prueba*. */
public class PruebaCorreccionesDocente {
    public static void main(String[] args) throws Exception {
        probarEquipamiento();
        probarFabricas();
        probarSprites();
        probarResultados();
        probarSesion();
        if (args.length > 0) { probarGuardadoAnterior(args[0]); }
        System.out.println("Correcciones del docente: OK.");
    }

    private static void probarEquipamiento() {
        Equipo equipo = new Equipo();
        Empleado primero = equipo.obtenerEmpleados().get(0);
        Empleado segundo = equipo.obtenerEmpleados().get(1);
        Inventario inventario = equipo.obtenerInventario();
        Herramienta vieja = new Herramienta("Anterior", 5);
        Herramienta nueva = new Herramienta("Nueva", 10);
        Proteccion proteccion = new Proteccion("Auriculares", 4);
        inventario.agregarItem(vieja);
        inventario.agregarItem(nueva);
        inventario.agregarItem(proteccion);
        verificar(equipo.equipar(primero, vieja) && equipo.equipar(primero, proteccion),
                "El polimorfismo equipa cada objeto en su casilla");
        verificar(equipo.equipar(primero, nueva) && inventario.contiene(vieja)
                        && primero.obtenerHerramienta() == nueva
                        && primero.obtenerProteccion() == proteccion,
                "Cambiar herramienta devuelve la anterior y conserva la proteccion");
        verificar(!equipo.equipar(segundo, nueva) && !equipo.desequipar(segundo, nueva),
                "El mismo equipo no se duplica ni se retira de otro empleado");
        verificar(!equipo.equipar(new Backend(), vieja) && inventario.contiene(vieja),
                "Rechaza empleados ajenos sin consumir el objeto");
        primero.recibirDanio(10000);
        verificar(!equipo.equipar(primero, vieja) && equipo.desequipar(primero, nueva)
                        && equipo.desequipar(primero, proteccion)
                        && !equipo.desequipar(primero, proteccion),
                "Se recupera equipo de un despedido una sola vez");
    }

    private static void probarFabricas() {
        // Un rival adicional, inyectado sin cambiar GeneradorEncuentros.
        FabricaEnemigo fabrica = ciclo -> new RivalDePrueba(40 * ciclo);
        GeneradorEncuentros generador = new GeneradorEncuentros(
                List.of(fabrica), FabricasEnemigos::crearJefe);
        List<Enemigo> rivales = generador.crearEnemigos(3, false);
        verificar(rivales.size() == 2 && rivales.get(0) instanceof RivalDePrueba
                        && rivales.get(0).obtenerVidaMaxima() == 120
                        && rivales.get(0) != rivales.get(1),
                "El generador admite una fabrica externa y crea instancias independientes");
        verificar(generador.crearEnemigos(2, true).get(0).obtenerVidaMaxima()
                        == (JefeEmpresa.VIDA_BASE * 115 + 99) / 100,
                "El jefe conserva el escalado de la entrega");
        for (TipoEstado tipo : TipoEstado.values()) {
            EfectoEstado a = tipo.crearEfecto(2, 5);
            EfectoEstado b = tipo.crearEfecto(2, 5);
            a.alFinalizarTurno();
            verificar(a != b && a.obtenerTipo() == tipo && b.obtenerDuracion() == 2,
                    "La fabrica de " + tipo + " no comparte duraciones entre personajes");
        }
    }

    private static void probarSprites() {
        // Otro nombre de clase sigue usando la identidad heredada de Frontend.
        Frontend personaje = new FrontendRenombrado();
        verificar("frontend.png".equals(SpritesPersonajes.archivoPara(personaje.obtenerTipoPersonaje())),
                "Cambiar el nombre de una clase no cambia su sprite");
        for (TipoPersonaje tipo : TipoPersonaje.values()) {
            if (tipo == TipoPersonaje.DESCONOCIDO) { continue; }
            String archivo = SpritesPersonajes.archivoPara(tipo);
            verificar(PruebaCorreccionesDocente.class.getResource("/recursos/personajes/" + archivo) != null,
                    "Existe el recurso registrado para " + tipo);
        }
    }

    private static void probarResultados() throws Exception {
        Partida partida = new Partida();
        partida.obtenerEquipo().obtenerEmpleados().get(3).recibirDanio(10000);
        verificar(partida.procesarFinCombate() == null, "Sin batalla no se produce un resultado");
        ResultadoBatalla resultado = null;
        for (int i = 0; i < 6; i++) {
            Batalla batalla = partida.iniciarSiguienteCombate();
            verificar(partida.procesarFinCombate() == null, "No se premia una batalla pendiente");
            for (Enemigo enemigo : batalla.obtenerEnemigos()) { enemigo.recibirDanio(10000); }
            resultado = partida.procesarFinCombate();
            int premio = i == 5 ? 80 : 30;
            verificar(resultado.fueVictoria() && resultado.obtenerCreditos() == premio
                            && resultado.obtenerExperiencia() == (i == 5 ? 100 : 40),
                    "El resultado contiene los premios reales del combate " + (i + 1));
            verificar(partida.procesarFinCombate() == null,
                    "Consultar otra vez no genera otro resultado ni acredita premios");
        }
        verificar(resultado.eraJefe() && partida.obtenerEstres() == 5
                        && partida.obtenerEquipo().obtenerInventario().obtenerCreditos() == 230
                        && partida.obtenerEquipo().obtenerEmpleados().get(3).obtenerExperiencia() == 0,
                "El jefe deja la mejora pendiente y el despedido no gana experiencia");

        Partida pendiente = partida;
        ResultadoBatalla ganado = resultado;
        SwingUtilities.invokeAndWait(() -> {
            ResultadoBatallaDTO datos = datos(ganado, pendiente);
            PanelResultadoBatalla panel = new PanelResultadoBatalla(datos);
            final boolean[] cerrado = {false};
            panel.conectarContinuar(() -> cerrado[0] = true);
            panel.conectarMejoras(() -> pendiente.elegirMejora(TipoMejora.EXPERIENCIA),
                    () -> pendiente.elegirMejora(TipoMejora.CREDITOS));
            boton(panel, "Elegir después").doClick();
            verificar(cerrado[0] && pendiente.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA,
                    "Salir de resultados conserva la eleccion pendiente");
        });
        Path archivo = Files.createTempFile("resultado-pendiente-", ".rpg");
        try {
            PersistenciaPartida persistencia = new PersistenciaPartida();
            persistencia.guardar(pendiente, archivo.toString());
            Partida cargada = persistencia.cargar(archivo.toString());
            verificar(cargada.procesarFinCombate() == null
                            && cargada.obtenerEquipo().obtenerInventario().obtenerCreditos() == 230,
                    "Cargar la mejora pendiente no vuelve a cobrar el jefe");
            SwingUtilities.invokeAndWait(() -> {
                PanelResultadoBatalla panel = new PanelResultadoBatalla(datos(null, cargada));
                panel.conectarMejoras(() -> cargada.elegirMejora(TipoMejora.EXPERIENCIA),
                        () -> cargada.elegirMejora(TipoMejora.CREDITOS));
                boton(panel, "Mejorar créditos (+20%)").doClick();
                boton(panel, "Mejorar créditos (+20%)").doClick();
                verificar(cargada.obtenerCiclo() == 2 && cargada.obtenerEstres() == 0
                                && Math.abs(cargada.obtenerMultiplicadorCreditos() - 1.2) < 0.0001,
                        "La accion de mejora inicia el siguiente ciclo una sola vez");
            });
        } finally {
            Files.deleteIfExists(archivo);
        }
        Partida derrota = new Partida();
        derrota.iniciarSiguienteCombate();
        for (Empleado empleado : derrota.obtenerEquipo().obtenerEmpleados()) {
            empleado.recibirDanio(10000);
        }
        ResultadoBatalla perdido = derrota.procesarFinCombate();
        verificar(!perdido.fueVictoria() && perdido.obtenerCreditos() == 0
                        && perdido.obtenerExperiencia() == 0 && derrota.estaFinalizada(),
                "El resultado de derrota termina la partida sin premios");
    }

    private static ResultadoBatallaDTO datos(ResultadoBatalla resultado, Partida partida) {
        return new ResultadoBatallaDTO(resultado != null,
                resultado != null && resultado.fueVictoria(),
                resultado != null && resultado.eraJefe(), partida.obtenerCiclo(),
                partida.obtenerEstres(), partida.obtenerLimiteEstres(),
                resultado == null ? 0 : resultado.obtenerCreditos(),
                resultado == null ? 0 : resultado.obtenerExperiencia(),
                partida.obtenerEtapa() == EtapaPartida.ELECCION_MEJORA,
                DatosEquipo.crear(partida.obtenerEquipo()));
    }

    private static void probarSesion() {
        Partida original = new Partida();
        SesionPartida sesion = new SesionPartida(original);
        Partida cargada = new Partida();
        cargada.obtenerEquipo().obtenerInventario().agregarCreditos(77);
        sesion.reemplazarPartida(cargada);
        verificar(sesion.obtenerPartida() == cargada
                        && sesion.obtenerPartida().obtenerEquipo().obtenerInventario().obtenerCreditos() == 77
                        && original.obtenerEquipo().obtenerInventario().obtenerCreditos() == 0,
                "La sesion apunta a la partida cargada y deja de operar sobre la anterior");
    }

    private static void probarGuardadoAnterior(String ruta) throws Exception {
        Partida cargada = new PersistenciaPartida().cargar(ruta);
        Empleado empleado = cargada.obtenerEquipo().obtenerEmpleados().get(0);
        verificar(cargada.obtenerEquipo().obtenerInventario().obtenerCreditos() == 123
                        && empleado.obtenerNivel() == 2 && empleado.obtenerExperiencia() == 20
                        && empleado.obtenerVida() == 130
                        && empleado.obtenerHerramienta().obtenerBonusAtaque() == 10
                        && empleado.obtenerProteccion().obtenerBonusDefensa() == 5,
                "Carga el archivo creado por la version entregada sin perder equipo ni progreso");
        verificar(cargada.desequipar(empleado, empleado.obtenerHerramienta()),
                "El equipamiento antiguo conserva su comportamiento");
        Path nuevo = Files.createTempFile("guardado-migrado-", ".rpg");
        try {
            PersistenciaPartida persistencia = new PersistenciaPartida();
            persistencia.guardar(cargada, nuevo.toString());
            verificar(persistencia.cargar(nuevo.toString()).obtenerEquipo().obtenerInventario()
                            .obtenerCreditos() == 123,
                    "Se puede volver a guardar y cargar la partida antigua");
        } finally {
            Files.deleteIfExists(nuevo);
        }
    }

    private static JButton boton(Container raiz, String texto) {
        for (Component componente : raiz.getComponents()) {
            if (componente instanceof JButton && ((JButton) componente).getText().equals(texto)) {
                return (JButton) componente;
            }
            if (componente instanceof Container) {
                JButton encontrado = buscarBoton((Container) componente, texto);
                if (encontrado != null) { return encontrado; }
            }
        }
        throw new IllegalStateException("No se encontro el boton " + texto);
    }

    private static JButton buscarBoton(Container raiz, String texto) {
        for (Component componente : raiz.getComponents()) {
            if (componente instanceof JButton && ((JButton) componente).getText().equals(texto)) {
                return (JButton) componente;
            }
            if (componente instanceof Container) {
                JButton encontrado = buscarBoton((Container) componente, texto);
                if (encontrado != null) { return encontrado; }
            }
        }
        return null;
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) { throw new IllegalStateException("FALLO: " + mensaje); }
        System.out.println("OK: " + mensaje);
    }

    private static class RivalDePrueba extends Enemigo {
        RivalDePrueba(int vida) { super("Rival de prueba", vida, 0, 1, 0, 1); }
        @Override public void ejecutarAccion(Equipo equipo) { }
    }

    private static class FrontendRenombrado extends Frontend { }
}
