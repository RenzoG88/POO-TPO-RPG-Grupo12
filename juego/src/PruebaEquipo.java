import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;

public class PruebaEquipo {

    public static void main(String[] args) {
        Equipo equipo = new Equipo();

        System.out.println("Empleados: " + equipo.obtenerEmpleados().size());
        System.out.println("Activos: " + equipo.obtenerActivos().size());

        Empleado primero = equipo.obtenerEmpleados().get(0);
        primero.recibirDanio(primero.obtenerVida());

        System.out.println("Despedido: " + primero.obtenerNombre());
        System.out.println("Activos ahora: " + equipo.obtenerActivos().size());
        System.out.println("Sigue en el equipo: " + equipo.pertenece(primero));
        System.out.println("Todos despedidos: " + equipo.estanTodosDespedidos());

        equipo.repartirExperiencia(100);

        for (Empleado empleado : equipo.obtenerEmpleados()) {
            System.out.println(empleado.obtenerNombre()
                    + " | Nivel: " + empleado.obtenerNivel());
        }

        equipo.obtenerInventario().agregarCreditos(50);

        System.out.println("Creditos compartidos: "
                + equipo.obtenerInventario().obtenerCreditos());
    }
}