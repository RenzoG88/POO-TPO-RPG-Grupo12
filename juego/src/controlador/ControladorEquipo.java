package controlador;

import dto.EmpleadoDTO;
import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import vista.PantallaEquipo;

import java.util.ArrayList;
import java.util.List;

public class ControladorEquipo {

    private final Equipo equipo;
    private final PantallaEquipo vista;

    public ControladorEquipo(Equipo equipo, PantallaEquipo vista) {
        this.equipo = equipo;
        this.vista = vista;
    }

    public void refrescarVista() {
        List<EmpleadoDTO> datos = new ArrayList<>();

        for (Empleado empleado : equipo.obtenerEmpleados()) {
            datos.add(new EmpleadoDTO(
                    empleado.obtenerNombre(),
                    empleado.obtenerNivel(),
                    empleado.obtenerExperiencia(),
                    empleado.obtenerVida(),
                    empleado.obtenerVidaMaxima(),
                    empleado.obtenerEnergia(),
                    empleado.obtenerEnergiaMaxima(),
                    empleado.estaDespedido()
            ));
        }

        vista.mostrarEquipo(
                datos,
                equipo.obtenerInventario().obtenerCreditos()
        );
    }
}