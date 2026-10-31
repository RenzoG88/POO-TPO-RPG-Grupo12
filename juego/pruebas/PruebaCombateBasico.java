import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.combatientes.UsuarioPhishing;
import modelo.inventario.Herramienta;

public class PruebaCombateBasico {

    public static void main(String[] args) {
        Equipo equipo = new Equipo();
        Empleado ciberseguridad = equipo.obtenerEmpleados().get(0);
        Empleado infraestructura = equipo.obtenerEmpleados().get(1);
        UsuarioPhishing enemigo = new UsuarioPhishing();

        System.out.println("Vida inicial de Ciberseguridad: "
                + ciberseguridad.obtenerVida());
        System.out.println("Vida inicial del enemigo: " + enemigo.obtenerVida());

        ciberseguridad.atacar(enemigo);
        System.out.println("Enemigo despues del ataque: " + enemigo.obtenerVida());

        enemigo.ejecutarAccion(equipo);
        System.out.println("Ciberseguridad despues del ataque: "
                + ciberseguridad.obtenerVida());

        ciberseguridad.defender();
        enemigo.ejecutarAccion(equipo);
        System.out.println("Ciberseguridad defendiendo: "
                + ciberseguridad.obtenerVida());

        // Simulamos el comienzo del siguiente turno propio.
        ciberseguridad.iniciarTurno();
        enemigo.ejecutarAccion(equipo);
        System.out.println("Ciberseguridad sin defensa temporal: "
                + ciberseguridad.obtenerVida());

        Herramienta teclado = new Herramienta("Teclado mecanico", 10);
        equipo.obtenerInventario().agregarItem(teclado);
        equipo.equipar(ciberseguridad, teclado);
        ciberseguridad.atacar(enemigo);
        System.out.println("Enemigo despues del ataque equipado: "
                + enemigo.obtenerVida());

        // Simulamos un despido para comprobar la seleccion del objetivo.
        ciberseguridad.recibirDanio(ciberseguridad.obtenerVida());
        System.out.println("Ciberseguridad despedido: "
                + ciberseguridad.estaDespedido());

        enemigo.ejecutarAccion(equipo);
        System.out.println("Infraestructura despues del ataque: "
                + infraestructura.obtenerVida());

        ciberseguridad.atacar(enemigo);
        System.out.println("Enemigo despues del intento del despedido: "
                + enemigo.obtenerVida());

        enemigo.recibirDanio(enemigo.obtenerVida());
        enemigo.ejecutarAccion(equipo);
        System.out.println("Enemigo activo: " + enemigo.estaActivo());
        System.out.println("Vida final de Infraestructura: "
                + infraestructura.obtenerVida());
    }
}
