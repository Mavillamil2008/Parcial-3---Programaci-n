package estructuras;

import java.util.Stack;

/**
 * Permite navegar en el historial de reportes visualizados por el usuario
 * utilizando una arquitectura de doble Pila (Stack) similar a los botones
 * "Atrás" y "Adelante" de un navegador web.
 */
public class NavegadorReportes {
    private final Stack<String> pilaAtras;
    private final Stack<String> pilaAdelante;
    private String reporteActual;

    public NavegadorReportes() {
        this.pilaAtras = new Stack<>();
        this.pilaAdelante = new Stack<>();
        this.reporteActual = null;
    }

    /**
     * Registra un nuevo reporte que el usuario acaba de generar o consultar.
     * @param reporte Contenido en texto del reporte.
     */
    public void registrarReporte(String reporte) {
        if (reporte != null && !reporte.trim().isEmpty()) {
            if (reporteActual != null) {
                pilaAtras.push(reporteActual);
            }
            reporteActual = reporte;
            pilaAdelante.clear();
        }
    }

    /**
     * Retrocede al reporte anterior guardado en la pila de historial.
     * @return El reporte anterior, o null si no hay ninguno.
     */
    public String retroceder() {
        if (pilaAtras.isEmpty()) {
            return null;
        }
        pilaAdelante.push(reporteActual);
        reporteActual = pilaAtras.pop();
        return reporteActual;
    }

    /**
     * Avanza al reporte siguiente guardado en la pila de avance.
     * @return El reporte siguiente, o null si no hay ninguno.
     */
    public String avanzar() {
        if (pilaAdelante.isEmpty()) {
            return null;
        }
        pilaAtras.push(reporteActual);
        reporteActual = pilaAdelante.pop();
        return reporteActual;
    }

    public boolean puedeRetroceder() {
        return !pilaAtras.isEmpty();
    }

    public boolean puedeAvanzar() {
        return !pilaAdelante.isEmpty();
    }

    public String getReporteActual() {
        return reporteActual;
    }

    public int getHistorialAtrasCount() {
        return pilaAtras.size();
    }

    public int getHistorialAdelanteCount() {
        return pilaAdelante.size();
    }
}

