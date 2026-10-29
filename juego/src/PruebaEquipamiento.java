import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.inventario.Herramienta;
import modelo.inventario.Inventario;

public class PruebaEquipamiento {

    public static void main(String[] args) {
        Equipo equipo = new Equipo();
        Empleado empleado = equipo.obtenerEmpleados().get(0);
        Inventario inventario = equipo.obtenerInventario();

        Herramienta teclado =
                new Herramienta("Teclado mecanico", 10);

        Herramienta tecladoMejorado =
                new Herramienta("Teclado mejorado", 20);

        inventario.agregarItem(teclado);
        inventario.agregarItem(tecladoMejorado);

        System.out.println("Ataque inicial: "
                + empleado.obtenerAtaqueTotal());

        System.out.println("Equipar: "
                + equipo.equipar(empleado, teclado));

        System.out.println("Ataque equipado: "
                + empleado.obtenerAtaqueTotal());

        System.out.println("Teclado en inventario: "
                + inventario.contiene(teclado));

        System.out.println("Cambiar: "
                + equipo.equipar(empleado, tecladoMejorado));

        System.out.println("Ataque nuevo: "
                + empleado.obtenerAtaqueTotal());

        System.out.println("Anterior recuperado: "
                + inventario.contiene(teclado));

        empleado.recibirDanio(empleado.obtenerVida());

        System.out.println("Despedido: "
                + empleado.estaDespedido());

        System.out.println("Equipar despedido: "
                + equipo.equipar(empleado, teclado));

        System.out.println("Recuperar equipo: "
                + equipo.desequipar(empleado, tecladoMejorado));

        System.out.println("Mejorado en inventario: "
                + inventario.contiene(tecladoMejorado));

        System.out.println("Desequipar otra vez: "
                + equipo.desequipar(empleado, tecladoMejorado));
    }
}
