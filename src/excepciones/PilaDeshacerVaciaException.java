package excepciones;

/**
 * Excepción lanzada cuando se intenta deshacer o rehacer una operación pero la pila correspondiente no tiene elementos.
 */
public class PilaDeshacerVaciaException extends Exception {
    public PilaDeshacerVaciaException(String mensaje) {
        super(mensaje);
    }
}

