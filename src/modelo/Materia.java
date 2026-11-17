package modelo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import excepciones.CupoLlenoException;
import excepciones.PreRequisitoNoAprobadoException;

/**
 * Representa una asignatura académica ofrecida por la universidad.
 * Administra sus pre-requisitos mediante una lista enlazada/List y su flujo de sobrecupo
 * mediante una estructura tipo Cola (Queue).
 */
public class Materia {
    private String codigo;
    private String nombre;
    private int cuposMaximos;
    private int creditos;
    private List<String> prerequisitos; // Lista de códigos de asignaturas requeridas
    private List<Estudiante> inscritos; // Lista de estudiantes inscritos formalmente
    private Queue<Estudiante> colaEspera; // Cola FIFO para cuando los cupos se agotan

    public Materia(String codigo, String nombre, int cuposMaximos, int creditos) {
        this.codigo = codigo != null ? codigo.trim().toUpperCase() : "";
        this.nombre = nombre != null ? nombre.trim() : "";
        this.cuposMaximos = cuposMaximos;
        this.creditos = creditos;
        this.prerequisitos = new ArrayList<>();
        this.inscritos = new ArrayList<>();
        this.colaEspera = new ArrayDeque<>();
    }

    /**
     * Agrega un código de materia como pre-requisito.
     */
    public boolean agregarPrerequisito(String codigoPrerequisito) {
        if (codigoPrerequisito == null || codigoPrerequisito.trim().isEmpty()) {
            return false;
        }
        String codigoLimpio = codigoPrerequisito.trim().toUpperCase();
        if (codigoLimpio.equalsIgnoreCase(this.codigo)) {
            throw new IllegalArgumentException("Una materia no puede ser pre-requisito de sí misma.");
        }
        if (!prerequisitos.contains(codigoLimpio)) {
            prerequisitos.add(codigoLimpio);
            return true;
        }
        return false;
    }

    /**
     * Verifica si el estudiante cumple con la totalidad de los pre-requisitos.
     */
    public boolean cumplePrerequisitos(Estudiante estudiante) {
        for (String req : prerequisitos) {
            if (!estudiante.haAprobado(req)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Intenta inscribir al estudiante en la asignatura.
     * @param estudiante Estudiante que solicita la inscripción.
     * @return ResultadoInscripcion con el estado (INSCRITO o EN_COLA_DE_ESPERA) y detalles.
     * @throws PreRequisitoNoAprobadoException Si no ha aprobado las materias requeridas.
     */
    public ResultadoInscripcion inscribirEstudiante(Estudiante estudiante) throws PreRequisitoNoAprobadoException {
        // 1. Validar que no esté ya inscrito
        if (estaInscrito(estudiante.getId())) {
            return new ResultadoInscripcion(EstadoInscripcion.YA_INSCRITO, "El estudiante ya está inscrito en esta materia.", getCuposRestantes(), null);
        }

        // 2. Validar pre-requisitos
        for (String req : prerequisitos) {
            if (!estudiante.haAprobado(req)) {
                throw new PreRequisitoNoAprobadoException(
                    String.format("El estudiante %s no cumple el pre-requisito: %s", estudiante.getNombre(), req)
                );
            }
        }

        // 3. Evaluar disponibilidad de cupos
        if (inscritos.size() < cuposMaximos) {
            inscritos.add(estudiante);
            if (!estudiante.getMateriasInscritas().contains(this.codigo)) {
                estudiante.getMateriasInscritas().add(this.codigo);
            }
            return new ResultadoInscripcion(
                EstadoInscripcion.EXITOSA,
                "Cupo disponible (cupos restantes: " + getCuposRestantes() + ")",
                getCuposRestantes(),
                null
            );
        } else {
            // Materia llena -> Cola de espera FIFO
            if (!estaEnCola(estudiante.getId())) {
                colaEspera.offer(estudiante);
            }
            return new ResultadoInscripcion(
                EstadoInscripcion.EN_COLA_DE_ESPERA,
                "Materia llena. Agregada a COLA DE ESPERA",
                0,
                null
            );
        }
    }

    /**
     * Cancela la inscripción de un estudiante. Si hay estudiantes en la cola de espera,
     * promueve de inmediato al primero en fila.
     * @param idEstudiante ID del estudiante a dar de baja.
     * @return ResultadoInscripcion indicando si fue liberado y si se promovió a alguien de la cola.
     */
    public ResultadoInscripcion cancelarInscripcion(String idEstudiante) {
        Estudiante retirado = null;
        for (int i = 0; i < inscritos.size(); i++) {
            if (inscritos.get(i).getId().equalsIgnoreCase(idEstudiante)) {
                retirado = inscritos.remove(i);
                retirado.getMateriasInscritas().remove(this.codigo);
                break;
            }
        }

        if (retirado == null) {
            // Verificar si estaba en la cola de espera para retirarlo de allí
            boolean estabaEnCola = colaEspera.removeIf(e -> e.getId().equalsIgnoreCase(idEstudiante));
            if (estabaEnCola) {
                return new ResultadoInscripcion(EstadoInscripcion.RETIRADO_DE_COLA, "Estudiante retirado de la cola de espera.", getCuposRestantes(), null);
            }
            return new ResultadoInscripcion(EstadoInscripcion.NO_ENCONTRADO, "El estudiante no se encontraba inscrito ni en espera.", getCuposRestantes(), null);
        }

        // Si se liberó un cupo y hay estudiantes en la cola de espera:
        Estudiante promovido = null;
        if (!colaEspera.isEmpty()) {
            promovido = colaEspera.poll();
            inscritos.add(promovido);
            if (!promovido.getMateriasInscritas().contains(this.codigo)) {
                promovido.getMateriasInscritas().add(this.codigo);
            }
        }

        return new ResultadoInscripcion(
            EstadoInscripcion.CANCELACION_EXITOSA,
            "Cancelacion exitosa. Cupo liberado.",
            getCuposRestantes(),
            promovido
        );
    }

    public boolean estaInscrito(String idEstudiante) {
        return inscritos.stream().anyMatch(e -> e.getId().equalsIgnoreCase(idEstudiante));
    }

    public boolean estaEnCola(String idEstudiante) {
        return colaEspera.stream().anyMatch(e -> e.getId().equalsIgnoreCase(idEstudiante));
    }

    public int getCuposRestantes() {
        return Math.max(0, cuposMaximos - inscritos.size());
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCuposMaximos() {
        return cuposMaximos;
    }

    public void setCuposMaximos(int cuposMaximos) {
        this.cuposMaximos = cuposMaximos;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public List<String> getPrerequisitos() {
        return prerequisitos;
    }

    public List<Estudiante> getInscritos() {
        return inscritos;
    }

    public Queue<Estudiante> getColaEspera() {
        return colaEspera;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (Créditos: %d | Cupos: %d/%d)", codigo, nombre, creditos, inscritos.size(), cuposMaximos);
    }

    // Enumeración y clase interna de soporte de resultados
    public enum EstadoInscripcion {
        EXITOSA,
        EN_COLA_DE_ESPERA,
        YA_INSCRITO,
        CANCELACION_EXITOSA,
        RETIRADO_DE_COLA,
        NO_ENCONTRADO
    }

    public static class ResultadoInscripcion {
        private final EstadoInscripcion estado;
        private final String mensaje;
        private final int cuposRestantes;
        private final Estudiante estudiantePromovido;

        public ResultadoInscripcion(EstadoInscripcion estado, String mensaje, int cuposRestantes, Estudiante estudiantePromovido) {
            this.estado = estado;
            this.mensaje = mensaje;
            this.cuposRestantes = cuposRestantes;
            this.estudiantePromovido = estudiantePromovido;
        }

        public EstadoInscripcion getEstado() {
            return estado;
        }

        public String getMensaje() {
            return mensaje;
        }

        public int getCuposRestantes() {
            return cuposRestantes;
        }

        public Estudiante getEstudiantePromovido() {
            return estudiantePromovido;
        }
    }
}

