package estructuras;

import java.util.Stack;
import excepciones.PilaDeshacerVaciaException;

/**
 * Administra el sistema de Deshacer (Undo) y Rehacer (Redo) de la plataforma
 * utilizando dos estructuras tipo Pila (Stack).
 * Cumple con el requerimiento obligatorio de la rúbrica sobre Pilas.
 */
public class HistorialOperaciones {
    private final Stack<Operacion> pilaDeshacer;
    private final Stack<Operacion> pilaRehacer;

    public HistorialOperaciones() {
        this.pilaDeshacer = new Stack<>();
        this.pilaRehacer = new Stack<>();
    }

    /**
     * Registra una nueva operación en la pila de deshacer y vacía la pila de rehacer.
     * @param operacion Operación ejecutada exitosamente.
     */
    public void registrarOperacion(Operacion operacion) {
        if (operacion != null) {
            pilaDeshacer.push(operacion);
            pilaRehacer.clear(); // Una nueva acción invalida el árbol futuro de rehacer
        }
    }

    /**
     * Revierte la última operación ejecutada.
     * @return Mensaje informativo de la acción deshecha.
     * @throws PilaDeshacerVaciaException Si no hay acciones en la pila de deshacer.
     */
    public String deshacer() throws PilaDeshacerVaciaException {
        if (pilaDeshacer.isEmpty()) {
            throw new PilaDeshacerVaciaException("No hay operaciones para deshacer");
        }
        Operacion op = pilaDeshacer.pop();
        try {
            String mensaje = op.deshacer();
            pilaRehacer.push(op);
            return mensaje;
        } catch (Exception e) {
            // Reintegrar a la pila en caso de error inesperado
            pilaDeshacer.push(op);
            throw new RuntimeException("Error al revertir la operación: " + e.getMessage(), e);
        }
    }

    /**
     * Vuelve a aplicar la última operación que fue deshecha.
     * @return Mensaje informativo de la acción rehecha.
     * @throws PilaDeshacerVaciaException Si no hay acciones en la pila de rehacer.
     */
    public String rehacer() throws PilaDeshacerVaciaException {
        if (pilaRehacer.isEmpty()) {
            throw new PilaDeshacerVaciaException("No hay operaciones para rehacer");
        }
        Operacion op = pilaRehacer.pop();
        try {
            String mensaje = op.rehacer();
            pilaDeshacer.push(op);
            return mensaje;
        } catch (Exception e) {
            pilaRehacer.push(op);
            throw new RuntimeException("Error al reaplicar la operación: " + e.getMessage(), e);
        }
    }

    public int getCantidadDeshacer() {
        return pilaDeshacer.size();
    }

    public int getCantidadRehacer() {
        return pilaRehacer.size();
    }

    public boolean tieneDeshacer() {
        return !pilaDeshacer.isEmpty();
    }

    public boolean tieneRehacer() {
        return !pilaRehacer.isEmpty();
    }

    public void limpiar() {
        pilaDeshacer.clear();
        pilaRehacer.clear();
    }
}

