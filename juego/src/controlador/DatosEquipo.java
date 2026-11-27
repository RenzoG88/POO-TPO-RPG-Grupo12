package controlador;

import dto.EmpleadoDTO;
import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import java.util.ArrayList;
import java.util.List;

/** Conversion compartida para preparacion y resultados; no cambia el modelo. */
public final class DatosEquipo {
    private DatosEquipo() { }

    public static List<EmpleadoDTO> crear(Equipo equipo) {
        List<EmpleadoDTO> datos = new ArrayList<>();

        for (Empleado empleado : equipo.obtenerEmpleados()) {
            String herramienta = empleado.obtenerHerramienta() == null
                    ? "Sin herramienta" : empleado.obtenerHerramienta().obtenerNombre();
            String proteccion = empleado.obtenerProteccion() == null
                    ? "Sin proteccion" : empleado.obtenerProteccion().obtenerNombre();

            datos.add(new EmpleadoDTO(
                    empleado.obtenerNombre(),
                    empleado.obtenerNivel(),
                    empleado.obtenerExperiencia(),
                    empleado.obtenerVida(),
                    empleado.obtenerVidaMaxima(),
                    empleado.obtenerEnergia(),
                    empleado.obtenerEnergiaMaxima(),
                    empleado.estaDespedido(),
                    empleado.obtenerAtaqueTotal(),
                    empleado.obtenerDefensaTotal(),
                    herramienta,
                    proteccion,
                    empleado.obtenerExperienciaSiguienteNivel()
            ));
        }

        return datos;
    }
}
