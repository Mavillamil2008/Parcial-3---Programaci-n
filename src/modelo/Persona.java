package modelo;

/**
 * Clase abstracta base que representa una persona en el entorno universitario.
 * Define los atributos comunes de identidad y obliga a las subclases a implementar
 * el polimorfismo mediante el método mostrarInformacion().
 */
public abstract class Persona {
    private String id;
    private String nombre;
    private String email;

    public Persona(String id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    /**
     * Método polimórfico que debe ser sobrescrito por las subclases
     * para presentar la información correspondiente al rol de la persona.
     * @return Cadena formateada con la información de la persona.
     */
    public abstract String mostrarInformacion();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Nombre: %s | Email: %s", id, nombre, email);
    }
}

