import modelo.combate.Habilidad;
import modelo.combatientes.Empleado;
import modelo.combatientes.Equipo;
import modelo.combatientes.UsuarioPhishing;
import modelo.estadostemporales.TicketPendiente;
import modelo.estadostemporales.TipoEstado;
import modelo.inventario.Herramienta;
import modelo.partida.Partida;
import modelo.partida.PersistenciaPartida;
import java.nio.file.Files;
import java.nio.file.Path;

public class PruebaHabilidadesEmpleados {
    public static void main(String[] args) throws Exception {
        Partida partida = new Partida();
        Equipo equipo = partida.obtenerEquipo();
        Empleado ciber = equipo.obtenerEmpleados().get(0);
        Empleado infra = equipo.obtenerEmpleados().get(1);
        Empleado backend = equipo.obtenerEmpleados().get(2);
        Empleado frontend = equipo.obtenerEmpleados().get(3);

        for (Empleado empleado : equipo.obtenerEmpleados()) {
            verificar(empleado.obtenerHabilidades().size() == 1,
                    empleado.obtenerNombre() + " tiene una habilidad inicial");
            Habilidad habilidad = empleado.obtenerHabilidades().get(0);
            verificar(habilidad.obtenerCostoEnergia() == 10,
                    habilidad.obtenerNombre() + " cuesta 10 de energia");
        }

        Habilidad firewall = ciber.obtenerHabilidades().get(0);
        verificar(ciber.usarHabilidad(firewall, ciber) && ciber.obtenerEnergia() == 50,
                "Ciberseguridad puede protegerse y paga el costo una vez");
        verificar(ciber.obtenerEstados().size() == 1
                        && ciber.obtenerEstados().get(0).obtenerTipo() == TipoEstado.FIREWALL_ACTIVO
                        && ciber.obtenerEstados().get(0).obtenerDuracion() == 2,
                "Firewall empieza con dos turnos de duracion");
        ciber.recibirDanio(20);
        verificar(ciber.obtenerVida() == 145, "Firewall reduce 20 de danio a 15");
        ciber.finalizarTurno();
        ciber.iniciarTurno();
        ciber.finalizarTurno();
        verificar(ciber.obtenerEstados().isEmpty(), "Firewall expira tras dos cierres propios");
        verificar(ciber.usarHabilidad(firewall, backend), "Firewall tambien protege a otro empleado");
        backend.limpiarEstados();

        Habilidad reparar = infra.obtenerHabilidades().get(0);
        ciber.recibirDanio(25);
        verificar(infra.usarHabilidad(reparar, ciber) && ciber.obtenerVida() == 150
                        && infra.obtenerEnergia() == 90,
                "Infraestructura recupera 30 de vida y paga 10 de energia");
        verificar(infra.usarHabilidad(reparar, ciber) && ciber.obtenerVida() == 160,
                "Reparacion respeta la vida maxima");
        int energiaInfra = infra.obtenerEnergia();
        verificar(!infra.usarHabilidad(reparar, ciber) && infra.obtenerEnergia() == energiaInfra,
                "No se gasta energia si el objetivo tiene toda su vida");
        infra.recibirDanio(40);
        verificar(infra.usarHabilidad(reparar, infra) && infra.obtenerVida() == 110,
                "Infraestructura tambien puede repararse a si mismo");

        Habilidad proceso = backend.obtenerHabilidades().get(0);
        UsuarioPhishing rivalBackend = new UsuarioPhishing();
        verificar(backend.usarHabilidad(proceso, rivalBackend)
                        && rivalBackend.obtenerVida() == 35 && backend.obtenerEnergia() == 70,
                "Backend causa 45: floor(28 x 1.8) - 5, costo unico de 10");
        Herramienta herramienta = new Herramienta("Teclado mejorado", 10);
        equipo.obtenerInventario().agregarItem(herramienta);
        verificar(equipo.equipar(backend, herramienta), "Se equipa el teclado de prueba");
        UsuarioPhishing rivalEquipado = new UsuarioPhishing();
        verificar(backend.usarHabilidad(proceso, rivalEquipado)
                        && rivalEquipado.obtenerVida() == 17,
                "La habilidad de Backend incluye el bonus de la herramienta");

        Habilidad doble = frontend.obtenerHabilidades().get(0);
        UsuarioPhishing rivalFrontend = new UsuarioPhishing();
        verificar(frontend.usarHabilidad(doble, rivalFrontend)
                        && rivalFrontend.obtenerVida() == 56 && frontend.obtenerEnergia() == 70,
                "Frontend causa dos golpes de 12 y paga una sola vez");

        int energiaBackend = backend.obtenerEnergia();
        verificar(!backend.usarHabilidad(proceso, ciber)
                        && !backend.usarHabilidad(doble, rivalFrontend)
                        && backend.obtenerEnergia() == energiaBackend,
                "Se rechazan ataque a un aliado y habilidad ajena sin gastar energia");
        int energiaCiber = ciber.obtenerEnergia();
        verificar(!ciber.usarHabilidad(firewall, rivalFrontend)
                        && ciber.obtenerEnergia() == energiaCiber,
                "Firewall no protege a un enemigo");
        energiaInfra = infra.obtenerEnergia();
        verificar(!infra.usarHabilidad(reparar, rivalFrontend)
                        && !infra.usarHabilidad(reparar, null)
                        && infra.obtenerEnergia() == energiaInfra,
                "Reparacion rechaza enemigos y objetivos inexistentes sin cobrar");

        frontend.gastarEnergia(frontend.obtenerEnergia());
        verificar(!frontend.usarHabilidad(doble, rivalFrontend) && rivalFrontend.obtenerVida() == 56,
                "Sin energia no se ejecuta la habilidad");
        backend.agregarEstado(new TicketPendiente());
        verificar(!backend.usarHabilidad(proceso, rivalFrontend)
                        && backend.obtenerEnergia() == energiaBackend,
                "Ticket pendiente bloquea la habilidad sin gastar energia");
        backend.limpiarEstados();
        frontend.recibirDanio(1000);
        verificar(!infra.usarHabilidad(reparar, frontend) && frontend.estaDespedido(),
                "Reparacion no reincorpora a un empleado despedido");

        Path archivo = Files.createTempFile("habilidades-empleados-", ".rpg");
        try {
            PersistenciaPartida persistencia = new PersistenciaPartida();
            persistencia.guardar(partida, archivo.toString());
            Partida cargada = persistencia.cargar(archivo.toString());
            persistencia.guardar(cargada, archivo.toString());
            cargada = persistencia.cargar(archivo.toString());
            for (int i = 0; i < equipo.obtenerEmpleados().size(); i++) {
                Empleado antes = equipo.obtenerEmpleados().get(i);
                Empleado despues = cargada.obtenerEquipo().obtenerEmpleados().get(i);
                verificar(despues.obtenerHabilidades().size() == 1
                                && despues.obtenerHabilidades().get(0).obtenerNombre()
                                .equals(antes.obtenerHabilidades().get(0).obtenerNombre())
                                && despues.obtenerVida() == antes.obtenerVida()
                                && despues.obtenerEnergia() == antes.obtenerEnergia(),
                        "Guardar/cargar conserva " + antes.obtenerNombre() + " sin duplicar habilidades");
            }
            Empleado infraCargado = cargada.obtenerEquipo().obtenerEmpleados().get(1);
            verificar(infraCargado.usarHabilidad(infraCargado.obtenerHabilidades().get(0), infraCargado)
                            && infraCargado.obtenerVida() == 120,
                    "La habilidad recuperada del guardado sigue funcionando");
        } finally {
            Files.deleteIfExists(archivo);
        }
        System.out.println("Habilidades iniciales de empleados: OK.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalStateException("FALLO: " + mensaje);
        System.out.println("OK: " + mensaje);
    }
}
