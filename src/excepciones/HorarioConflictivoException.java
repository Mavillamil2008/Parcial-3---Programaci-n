package excepciones;

/**
 * Excepción lanzada cuando se intenta reservar un aula en un horario ya ocupado
 * o que genera conflicto con otra reserva.
 */
public class HorarioConflictivoException extends Exception {
    public HorarioConflictivoException(String mensaje) {
        super(mensaje);
    }
}

