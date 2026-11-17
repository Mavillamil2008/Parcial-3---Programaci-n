package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un estudiante de la universidad. Hereda de Persona.
 * Gestiona su rendimiento académico mediante un arreglo estático bidimensional Double[10][20],
 * donde las filas corresponden a los 10 semestres y las columnas a hasta 20 materias por semestre.
 */
public class Estudiante extends Persona {
    private int semestre;
    private Double[][] notas; // Arreglo estático nativo Double[10][20]
    private String[][] materiasNombres; // Matriz paralela para identificar la materia de cada nota
    private int[] conteoMateriasSemestre; // Conteo de notas cargadas en cada semestre
    private List<String> historialMaterias; // Lista de materias cursadas y aprobadas (para pre-requisitos)
    private List<String> materiasInscritas; // Materias actualmente inscritas en el ciclo en curso

    public static final int MAX_SEMESTRES = 10;
    public static final int MAX_MATERIAS_POR_SEMESTRE = 20;

    public Estudiante(String id, String nombre, String email, int semestre) {
        super(id, nombre, email);
        this.semestre = semestre;
        this.notas = new Double[MAX_SEMESTRES][MAX_MATERIAS_POR_SEMESTRE];
        this.materiasNombres = new String[MAX_SEMESTRES][MAX_MATERIAS_POR_SEMESTRE];
        this.conteoMateriasSemestre = new int[MAX_SEMESTRES];
        this.historialMaterias = new ArrayList<>();
        this.materiasInscritas = new ArrayList<>();
    }

    /**
     * Registra una calificación para una materia en un semestre determinado.
     * Si la nota es mayor o igual a 3.0, la materia se adiciona al historial de aprobadas.
     * @param numSemestre Número de semestre (1 a 10).
     * @param codigoMateria Código identificador de la asignatura.
     * @param nota Calificación en escala de 0.0 a 5.0.
     * @return true si se registró exitosamente, false si no hay espacio en el semestre.
     */
    public boolean registrarNota(int numSemestre, String codigoMateria, double nota) {
        if (numSemestre < 1 || numSemestre > MAX_SEMESTRES) {
            throw new IllegalArgumentException("El semestre debe estar entre 1 y " + MAX_SEMESTRES);
        }
        int fila = numSemestre - 1;
        int columna = conteoMateriasSemestre[fila];

        if (columna >= MAX_MATERIAS_POR_SEMESTRE) {
            return false;
        }

        notas[fila][columna] = nota;
        materiasNombres[fila][columna] = codigoMateria;
        conteoMateriasSemestre[fila]++;

        if (nota >= 3.0 && !historialMaterias.contains(codigoMateria)) {
            historialMaterias.add(codigoMateria);
        }
        return true;
    }

    /**
     * Elimina el último registro de nota de un semestre (útil para el patrón Deshacer).
     */
    public boolean eliminarUltimaNota(int numSemestre, String codigoMateria) {
        if (numSemestre < 1 || numSemestre > MAX_SEMESTRES) return false;
        int fila = numSemestre - 1;
        int total = conteoMateriasSemestre[fila];
        for (int c = total - 1; c >= 0; c--) {
            if (codigoMateria.equalsIgnoreCase(materiasNombres[fila][c])) {
                Double notaEliminada = notas[fila][c];
                notas[fila][c] = null;
                materiasNombres[fila][c] = null;
                // Compactar o reubicar posiciones
                for (int k = c; k < total - 1; k++) {
                    notas[fila][k] = notas[fila][k + 1];
                    materiasNombres[fila][k] = materiasNombres[fila][k + 1];
                }
                notas[fila][total - 1] = null;
                materiasNombres[fila][total - 1] = null;
                conteoMateriasSemestre[fila]--;

                // Verificar si aún tiene otra nota aprobada en esa materia
                if (notaEliminada != null && notaEliminada >= 3.0) {
                    boolean aunAprobada = false;
                    for (int s = 0; s < MAX_SEMESTRES; s++) {
                        for (int m = 0; m < conteoMateriasSemestre[s]; m++) {
                            if (codigoMateria.equalsIgnoreCase(materiasNombres[s][m]) && notas[s][m] != null && notas[s][m] >= 3.0) {
                                aunAprobada = true;
                                break;
                            }
                        }
                    }
                    if (!aunAprobada) {
                        historialMaterias.remove(codigoMateria);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula el promedio numérico de un semestre dado.
     * @param numSemestre Semestre a consultar (1 a 10).
     * @return Promedio de las materias en dicho semestre, o 0.0 si no tiene calificaciones.
     */
    public double calcularPromedioSemestre(int numSemestre) {
        if (numSemestre < 1 || numSemestre > MAX_SEMESTRES) return 0.0;
        int fila = numSemestre - 1;
        int total = conteoMateriasSemestre[fila];
        if (total == 0) return 0.0;

        double suma = 0.0;
        for (int col = 0; col < total; col++) {
            if (notas[fila][col] != null) {
                suma += notas[fila][col];
            }
        }
        return suma / total;
    }

    /**
     * Calcula el promedio ponderado/acumulado general del estudiante a lo largo de todos los semestres.
     * @return Promedio acumulado, o 0.0 si no registra notas.
     */
    public double calcularPromedioAcumulado() {
        double sumaTotal = 0.0;
        int totalNotas = 0;

        for (int fila = 0; fila < MAX_SEMESTRES; fila++) {
            for (int col = 0; col < conteoMateriasSemestre[fila]; col++) {
                if (notas[fila][col] != null) {
                    sumaTotal += notas[fila][col];
                    totalNotas++;
                }
            }
        }
        return totalNotas == 0 ? 0.0 : (sumaTotal / totalNotas);
    }

    /**
     * Cuenta cuántas materias han sido aprobadas (nota >= 3.0).
     */
    public int contarMateriasAprobadas() {
        int count = 0;
        for (int f = 0; f < MAX_SEMESTRES; f++) {
            for (int c = 0; c < conteoMateriasSemestre[f]; c++) {
                if (notas[f][c] != null && notas[f][c] >= 3.0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Cuenta cuántas materias han sido reprobadas (nota < 3.0).
     */
    public int contarMateriasReprobadas() {
        int count = 0;
        for (int f = 0; f < MAX_SEMESTRES; f++) {
            for (int c = 0; c < conteoMateriasSemestre[f]; c++) {
                if (notas[f][c] != null && notas[f][c] < 3.0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Determina si el estudiante ha aprobado una materia por su código.
     */
    public boolean haAprobado(String codigoMateria) {
        return historialMaterias.stream().anyMatch(m -> m.equalsIgnoreCase(codigoMateria));
    }

    /**
     * Genera un reporte formateado del historial académico completo del estudiante.
     */
    public String generarReporteAcademico() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- REPORTE ACADEMICO ---\n");
        sb.append(String.format("Estudiante: %s (ID: %s)\n\n", getNombre(), getId()));

        boolean tieneNotas = false;
        for (int s = 0; s < MAX_SEMESTRES; s++) {
            if (conteoMateriasSemestre[s] > 0) {
                tieneNotas = true;
                sb.append(String.format("Semestre %d:\n", s + 1));
                for (int m = 0; m < conteoMateriasSemestre[s]; m++) {
                    sb.append(String.format("  %s: %.1f\n", materiasNombres[s][m], notas[s][m]));
                }
                sb.append(String.format("  Promedio: %.2f\n\n", calcularPromedioSemestre(s + 1)));
            }
        }

        if (!tieneNotas) {
            sb.append("El estudiante no registra calificaciones todavía.\n\n");
        }

        sb.append("=== RESUMEN ===\n");
        sb.append(String.format("Promedio acumulado: %.2f\n", calcularPromedioAcumulado()));
        sb.append(String.format("Materias aprobadas: %d\n", contarMateriasAprobadas()));
        sb.append(String.format("Materias reprobadas: %d\n", contarMateriasReprobadas()));
        return sb.toString();
    }

    /**
     * Genera la lista de materias reprobadas (< 3.0) con sus detalles.
     */
    public List<String> obtenerMateriasReprobadasDetalle() {
        List<String> lista = new ArrayList<>();
        for (int f = 0; f < MAX_SEMESTRES; f++) {
            for (int c = 0; c < conteoMateriasSemestre[f]; c++) {
                if (notas[f][c] != null && notas[f][c] < 3.0) {
                    lista.add(String.format("Semestre %d | %s | Nota: %.1f", f + 1, materiasNombres[f][c], notas[f][c]));
                }
            }
        }
        return lista;
    }

    @Override
    public String mostrarInformacion() {
        double prom = calcularPromedioAcumulado();
        String promStr = (prom == 0.0 && contarMateriasAprobadas() == 0 && contarMateriasReprobadas() == 0)
            ? "0.0 (sin notas aun)"
            : String.format("%.2f", prom);

        return String.format(
            "ID: %s\n" +
            "Nombre: %s\n" +
            "Email: %s\n" +
            "Semestre: %d\n" +
            "Promedio acumulado: %s",
            getId(), getNombre(), getEmail(), semestre, promStr
        );
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public Double[][] getNotas() {
        return notas;
    }

    public String[][] getMateriasNombres() {
        return materiasNombres;
    }

    public int[] getConteoMateriasSemestre() {
        return conteoMateriasSemestre;
    }

    public List<String> getHistorialMaterias() {
        return historialMaterias;
    }

    public List<String> getMateriasInscritas() {
        return materiasInscritas;
    }
}

