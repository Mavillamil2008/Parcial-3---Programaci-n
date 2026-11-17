package excepciones;

/**
 * Excepción lanzada cuando ocurre un error de lectura, formato o procesamiento en archivos externos (como CSVs de lotes).
 */
public class ArchivoInvalidoException extends Exception {
    public ArchivoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

