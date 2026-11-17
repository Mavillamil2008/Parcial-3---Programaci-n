package modelo;

/**
 * Representa a un docente o profesor universitario. Hereda de la clase abstracta Persona.
 * Demuestra la jerarquía de herencia solicitada en la rúbrica del proyecto.
 */
public class Profesor extends Persona {
    private String departamento;
    private double salario;

    public Profesor(String id, String nombre, String email, String departamento, double salario) {
        super(id, nombre, email);
        this.departamento = departamento;
        this.salario = salario;
    }

    @Override
    public String mostrarInformacion() {
        return String.format(
            "=== INFORMACIÓN DOCENTE ===\n" +
            "ID: %s\n" +
            "Nombre: %s\n" +
            "Email: %s\n" +
            "Departamento: %s\n" +
            "Salario: $%.2f",
            getId(), getNombre(), getEmail(), departamento, salario
        );
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }
}

