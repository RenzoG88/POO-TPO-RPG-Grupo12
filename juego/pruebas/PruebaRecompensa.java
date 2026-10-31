import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.partida.EtapaPartida;
import modelo.partida.Recompensa;
import modelo.partida.TipoMejora;

public class PruebaRecompensa {

    public static void main(String[] args) {
        System.out.println("Etapa de ejemplo: " + EtapaPartida.PREPARACION);
        System.out.println("Mejora de ejemplo: " + TipoMejora.EXPERIENCIA);

        Equipo equipo = new Equipo();
        Empleado ciberseguridad = equipo.obtenerEmpleados().get(0);
        Empleado frontend = equipo.obtenerEmpleados().get(3);

        // Simulamos el estado que dejo un combate. Son datos de prueba.
        ciberseguridad.recibirDanio(10);
        ciberseguridad.gastarEnergia(10);
        frontend.recibirDanio(frontend.obtenerVida());

        // Estos valores son ejemplos; no fijan las recompensas del juego.
        Recompensa recompensa = new Recompensa(100, 50);
        int experienciaGanada = recompensa.calcularExperiencia(1.2);
        int creditosGanados = recompensa.calcularCreditos(1.4);

        System.out.println("Experiencia calculada: " + experienciaGanada);
        System.out.println("Creditos calculados: " + creditosGanados);
        System.out.println("Saldo antes de entregar: "
                + equipo.obtenerInventario().obtenerCreditos());

        // En el juego, Partida coordinara esta entrega una sola vez por victoria.
        equipo.repartirExperiencia(experienciaGanada);
        equipo.obtenerInventario().agregarCreditos(creditosGanados);

        System.out.println("Saldo despues de entregar: "
                + equipo.obtenerInventario().obtenerCreditos());

        for (Empleado empleado : equipo.obtenerEmpleados()) {
            System.out.println(empleado.obtenerNombre()
                    + " | Nivel: " + empleado.obtenerNivel()
                    + " | XP: " + empleado.obtenerExperiencia()
                    + " | Despedido: " + empleado.estaDespedido());
        }

        System.out.println("Vida de Ciberseguridad: "
                + ciberseguridad.obtenerVida() + "/"
                + ciberseguridad.obtenerVidaMaxima());
        System.out.println("Energia de Ciberseguridad: "
                + ciberseguridad.obtenerEnergia() + "/"
                + ciberseguridad.obtenerEnergiaMaxima());
    }
}
