package estructuras;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import modelo.Aula;
import modelo.Estudiante;
import modelo.Materia;

/**
 * Contrato e implementaciones para el sistema de Deshacer (Undo) y Rehacer (Redo)
 * utilizando el patrón Command / Memento con pilas (Stack).
 */
public interface Operacion {
    String deshacer() throws Exception;
    String rehacer() throws Exception;
    String getDescripcion();

    // 1. Inscripción de estudiante (en cupo directo o en cola de espera)
    class Inscripcion implements Operacion {
        private final Materia materia;
        private final Estudiante estudiante;
        private final boolean estabaEnEspera;

        public Inscripcion(Materia materia, Estudiante estudiante, boolean estabaEnEspera) {
            this.materia = materia;
            this.estudiante = estudiante;
            this.estabaEnEspera = estabaEnEspera;
        }

        @Override
        public String deshacer() {
            if (estabaEnEspera) {
                materia.getColaEspera().removeIf(e -> e.getId().equalsIgnoreCase(estudiante.getId()));
            } else {
                materia.getInscritos().removeIf(e -> e.getId().equalsIgnoreCase(estudiante.getId()));
                estudiante.getMateriasInscritas().remove(materia.getCodigo());
            }
            return String.format("%s ya NO esta inscrita en %s", estudiante.getNombre(), materia.getCodigo());
        }

        @Override
        public String rehacer() {
            if (estabaEnEspera) {
                if (!materia.estaEnCola(estudiante.getId())) {
                    materia.getColaEspera().offer(estudiante);
                }
            } else {
                if (!materia.estaInscrito(estudiante.getId())) {
                    materia.getInscritos().add(estudiante);
                    if (!estudiante.getMateriasInscritas().contains(materia.getCodigo())) {
                        estudiante.getMateriasInscritas().add(materia.getCodigo());
                    }
                }
            }
            return String.format("%s inscrita en %s", estudiante.getNombre(), materia.getCodigo());
        }

        @Override
        public String getDescripcion() {
            return String.format("Inscribir %s en %s", estudiante.getNombre(), materia.getCodigo());
        }
    }

    // 2. Cancelación de inscripción con posible promoción de lista de espera
    class Cancelacion implements Operacion {
        private final Materia materia;
        private final Estudiante estudianteCancelado;
        private final Estudiante estudiantePromovido;

        public Cancelacion(Materia materia, Estudiante estudianteCancelado, Estudiante estudiantePromovido) {
            this.materia = materia;
            this.estudianteCancelado = estudianteCancelado;
            this.estudiantePromovido = estudiantePromovido;
        }

        @Override
        public String deshacer() {
            if (estudiantePromovido != null) {
                materia.getInscritos().removeIf(e -> e.getId().equalsIgnoreCase(estudiantePromovido.getId()));
                estudiantePromovido.getMateriasInscritas().remove(materia.getCodigo());

                Queue<Estudiante> nuevaCola = new ArrayDeque<>();
                nuevaCola.offer(estudiantePromovido);
                nuevaCola.addAll(materia.getColaEspera());
                materia.getColaEspera().clear();
                materia.getColaEspera().addAll(nuevaCola);
            }

            if (!materia.estaInscrito(estudianteCancelado.getId())) {
                materia.getInscritos().add(estudianteCancelado);
                if (!estudianteCancelado.getMateriasInscritas().contains(materia.getCodigo())) {
                    estudianteCancelado.getMateriasInscritas().add(materia.getCodigo());
                }
            }
            return String.format("Cancelación deshecha: %s reincorporado en %s", estudianteCancelado.getNombre(), materia.getCodigo());
        }

        @Override
        public String rehacer() {
            materia.cancelarInscripcion(estudianteCancelado.getId());
            return String.format("Cancelación rehecha: %s retirado de %s", estudianteCancelado.getNombre(), materia.getCodigo());
        }

        @Override
        public String getDescripcion() {
            return String.format("Cancelar inscripción de %s en %s", estudianteCancelado.getNombre(), materia.getCodigo());
        }
    }

    // 3. Reserva o liberación de horario en aula
    class Horario implements Operacion {
        private final Aula aula;
        private final int dia;
        private final int hora;
        private final int duracion;
        private final boolean eraReserva;

        public Horario(Aula aula, int dia, int hora, int duracion, boolean eraReserva) {
            this.aula = aula;
            this.dia = dia;
            this.hora = hora;
            this.duracion = duracion;
            this.eraReserva = eraReserva;
        }

        @Override
        public String deshacer() throws Exception {
            if (eraReserva) {
                aula.liberar(dia, hora, duracion);
                return String.format("Reserva deshecha en %s (%s de %d:00 a %d:00 liberado)",
                    aula.getCodigo(), Aula.DIAS_SEMANA[dia], hora, hora + duracion);
            } else {
                aula.reservar(dia, hora, duracion);
                return String.format("Liberación deshecha en %s (%s de %d:00 a %d:00 vuelto a reservar)",
                    aula.getCodigo(), Aula.DIAS_SEMANA[dia], hora, hora + duracion);
            }
        }

        @Override
        public String rehacer() throws Exception {
            if (eraReserva) {
                aula.reservar(dia, hora, duracion);
                return String.format("Reserva rehecha en %s (%s de %d:00 a %d:00)",
                    aula.getCodigo(), Aula.DIAS_SEMANA[dia], hora, hora + duracion);
            } else {
                aula.liberar(dia, hora, duracion);
                return String.format("Liberación rehecha en %s (%s de %d:00 a %d:00)",
                    aula.getCodigo(), Aula.DIAS_SEMANA[dia], hora, hora + duracion);
            }
        }

        @Override
        public String getDescripcion() {
            return String.format("Horario en %s: %s %d:00 (%d horas)", aula.getCodigo(), Aula.DIAS_SEMANA[dia], hora, duracion);
        }
    }

    // 4. Registro de calificación en matriz Double[10][20]
    class Nota implements Operacion {
        private final Estudiante estudiante;
        private final int semestre;
        private final String codigoMateria;
        private final double nota;

        public Nota(Estudiante estudiante, int semestre, String codigoMateria, double nota) {
            this.estudiante = estudiante;
            this.semestre = semestre;
            this.codigoMateria = codigoMateria;
            this.nota = nota;
        }

        @Override
        public String deshacer() {
            estudiante.eliminarUltimaNota(semestre, codigoMateria);
            return String.format("Nota de %.1f en %s deshecha para %s", nota, codigoMateria, estudiante.getNombre());
        }

        @Override
        public String rehacer() {
            estudiante.registrarNota(semestre, codigoMateria, nota);
            return String.format("Nota de %.1f en %s re-registrada para %s", nota, codigoMateria, estudiante.getNombre());
        }

        @Override
        public String getDescripcion() {
            return String.format("Registrar nota %.1f a %s en %s (Semestre %d)", nota, estudiante.getNombre(), codigoMateria, semestre);
        }
    }

    // 5. Baja o eliminación definitiva de estudiante
    class EliminarEstudiante implements Operacion {
        private final Map<String, Estudiante> mapaEstudiantes;
        private final Estudiante estudianteEliminado;

        public EliminarEstudiante(Map<String, Estudiante> mapaEstudiantes, Estudiante estudianteEliminado) {
            this.mapaEstudiantes = mapaEstudiantes;
            this.estudianteEliminado = estudianteEliminado;
        }

        @Override
        public String deshacer() {
            mapaEstudiantes.put(estudianteEliminado.getId(), estudianteEliminado);
            return String.format("Eliminación deshecha: Estudiante %s (ID: %s) restaurado al sistema",
                estudianteEliminado.getNombre(), estudianteEliminado.getId());
        }

        @Override
        public String rehacer() {
            mapaEstudiantes.remove(estudianteEliminado.getId());
            return String.format("Eliminación rehecha: Estudiante %s (ID: %s) eliminado definitivamente",
                estudianteEliminado.getNombre(), estudianteEliminado.getId());
        }

        @Override
        public String getDescripcion() {
            return String.format("Eliminar estudiante %s (ID: %s)", estudianteEliminado.getNombre(), estudianteEliminado.getId());
        }
    }
}
