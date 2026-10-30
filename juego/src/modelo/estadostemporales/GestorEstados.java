package modelo.estadostemporales;

import modelo.combatientes.Combatiente;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GestorEstados implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<EfectoEstado> estados = new ArrayList<>();

    public void agregar(EfectoEstado nuevo) {
        if (nuevo == null || nuevo.haExpirado()) {
            throw new IllegalArgumentException("El estado debe existir y estar vigente");
        }
        for (EfectoEstado actual : estados) {
            if (actual.obtenerTipo() == nuevo.obtenerTipo()) {
                // Mantiene el objeto y su potencia; solo extiende su duracion.
                actual.sumarDuracion(nuevo.obtenerDuracion());
                return;
            }
        }
        estados.add(nuevo);
    }

    public void procesarInicio(Combatiente afectado) {
        for (EfectoEstado estado : estados) {
            if (!afectado.estaActivo()) {
                break;
            }
            estado.alIniciarTurno(afectado);
        }
    }

    public boolean permiteActuar() {
        for (EfectoEstado estado : estados) {
            if (!estado.permiteActuar()) {
                return false;
            }
        }
        return true;
    }

    public int modificarDanio(int cantidad) {
        for (EfectoEstado estado : estados) {
            cantidad = estado.modificarDanioRecibido(cantidad);
        }
        return cantidad;
    }

    public void finalizarTurno() {
        Iterator<EfectoEstado> iterador = estados.iterator();
        while (iterador.hasNext()) {
            EfectoEstado estado = iterador.next();
            estado.alFinalizarTurno();
            if (estado.haExpirado()) {
                iterador.remove();
            }
        }
    }

    public void limpiar() {
        estados.clear();
    }

    public List<EfectoEstado> obtenerEstados() {
        return new ArrayList<>(estados);
    }
}
