import modelo.combate.AplicarEstado;
import modelo.combate.AtaquePotenciado;
import modelo.combatientes.Backend;
import modelo.combatientes.Burocrata;
import modelo.combatientes.ClienteExigente;
import modelo.combatientes.Empleado;
import modelo.combatientes.Enemigo;
import modelo.combatientes.Equipo;
import modelo.combatientes.JefeEmpresa;
import modelo.combatientes.UsuarioPhishing;
import modelo.estadostemporales.Burnout;
import modelo.estadostemporales.FirewallActivo;
import modelo.estadostemporales.TicketPendiente;
import modelo.estadostemporales.TipoEstado;
import modelo.partida.GeneradorEncuentros;
import java.util.List;

public class PruebaEnemigos {
    public static void main(String[] args) {
        Equipo equipo = new Equipo();
        Empleado ciber = equipo.obtenerEmpleados().get(0);
        UsuarioPhishing phishing = new UsuarioPhishing();
        phishing.ejecutarAccion(equipo);
        verificar(ciber.obtenerVida() == 152, "Phishing ataca al primer empleado activo");

        equipo = new Equipo();
        ciber = equipo.obtenerEmpleados().get(0);
        ClienteExigente cliente = new ClienteExigente();
        cliente.ejecutarAccion(equipo);
        cliente.ejecutarAccion(equipo);
        verificar(ciber.obtenerVida() == 154 && cliente.obtenerEnergia() == 30
                        && ciber.obtenerEstados().get(0).obtenerTipo() == TipoEstado.BURNOUT,
                "Cliente alterna ataque y burnout de costo 10");
        ciber.iniciarTurno();
        ciber.finalizarTurno();
        verificar(ciber.obtenerVida() == 149 && ciber.obtenerEstados().get(0).obtenerDuracion() == 2,
                "Burnout quita 5 al inicio propio y descuenta duracion al cierre");

        equipo = new Equipo();
        ciber = equipo.obtenerEmpleados().get(0);
        Burocrata burocrata = new Burocrata();
        burocrata.ejecutarAccion(equipo);
        burocrata.ejecutarAccion(equipo);
        ciber.iniciarTurno();
        int vidaRival = burocrata.obtenerVida();
        ciber.atacar(burocrata);
        verificar(!ciber.puedeActuar() && burocrata.obtenerVida() == vidaRival,
                "El ticket bloquea la accion del afectado");
        ciber.finalizarTurno();
        ciber.iniciarTurno();
        verificar(ciber.puedeActuar() && ciber.obtenerEstados().isEmpty(),
                "El turno perdido tambien vence el ticket");

        equipo = new Equipo();
        ciber = equipo.obtenerEmpleados().get(0);
        JefeEmpresa jefe = new JefeEmpresa();
        jefe.ejecutarAccion(equipo);
        jefe.ejecutarAccion(equipo);
        verificar(ciber.obtenerVida() == 124 && jefe.obtenerEnergia() == 40
                        && ciber.obtenerEstados().get(0).obtenerTipo() == TipoEstado.BURNOUT,
                "El jefe alterna golpe x1.5 y burnout, con consumo de energia");
        ciber.recibirDanio(1000);
        jefe.ejecutarAccion(equipo);
        verificar(equipo.obtenerEmpleados().get(1).obtenerVida() == 76,
                "Los enemigos cambian de objetivo cuando el anterior es despedido");
        jefe.recibirDanio(10000);
        jefe.ejecutarAccion(equipo);
        verificar(equipo.obtenerEmpleados().get(1).obtenerVida() == 76,
                "Un enemigo derrotado no actua");

        equipo = new Equipo();
        ciber = equipo.obtenerEmpleados().get(0);
        cliente = new ClienteExigente();
        cliente.gastarEnergia(40);
        cliente.ejecutarAccion(equipo);
        cliente.ejecutarAccion(equipo);
        verificar(ciber.obtenerVida() == 148 && ciber.obtenerEstados().isEmpty(),
                "Sin energia la habilidad enemiga se reemplaza por ataque basico");
        cliente = new ClienteExigente();
        cliente.agregarEstado(new TicketPendiente());
        cliente.iniciarTurno();
        cliente.ejecutarAccion(equipo);
        cliente.finalizarTurno();
        verificar(ciber.obtenerVida() == 148, "Un enemigo bloqueado tampoco puede actuar");
        cliente.iniciarTurno();
        cliente.ejecutarAccion(equipo);
        verificar(ciber.obtenerVida() == 142 && ciber.obtenerEstados().isEmpty(),
                "Perder el turno no avanza la alternancia del enemigo");

        AplicarEstado ataqueBurnout = new AplicarEstado("Burnout", 10, TipoEstado.BURNOUT, 3, 5);
        ataqueBurnout.aplicar(ciber, cliente);
        verificar(!ataqueBurnout.objetivoValido(ciber, cliente) && cliente.obtenerEstados().isEmpty()
                        && !ciber.usarHabilidad(cliente.obtenerHabilidades().get(0), cliente),
                "Un empleado no aplica burnout ni puede usar una habilidad ajena");

        ciber = new Equipo().obtenerEmpleados().get(0);
        ciber.agregarEstado(new Burnout(3, 5));
        ciber.agregarEstado(new Burnout(2, 99));
        verificar(ciber.obtenerEstados().size() == 1
                        && ciber.obtenerEstados().get(0).obtenerDuracion() == 5,
                "Reaplicar suma duracion sin duplicar el estado");
        for (int i = 0; i < 5; i++) {
            ciber.iniciarTurno();
            ciber.finalizarTurno();
        }
        verificar(ciber.obtenerVida() == 135 && ciber.obtenerEstados().isEmpty(),
                "Se conserva la potencia original de 5 y el estado expira");
        ciber.agregarEstado(new FirewallActivo());
        ciber.defender();
        ciber.recibirDanio(20);
        verificar(ciber.obtenerVida() == 128, "Defender y firewall reducen sucesivamente: 20 a 10 a 7");
        ciber.iniciarTurno();
        ciber.recibirDanio(4);
        ciber.finalizarTurno();
        verificar(ciber.obtenerVida() == 125 && ciber.obtenerEstados().get(0).obtenerDuracion() == 1,
                "El inicio quita Defender y el cierre reduce la duracion de firewall");
        ciber.limpiarEstados();
        ciber.recibirDanio(4);
        verificar(ciber.obtenerVida() == 121 && ciber.obtenerEstados().isEmpty(),
                "Limpiar estados elimina las reducciones sin curar");
        ciber.recibirDanio(118);
        ciber.agregarEstado(new Burnout());
        ciber.iniciarTurno();
        verificar(ciber.estaDespedido() && !ciber.puedeActuar(),
                "Burnout puede despedir antes de la accion");

        EnemigoContador objetivo = new EnemigoContador();
        new AtaquePotenciado("Doble", 10, 1, 2).aplicar(new Backend(), objetivo);
        verificar(objetivo.golpes == 1 && !objetivo.estaActivo(),
                "Un ataque multiple se detiene al derrotar al objetivo");

        GeneradorEncuentros generador = new GeneradorEncuentros();
        List<Enemigo> normales = generador.crearEnemigos(2, false);
        verificar(normales.size() == 2 && normales.get(0) != normales.get(1)
                        && !(normales.get(0) instanceof JefeEmpresa)
                        && !(normales.get(1) instanceof JefeEmpresa),
                "Cada encuentro normal crea dos instancias de enemigos normales");
        Enemigo jefeEscalado = generador.crearEnemigos(2, true).get(0);
        verificar(jefeEscalado instanceof JefeEmpresa
                        && jefeEscalado.obtenerVidaMaxima() == 387
                        && jefeEscalado.obtenerAtaqueTotal() == 41
                        && jefeEscalado.obtenerDefensaTotal() == 12,
                "El ciclo 2 escala vida, ataque y defensa por 1.15, hacia arriba");
        verificar(generador.crearEnemigos(2, true).get(0) != jefeEscalado,
                "El generador nunca reutiliza el enemigo de un encuentro anterior");
        System.out.println("Enemigos, habilidades y estados: OK.");
    }

    private static class EnemigoContador extends UsuarioPhishing {
        private int golpes;
        private EnemigoContador() {
            super(10, 1, 0);
        }
        @Override
        public void recibirDanio(int cantidad) {
            golpes++;
            super.recibirDanio(cantidad);
        }
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalStateException("FALLO: " + mensaje);
        }
        System.out.println("OK: " + mensaje);
    }
}
