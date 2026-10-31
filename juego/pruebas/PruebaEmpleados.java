import modelo.combatientes.Backend;
import modelo.combatientes.Ciberseguridad;
import modelo.combatientes.Empleado;
import modelo.combatientes.Frontend;
import modelo.combatientes.Infraestructura;

public class PruebaEmpleados {

    public static void main(String[] args) {
        Backend backend = new Backend();

        Empleado[] empleados = {
                new Ciberseguridad(),
                new Infraestructura(),
                backend,
                new Frontend()
        };

        for (Empleado empleado : empleados) {
            System.out.println(
                    empleado.obtenerNombre()
                            + " | Vida: " + empleado.obtenerVida()
                            + " | Energia: " + empleado.obtenerEnergia()
                            + " | Ataque: " + empleado.obtenerAtaqueTotal()
                            + " | Defensa: " + empleado.obtenerDefensaTotal()
                            + " | Velocidad: " + empleado.obtenerVelocidad()
            );
        }

        backend.recibirDanio(30);
        backend.gastarEnergia(20);
        backend.ganarExperiencia(350);

        System.out.println("--- Backend despues de ganar experiencia ---");
        System.out.println("Nivel: " + backend.obtenerNivel());
        System.out.println("Experiencia restante: "
                + backend.obtenerExperiencia());
        System.out.println("Vida: " + backend.obtenerVida()
                + "/" + backend.obtenerVidaMaxima());
        System.out.println("Energia: " + backend.obtenerEnergia()
                + "/" + backend.obtenerEnergiaMaxima());
    }
}