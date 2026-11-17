package modelo;

import excepciones.HorarioConflictivoException;

/**
 * Representa un espacio físico (aula) en la universidad.
 * Gestiona su disponibilidad horaria mediante una matriz nativa boolean[7][24].
 * 
 * Días: 0 = Domingo, 1 = Lunes, 2 = Martes, 3 = Miércoles, 4 = Jueves, 5 = Viernes, 6 = Sábado.
 * Horas: 0 a 23.
 * Valor: false = LIBRE, true = RESERVADO (ocupado).
 */
public class Aula implements Comparable<Aula> {
    private String codigo;
    private int capacidad;
    private boolean[][] horario; // boolean[7][24]

    public static final String[] DIAS_SEMANA = {
        "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"
    };

    public Aula(String codigo, int capacidad) {
        this.codigo = codigo;
        this.capacidad = capacidad;
        this.horario = new boolean[7][24]; // false por defecto (libre)
    }

    /**
     * Consulta si una hora específica está disponible.
     * @param dia Día de la semana (0 a 6).
     * @param hora Hora del día (0 a 23).
     * @return true si el horario está LIBRE (disponible), false si está ocupado.
     */
    public boolean consultarDisponibilidad(int dia, int hora) {
        validarRango(dia, hora);
        return !horario[dia][hora];
    }

    /**
     * Reserva un bloque de horas consecutivas para un día dado.
     * @param dia Día de la semana (0 a 6).
     * @param hora Hora de inicio (0 a 23).
     * @param duracion Cantidad de horas continuas a reservar.
     * @throws HorarioConflictivoException Si alguna de las horas ya se encuentra reservada.
     */
    public void reservar(int dia, int hora, int duracion) throws HorarioConflictivoException {
        validarRango(dia, hora);
        if (duracion <= 0 || hora + duracion > 24) {
            throw new IllegalArgumentException("La duración excede el límite del día (hora + duración > 24).");
        }

        // 1. Verificación previa de todos los slots
        for (int h = hora; h < hora + duracion; h++) {
            if (horario[dia][h]) {
                throw new HorarioConflictivoException(
                    String.format("%s %d:00 ya esta reservado", DIAS_SEMANA[dia], h)
                );
            }
        }

        // 2. Asignación de reserva
        for (int h = hora; h < hora + duracion; h++) {
            horario[dia][h] = true;
        }
    }

    /**
     * Libera un bloque de horas que estaba previamente reservado.
     * @param dia Día de la semana (0 a 6).
     * @param hora Hora de inicio (0 a 23).
     * @param duracion Cantidad de horas continuas a liberar.
     */
    public void liberar(int dia, int hora, int duracion) {
        validarRango(dia, hora);
        int fin = Math.min(24, hora + duracion);
        for (int h = hora; h < fin; h++) {
            horario[dia][h] = false;
        }
    }

    private void validarRango(int dia, int hora) {
        if (dia < 0 || dia > 6) {
            throw new IllegalArgumentException("Día inválido: debe estar entre 0 (Domingo) y 6 (Sábado).");
        }
        if (hora < 0 || hora > 23) {
            throw new IllegalArgumentException("Hora inválida: debe estar entre 0 y 23.");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public boolean[][] getHorario() {
        return horario;
    }

    @Override
    public int compareTo(Aula otra) {
        // Para TreeMap ordenado por código o capacidad
        return this.codigo.compareToIgnoreCase(otra.codigo);
    }

    @Override
    public String toString() {
        return String.format("Aula %s (Capacidad: %d personas)", codigo, capacidad);
    }
}

