package excepciones;

/**
 * Excepción lanzada cuando no se localiza un estudiante en el sistema mediante su identificador.
 */
public class EstudianteNoEncontradoException extends Exception {
    public EstudianteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

