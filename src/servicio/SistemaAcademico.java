package servicio;

import java.util.*;
import excepciones.*;
import estructuras.GrafoCampus;
import estructuras.HistorialOperaciones;
import estructuras.NavegadorReportes;
import estructuras.Operacion;
import modelo.*;
import util.LectorCSV;

/**
 * Servicio central (Fachada / Controlador de Negocio) que articula las estructuras de datos,
 * entidades del modelo, validaciones y reglas de negocio del sistema académico universitario.
 */
public class SistemaAcademico {

    // 1. Mapas requeridos
    private final Map<String, Estudiante> estudiantes; // HashMap para búsqueda por ID en O(1)
    private final Map<String, Aula> aulas; // TreeMap para aulas ordenadas por código en O(log N)
    private final Map<String, Materia> materias; // Materias registradas en el catálogo

    // 2. Arreglo estático nativo Facultad[5]
    private final Facultad[] facultades;

    // 3. Grafo campus con matriz nativa int[N][N] y algoritmo de Dijkstra
    private final GrafoCampus grafoCampus;

    // 4. Pilas para Deshacer/Rehacer y Navegación de Reportes
    private final HistorialOperaciones historialOperaciones;
    private final NavegadorReportes navegadorReportes;

    // 5. Cola para procesamiento masivo por lotes (Queue FIFO estándar)
    private final Queue<SolicitudInscripcion> colaBatch;

    public SistemaAcademico() {
        this.estudiantes = new HashMap<>();
        this.aulas = new TreeMap<>();
        this.materias = new LinkedHashMap<>();
        this.facultades = new Facultad[5];
        this.historialOperaciones = new HistorialOperaciones();
        this.navegadorReportes = new NavegadorReportes();
        this.colaBatch = new LinkedList<>();

        // Inicializar campus con al menos 5 edificios
        String[] nombresEdificios = {
            "Ingenieria", "Biblioteca", "Cafeteria", "Rectoria", "Laboratorios"
        };
        this.grafoCampus = new GrafoCampus(nombresEdificios);

        // Inicializar datos semilla para facilitar pruebas y calificación inmediata
        inicializarDatosSemilla();
    }

    /**
     * Carga inicial de facultades, aulas, materias, estudiantes y rutas según los ejemplos de la guía.
     */
    private void inicializarDatosSemilla() {
        // A. Carga de Facultad[5]
        facultades[0] = new Facultad("FAC-ING", "Facultad de Ingeniería", "Ing. Roberto Salazar MSc.");
        facultades[1] = new Facultad("FAC-SALUD", "Facultad de Ciencias de la Salud", "Dra. Carolina Vargas MD.");
        facultades[2] = new Facultad("FAC-CEA", "Facultad de Ciencias Económicas", "Mg. Fernando Castro");
        facultades[3] = new Facultad("FAC-EDU", "Facultad de Educación", "Dra. Patricia Reyes PhD.");
        facultades[4] = new Facultad("FAC-HUM", "Facultad de Humanidades y Artes", "Dr. Mario Mendoza");

        // B. Carga de Aulas en TreeMap
        aulas.put("101", new Aula("101", 35));
        aulas.put("102", new Aula("102", 40));
        aulas.put("201", new Aula("201", 30));
        aulas.put("202", new Aula("202", 25));
        aulas.put("301", new Aula("301", 50));

        // C. Carga de Materias
        Materia calc101 = new Materia("CALC101", "Calculo I", 3, 4);
        Materia calc102 = new Materia("CALC102", "Calculo II", 3, 4);
        calc102.agregarPrerequisito("CALC101");

        Materia fis101 = new Materia("FIS101", "Fisica I", 3, 3);
        Materia fis102 = new Materia("FIS102", "Fisica II", 3, 3);
        fis102.agregarPrerequisito("FIS101");

        Materia prog101 = new Materia("PROG101", "Programacion I", 3, 3);
        Materia prog102 = new Materia("PROG102", "Programacion II", 3, 3);
        prog102.agregarPrerequisito("PROG101");

        Materia matdis = new Materia("MATDIS", "Matematicas Discretas", 3, 3);

        materias.put(calc101.getCodigo(), calc101);
        materias.put(calc102.getCodigo(), calc102);
        materias.put(fis101.getCodigo(), fis101);
        materias.put(fis102.getCodigo(), fis102);
        materias.put(prog101.getCodigo(), prog101);
        materias.put(prog102.getCodigo(), prog102);
        materias.put(matdis.getCodigo(), matdis);

        // D. Carga de Estudiante Ejemplo (Ana María Gómez - ID 2024001 del PDF)
        Estudiante ana = new Estudiante("2024001", "Ana Maria Gomez", "ana.gomez@universidad.edu", 3);
        // Semestre 1 de Ana
        ana.registrarNota(1, "CALC101", 4.5);
        ana.registrarNota(1, "FIS101", 2.8);
        ana.registrarNota(1, "PROG101", 5.0);
        // Semestre 2 de Ana
        ana.registrarNota(2, "CALC102", 3.5);
        ana.registrarNota(2, "FIS102", 3.0);
        ana.registrarNota(2, "MATDIS", 2.5);
        estudiantes.put(ana.getId(), ana);

        // Estudiantes adicionales para pruebas de cola de espera
        estudiantes.put("2024002", new Estudiante("2024002", "Juan Perez", "juan.perez@universidad.edu", 1));
        estudiantes.put("2024003", new Estudiante("2024003", "Maria Lopez", "maria.lopez@universidad.edu", 1));
        estudiantes.put("2024004", new Estudiante("2024004", "Carlos Ruiz", "carlos.ruiz@universidad.edu", 1));
        estudiantes.put("2024005", new Estudiante("2024005", "Laura Diaz", "laura.diaz@universidad.edu", 1));
        estudiantes.put("2024006", new Estudiante("2024006", "Pablo Ortiz", "pablo.ortiz@universidad.edu", 1));

        // E. Conexiones del grafo campus (0: Ingenieria, 1: Biblioteca, 2: Cafeteria, 3: Rectoria, 4: Laboratorios)
        grafoCampus.agregarConexion(0, 2, 150); // Ingenieria <-> Cafeteria (150m)
        grafoCampus.agregarConexion(2, 3, 180); // Cafeteria <-> Rectoria (180m) -> Total 330m (Ejemplo 5)
        grafoCampus.agregarConexion(0, 1, 200); // Ingenieria <-> Biblioteca (200m)
        grafoCampus.agregarConexion(1, 3, 250); // Biblioteca <-> Rectoria (250m)
        grafoCampus.agregarConexion(2, 4, 120); // Cafeteria <-> Laboratorios (120m)
        grafoCampus.agregarConexion(4, 3, 210); // Laboratorios <-> Rectoria (210m)
    }

    // =========================================================================
    // 1. GESTIÓN DE ESTUDIANTES
    // =========================================================================

    public void registrarEstudiante(String id, String nombre, String email, int semestre) {
        if (estudiantes.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el ID: " + id);
        }
        Estudiante nuevo = new Estudiante(id, nombre, email, semestre);
        estudiantes.put(id, nuevo);
    }

    public Estudiante buscarEstudiante(String id) throws EstudianteNoEncontradoException {
        Estudiante est = estudiantes.get(id);
        if (est == null) {
            throw new EstudianteNoEncontradoException("No existe estudiante con ID: " + id);
        }
        return est;
    }

    public List<Estudiante> listarEstudiantes() {
        List<Estudiante> lista = new ArrayList<>(estudiantes.values());
        lista.sort(Comparator.comparing(Estudiante::getId));
        return lista;
    }

    public Estudiante eliminarEstudiante(String id) throws EstudianteNoEncontradoException {
        Estudiante est = buscarEstudiante(id);
        estudiantes.remove(id);
        historialOperaciones.registrarOperacion(new Operacion.EliminarEstudiante(estudiantes, est));
        return est;
    }

    // =========================================================================
    // 2. GESTIÓN DE MATERIAS
    // =========================================================================

    public void crearMateria(String codigo, String nombre, int cuposMaximos, int creditos) {
        String cod = codigo.trim().toUpperCase();
        if (materias.containsKey(cod)) {
            throw new IllegalArgumentException("Ya existe una materia con el código: " + cod);
        }
        Materia m = new Materia(cod, nombre, cuposMaximos, creditos);
        materias.put(cod, m);
    }

    public Materia buscarMateria(String codigo) {
        if (codigo == null) return null;
        return materias.get(codigo.trim().toUpperCase());
    }

    public boolean agregarPrerequisito(String codigoMateria, String codigoPrerequisito) {
        Materia m = buscarMateria(codigoMateria);
        if (m == null) {
            throw new IllegalArgumentException("No existe la materia con código: " + codigoMateria);
        }
        Materia pre = buscarMateria(codigoPrerequisito);
        if (pre == null) {
            throw new IllegalArgumentException("No existe la materia pre-requisito con código: " + codigoPrerequisito);
        }
        return m.agregarPrerequisito(codigoPrerequisito);
    }

    public List<String> obtenerPrerequisitos(String codigoMateria) {
        Materia m = buscarMateria(codigoMateria);
        if (m == null) {
            throw new IllegalArgumentException("No existe la materia con código: " + codigoMateria);
        }
        return m.getPrerequisitos();
    }

    public Materia.ResultadoInscripcion inscribirEstudianteEnMateria(String idEstudiante, String codigoMateria)
            throws EstudianteNoEncontradoException, PreRequisitoNoAprobadoException {
        Estudiante est = buscarEstudiante(idEstudiante);
        Materia mat = buscarMateria(codigoMateria);
        if (mat == null) {
            throw new IllegalArgumentException("No existe la materia con código: " + codigoMateria);
        }

        Materia.ResultadoInscripcion res = mat.inscribirEstudiante(est);
        if (res.getEstado() == Materia.EstadoInscripcion.EXITOSA) {
            historialOperaciones.registrarOperacion(new Operacion.Inscripcion(mat, est, false));
        } else if (res.getEstado() == Materia.EstadoInscripcion.EN_COLA_DE_ESPERA) {
            historialOperaciones.registrarOperacion(new Operacion.Inscripcion(mat, est, true));
        }
        return res;
    }

    public Materia.ResultadoInscripcion cancelarInscripcionMateria(String idEstudiante, String codigoMateria)
            throws EstudianteNoEncontradoException {
        Estudiante est = buscarEstudiante(idEstudiante);
        Materia mat = buscarMateria(codigoMateria);
        if (mat == null) {
            throw new IllegalArgumentException("No existe la materia con código: " + codigoMateria);
        }

        Materia.ResultadoInscripcion res = mat.cancelarInscripcion(idEstudiante);
        if (res.getEstado() == Materia.EstadoInscripcion.CANCELACION_EXITOSA) {
            historialOperaciones.registrarOperacion(new Operacion.Cancelacion(mat, est, res.getEstudiantePromovido()));
        }
        return res;
    }

    public Queue<Estudiante> obtenerColaEspera(String codigoMateria) throws ColaDeEsperaVaciaException {
        Materia mat = buscarMateria(codigoMateria);
        if (mat == null) {
            throw new IllegalArgumentException("No existe la materia con código: " + codigoMateria);
        }
        Queue<Estudiante> cola = mat.getColaEspera();
        if (cola.isEmpty()) {
            throw new ColaDeEsperaVaciaException("La cola de espera de la materia " + codigoMateria + " se encuentra vacía.");
        }
        return cola;
    }

    public Collection<Materia> listarMaterias() {
        return materias.values();
    }

    // =========================================================================
    // 3. GESTIÓN DE HORARIOS CON MATRIZ
    // =========================================================================

    public Aula buscarAula(String codigoAula) {
        return aulas.get(codigoAula);
    }

    public void reservarHorarioAula(String codigoAula, int dia, int hora, int duracion)
            throws HorarioConflictivoException {
        Aula aula = buscarAula(codigoAula);
        if (aula == null) {
            throw new IllegalArgumentException("No existe el aula con código: " + codigoAula);
        }
        aula.reservar(dia, hora, duracion);
        historialOperaciones.registrarOperacion(new Operacion.Horario(aula, dia, hora, duracion, true));
    }

    public void liberarHorarioAula(String codigoAula, int dia, int hora, int duracion) {
        Aula aula = buscarAula(codigoAula);
        if (aula == null) {
            throw new IllegalArgumentException("No existe el aula con código: " + codigoAula);
        }
        aula.liberar(dia, hora, duracion);
        historialOperaciones.registrarOperacion(new Operacion.Horario(aula, dia, hora, duracion, false));
    }

    public boolean consultarDisponibilidadAula(String codigoAula, int dia, int hora) {
        Aula aula = buscarAula(codigoAula);
        if (aula == null) {
            throw new IllegalArgumentException("No existe el aula con código: " + codigoAula);
        }
        return aula.consultarDisponibilidad(dia, hora);
    }

    public Map<String, Aula> getAulas() {
        return aulas;
    }

    // =========================================================================
    // 4. RUTAS ENTRE EDIFICIOS (GRAFO + DIJKSTRA)
    // =========================================================================

    public void agregarConexionCampus(int origen, int destino, int distanciaMetros) {
        grafoCampus.agregarConexion(origen, destino, distanciaMetros);
    }

    public GrafoCampus.ResultadoRuta calcularRutaMasCorta(int origen, int destino) {
        return grafoCampus.calcularRutaMasCorta(origen, destino);
    }

    public GrafoCampus getGrafoCampus() {
        return grafoCampus;
    }

    // =========================================================================
    // 5. REPORTES ACADÉMICOS CON ARREGLOS ESTÁTICOS
    // =========================================================================

    public void registrarNotaEstudiante(String idEstudiante, int semestre, String codigoMateria, double nota)
            throws EstudianteNoEncontradoException {
        Estudiante est = buscarEstudiante(idEstudiante);
        Materia mat = buscarMateria(codigoMateria);
        if (mat == null) {
            throw new IllegalArgumentException("La materia " + codigoMateria + " no existe en el catálogo.");
        }
        boolean ok = est.registrarNota(semestre, mat.getCodigo(), nota);
        if (!ok) {
            throw new IllegalStateException("Se alcanzó el límite máximo de 20 materias para el semestre " + semestre);
        }
        historialOperaciones.registrarOperacion(new Operacion.Nota(est, semestre, mat.getCodigo(), nota));
    }

    public String generarReporteAcademico(String idEstudiante) throws EstudianteNoEncontradoException {
        Estudiante est = buscarEstudiante(idEstudiante);
        String reporte = est.generarReporteAcademico();
        navegadorReportes.registrarReporte(reporte);
        return reporte;
    }

    public NavegadorReportes getNavegadorReportes() {
        return navegadorReportes;
    }

    // =========================================================================
    // 6. SISTEMA DESHACER / REHACER (PILAS)
    // =========================================================================

    public String deshacerOperacion() throws PilaDeshacerVaciaException {
        return historialOperaciones.deshacer();
    }

    public String rehacerOperacion() throws PilaDeshacerVaciaException {
        return historialOperaciones.rehacer();
    }

    public HistorialOperaciones getHistorialOperaciones() {
        return historialOperaciones;
    }

    // =========================================================================
    // 7. PROCESAMIENTO POR LOTES (BATCH - COLAS)
    // =========================================================================

    public ResumenBatch procesarArchivoBatch(String rutaArchivo) throws ArchivoInvalidoException {
        List<SolicitudInscripcion> solicitudes = LectorCSV.leerSolicitudesCSV(rutaArchivo);
        colaBatch.clear();
        colaBatch.addAll(solicitudes);

        int total = colaBatch.size();
        int exitosas = 0;
        int fallidas = 0;
        List<RegistroResultadoBatch> detalles = new ArrayList<>();

        int indice = 1;
        while (!colaBatch.isEmpty()) {
            SolicitudInscripcion sol = colaBatch.poll();
            String idEst = sol.getIdEstudiante();
            String codMat = sol.getCodigoMateria();

            Estudiante est = estudiantes.get(idEst);
            if (est == null) {
                fallidas++;
                detalles.add(new RegistroResultadoBatch(indice, idEst, codMat, false, "Estudiante no existe"));
                indice++;
                continue;
            }

            Materia mat = materias.get(codMat);
            if (mat == null) {
                fallidas++;
                detalles.add(new RegistroResultadoBatch(indice, idEst, codMat, false, "Materia no existe"));
                indice++;
                continue;
            }

            try {
                Materia.ResultadoInscripcion res = mat.inscribirEstudiante(est);
                if (res.getEstado() == Materia.EstadoInscripcion.EXITOSA) {
                    exitosas++;
                    detalles.add(new RegistroResultadoBatch(indice, idEst, codMat, true, "Exitosa"));
                } else if (res.getEstado() == Materia.EstadoInscripcion.EN_COLA_DE_ESPERA) {
                    fallidas++;
                    detalles.add(new RegistroResultadoBatch(indice, idEst, codMat, false, "Cupo lleno (En cola de espera)"));
                } else {
                    fallidas++;
                    detalles.add(new RegistroResultadoBatch(indice, idEst, codMat, false, res.getMensaje()));
                }
            } catch (PreRequisitoNoAprobadoException e) {
                fallidas++;
                detalles.add(new RegistroResultadoBatch(indice, idEst, codMat, false, "Pre-requisitos no cumplidos"));
            }
            indice++;
        }

        return new ResumenBatch(total, exitosas, fallidas, detalles);
    }

    public Facultad[] getFacultades() {
        return facultades;
    }

    // =========================================================================
    // ESTRUCTURAS DE SOPORTE PARA RESULTADOS BATCH
    // =========================================================================

    public static class RegistroResultadoBatch {
        private final int numero;
        private final String idEstudiante;
        private final String codigoMateria;
        private final boolean exitosa;
        private final String motivo;

        public RegistroResultadoBatch(int numero, String idEstudiante, String codigoMateria, boolean exitosa, String motivo) {
            this.numero = numero;
            this.idEstudiante = idEstudiante;
            this.codigoMateria = codigoMateria;
            this.exitosa = exitosa;
            this.motivo = motivo;
        }

        public int getNumero() { return numero; }
        public String getIdEstudiante() { return idEstudiante; }
        public String getCodigoMateria() { return codigoMateria; }
        public boolean isExitosa() { return exitosa; }
        public String getMotivo() { return motivo; }
    }

    public static class ResumenBatch {
        private final int total;
        private final int exitosas;
        private final int fallidas;
        private final List<RegistroResultadoBatch> detalles;

        public ResumenBatch(int total, int exitosas, int fallidas, List<RegistroResultadoBatch> detalles) {
            this.total = total;
            this.exitosas = exitosas;
            this.fallidas = fallidas;
            this.detalles = detalles;
        }

        public int getTotal() { return total; }
        public int getExitosas() { return exitosas; }
        public int getFallidas() { return fallidas; }
        public List<RegistroResultadoBatch> getDetalles() { return detalles; }
    }
}
