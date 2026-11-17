package excepciones;

/**
 * Excepción lanzada cuando una materia ha alcanzado su capacidad máxima de cupos.
 */
public class CupoLlenoException extends Exception {
    public CupoLlenoException(String mensaje) {
        super(mensaje);
    }
}

