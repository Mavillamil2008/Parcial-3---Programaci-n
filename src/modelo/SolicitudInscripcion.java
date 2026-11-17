package modelo;

/**
 * Representa una petición de inscripción proveniente de un proceso batch o archivo CSV.
 */
public class SolicitudInscripcion {
    private String idEstudiante;
    private String codigoMateria;

    public SolicitudInscripcion(String idEstudiante, String codigoMateria) {
        this.idEstudiante = idEstudiante != null ? idEstudiante.trim() : "";
        this.codigoMateria = codigoMateria != null ? codigoMateria.trim() : "";
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getCodigoMateria() {
        return codigoMateria;
    }

    public void setCodigoMateria(String codigoMateria) {
        this.codigoMateria = codigoMateria;
    }

    @Override
    public String toString() {
        return idEstudiante + " -> " + codigoMateria;
    }
}

