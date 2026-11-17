package excepciones;

/**
 * Excepción lanzada cuando se intenta obtener o atender un estudiante de una cola de espera que está vacía.
 */
public class ColaDeEsperaVaciaException extends Exception {
    public ColaDeEsperaVaciaException(String mensaje) {
        super(mensaje);
    }
}

