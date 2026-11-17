package excepciones;

/**
 * Excepción lanzada cuando un estudiante no cumple con los pre-requisitos
 * aprobados para inscribir una materia.
 */
public class PreRequisitoNoAprobadoException extends Exception {
    public PreRequisitoNoAprobadoException(String mensaje) {
        super(mensaje);
    }
}

