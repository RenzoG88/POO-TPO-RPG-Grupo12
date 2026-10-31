package controlador;

import dto.ObjetoDTO;
import modelo.combatientes.Empleado;
import modelo.inventario.*;
import modelo.partida.EtapaPartida;
import modelo.partida.Partida;
import vista.organizador.PantallaEquipo;
import vista.organizador.PantallaObjetos;
import java.util.ArrayList;
import java.util.List;

/** Traduce compras y usos de tarjetas a operaciones del modelo existente. */
public class ControladorObjetos {
    private final Partida partida;
    private final PantallaObjetos vista;
    private final Runnable alCambiar;
    private final boolean esTienda;
    private List<Oferta> ofertas = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

    public ControladorObjetos(Partida partida, PantallaEquipo propietario,
                             boolean esTienda, Runnable alCambiar) {
        this.partida = partida;
        this.esTienda = esTienda;
        this.alCambiar = alCambiar;
        vista = new PantallaObjetos(propietario, esTienda ? "Tienda del equipo" : "Inventario compartido");
        vista.obtenerPanel().conectarAccion(this::elegirObjeto);
    }

    public void mostrar() {
        actualizar();
        vista.setVisible(true);
    }

    private void actualizar() {
        int saldo = partida.obtenerEquipo().obtenerInventario().obtenerCreditos();
        boolean preparacion = partida.obtenerEtapa() == EtapaPartida.PREPARACION;
        List<ObjetoDTO> tarjetas = new ArrayList<>();
        if (esTienda) {
            ofertas = partida.obtenerTienda().obtenerOfertas();
            for (Oferta oferta : ofertas) {
                boolean habilitada = preparacion && oferta.estaDisponible() && saldo >= oferta.obtenerPrecio();
                String estado = !oferta.estaDisponible() ? "VENDIDA"
                        : !preparacion ? "Disponible en preparación"
                        : saldo < oferta.obtenerPrecio() ? "Créditos insuficientes" : "Disponible";
                tarjetas.add(crearDatos(oferta.obtenerItem(), oferta.obtenerPrecio(), estado,
                        oferta.estaDisponible() ? "Comprar" : "Vendida", habilitada));
            }
        } else {
            items = partida.obtenerEquipo().obtenerInventario().obtenerItems();
            for (Item item : items) {
                boolean utilizable = item instanceof Consumible || item instanceof Equipamiento;
                tarjetas.add(crearDatos(item, -1,
                        preparacion ? "Disponible para el equipo" : "Solo consulta en esta etapa",
                        item instanceof Consumible ? "Usar" : "Equipar", preparacion && utilizable));
            }
        }
        vista.obtenerPanel().actualizar(tarjetas, saldo, esTienda
                ? "Cada oferta vende una unidad. El catálogo se renueva al avanzar, no al abrir la tienda."
                : "Objetos disponibles. Lo equipado aparece en la ficha de cada empleado.");
    }

    private void elegirObjeto(int indice) {
        if (esTienda) {
            if (indice < 0 || indice >= ofertas.size()) { return; }
            Oferta oferta = ofertas.get(indice);
            boolean compra = partida.comprar(oferta);
            vista.obtenerPanel().mostrarMensaje(compra
                    ? "Compraste " + oferta.obtenerItem().obtenerNombre() + ". Ya está en el inventario."
                    : "No se pudo comprar. Revisá los créditos y la disponibilidad.");
        } else {
            if (indice < 0 || indice >= items.size()) { return; }
            Item item = items.get(indice);
            List<Empleado> empleados = partida.obtenerEquipo().obtenerActivos();
            String[] nombres = new String[empleados.size()];
            for (int i = 0; i < empleados.size(); i++) {
                Empleado e = empleados.get(i);
                nombres[i] = e.obtenerNombre() + " | Vida " + e.obtenerVida() + "/" + e.obtenerVidaMaxima()
                        + " | EN " + e.obtenerEnergia() + "/" + e.obtenerEnergiaMaxima();
            }
            int elegido = vista.elegirEmpleado(nombres);
            if (elegido < 0) { return; }
            Empleado empleado = empleados.get(elegido);
            boolean aplicado = item instanceof Consumible
                    ? partida.usarConsumible((Consumible) item, empleado)
                    : item instanceof Equipamiento && partida.equipar(empleado, (Equipamiento) item);
            vista.obtenerPanel().mostrarMensaje(aplicado
                    ? item.obtenerNombre() + " aplicado a " + empleado.obtenerNombre() + "."
                    : "No se usó el objeto. Revisá la etapa y si el empleado necesita su efecto.");
        }
        actualizar();
        alCambiar.run();
    }

    private ObjetoDTO crearDatos(Item item, int precio, String estado, String accion, boolean habilitado) {
        String imagen = "";
        String descripcion = "Objeto del equipo";
        if (item instanceof Snack) {
            imagen = "snack.png";
            descripcion = "Recupera " + ((Snack) item).obtenerRecuperacionVida() + " de vida";
        } else if (item instanceof Cafe) {
            imagen = "cafe.png";
            descripcion = "Recupera " + ((Cafe) item).obtenerRecuperacionEnergia() + " de energía";
        } else if (item instanceof Herramienta) {
            imagen = "herramienta.png";
            descripcion = "Ataque +" + ((Herramienta) item).obtenerBonusAtaque();
        } else if (item instanceof Proteccion) {
            imagen = "proteccion.png";
            descripcion = "Defensa +" + ((Proteccion) item).obtenerBonusDefensa();
        }
        return new ObjetoDTO(item.obtenerNombre(), descripcion, imagen, precio, estado, accion, habilitado);
    }
}
