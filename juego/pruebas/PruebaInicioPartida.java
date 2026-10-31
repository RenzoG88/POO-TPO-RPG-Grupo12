import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.inventario.Herramienta;
import modelo.partida.Partida;

public class PruebaInicioPartida {

    public static void main(String[] args) {
        Partida partida = new Partida();
        Equipo equipo = partida.obtenerEquipo();
        Empleado empleado = equipo.obtenerEmpleados().get(0);

        System.out.println("Etapa: " + partida.obtenerEtapa());
        System.out.println("Ciclo: " + partida.obtenerCiclo());
        System.out.println("Estres: " + partida.obtenerEstres()
                + "/" + partida.obtenerLimiteEstres());
        System.out.println("Empleados activos: " + equipo.obtenerActivos().size());
        System.out.println("Multiplicador de experiencia: "
                + partida.obtenerMultiplicadorExperiencia());
        System.out.println("Multiplicador de creditos: "
                + partida.obtenerMultiplicadorCreditos());
        System.out.println("Guardado permitido por etapa: " + partida.puedeGuardar());
        System.out.println("Partida finalizada: " + partida.estaFinalizada());

        Herramienta teclado = new Herramienta("Teclado mecanico", 10);
        equipo.obtenerInventario().agregarItem(teclado);

        System.out.println("Equipar desde Partida: " + partida.equipar(empleado, teclado));
        System.out.println("Ataque equipado: " + empleado.obtenerAtaqueTotal());
        System.out.println("Desequipar desde Partida: "
                + partida.desequipar(empleado, teclado));
        System.out.println("Objeto recuperado: " + equipo.obtenerInventario().contiene(teclado));

        equipo.obtenerInventario().agregarCreditos(75);

        // Reiniciar crea un equipo y un inventario nuevos.
        partida.iniciarNueva();

        System.out.println("Equipo nuevo: " + (equipo != partida.obtenerEquipo()));
        System.out.println("Creditos de la nueva partida: "
                + partida.obtenerEquipo().obtenerInventario().obtenerCreditos());
        System.out.println("Objetos de la nueva partida: "
                + partida.obtenerEquipo().obtenerInventario().obtenerItems().size());
        System.out.println("Activos de la nueva partida: "
                + partida.obtenerEquipo().obtenerActivos().size());
    }
}
