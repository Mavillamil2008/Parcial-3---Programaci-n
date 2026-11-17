package app;

import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import excepciones.*;
import estructuras.GrafoCampus;
import modelo.Aula;
import modelo.Estudiante;
import modelo.Materia;
import servicio.SistemaAcademico;
import util.FormateadorConsola;
import util.ValidadorEntrada;

/**
 * Clase principal del sistema que despliega el menú interactivo por consola
 * con las 22 opciones obligatorias especificadas en el proyecto.
 */
public class Main {

    private static final SistemaAcademico sistema = new SistemaAcademico();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = ValidadorEntrada.leerEntero(scanner, "Seleccione una opcion: ", 1, 22);

            try {
                switch (opcion) {
                    // === GESTIÓN DE ESTUDIANTES ===
                    case 1 -> registrarEstudiante();
                    case 2 -> buscarEstudiantePorId();
                    case 3 -> listarTodosLosEstudiantes();
                    case 4 -> eliminarEstudiante();

                    // === GESTIÓN DE MATERIAS ===
                    case 5 -> crearMateria();
                    case 6 -> agregarPrerequisito();
                    case 7 -> mostrarPrerequisitos();
                    case 8 -> inscribirEstudiante();
                    case 9 -> cancelarInscripcion();
                    case 10 -> mostrarColaEspera();

                    // === GESTIÓN DE HORARIOS ===
                    case 11 -> reservarHorarioEnAula();
                    case 12 -> liberarHorario();
                    case 13 -> consultarDisponibilidad();

                    // === RUTAS ENTRE EDIFICIOS ===
                    case 14 -> agregarConexionEntreEdificios();
                    case 15 -> calcularRutaMasCorta();

                    // === REPORTES ACADÉMICOS ===
                    case 16 -> registrarNota();
                    case 17 -> verReporteAcademico();
                    case 18 -> navegadorDeReportes();

                    // === SISTEMA DESHACER/REHACER ===
                    case 19 -> deshacerUltimaOperacion();
                    case 20 -> rehacerUltimaOperacion();

                    // === PROCESAMIENTO POR LOTES ===
                    case 21 -> procesarArchivoCSV();

                    // === SALIR ===
                    case 22 -> {
                        System.out.println("\nGracias por utilizar el Sistema de Gestión Académica. ¡Hasta pronto!");
                        salir = true;
                    }
                }
            } catch (Exception e) {
                // Manejo de excepciones imprevistas
                System.out.println("Error inesperado: " + e.getMessage());
            }

            if (!salir) {
                System.out.println("\nPresione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n" + FormateadorConsola.SEPARADOR_DOBLE);
        System.out.println("PLANIFICACIÓN ACADÉMICA - SISTEMA UNIVERSITARIO");
        System.out.println(FormateadorConsola.SEPARADOR_DOBLE);
        System.out.println("=== GESTION DE ESTUDIANTES ===");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Buscar estudiante por ID");
        System.out.println("3. Listar todos los estudiantes");
        System.out.println("4. Eliminar estudiante");
        System.out.println("\n=== GESTION DE MATERIAS ===");
        System.out.println("5. Crear materia");
        System.out.println("6. Agregar pre-requisito");
        System.out.println("7. Mostrar pre-requisitos");
        System.out.println("8. Inscribir estudiante");
        System.out.println("9. Cancelar inscripcion");
        System.out.println("10. Mostrar cola de espera");
        System.out.println("\n=== GESTION DE HORARIOS ===");
        System.out.println("11. Reservar horario en aula");
        System.out.println("12. Liberar horario");
        System.out.println("13. Consultar disponibilidad");
        System.out.println("\n=== RUTAS ENTRE EDIFICIOS ===");
        System.out.println("14. Agregar conexion entre edificios");
        System.out.println("15. Calcular ruta mas corta");
        System.out.println("\n=== REPORTES ACADEMICOS ===");
        System.out.println("16. Registrar nota");
        System.out.println("17. Ver reporte academico");
        System.out.println("18. Navegador de reportes (atras/adelante)");
        System.out.println("\n=== SISTEMA DESHACER/REHACER ===");
        System.out.println("19. Deshacer última operacion");
        System.out.println("20. Rehacer ultima operacion");
        System.out.println("\n=== PROCESAMIENTO POR LOTES ===");
        System.out.println("21. Procesar archivo CSV");
        System.out.println("\n=== SALIR ===");
        System.out.println("22. Salir\n");
    }

    // =========================================================================
    // 1-4. ESTUDIANTES
    // =========================================================================

    private static void registrarEstudiante() {
        FormateadorConsola.imprimirSubtitulo("REGISTRO DE ESTUDIANTE");
        String id = ValidadorEntrada.leerTexto(scanner, "ID: ");
        String nombre = ValidadorEntrada.leerTexto(scanner, "Nombre: ");
        String email = ValidadorEntrada.leerTexto(scanner, "Email: ");
        int semestre = ValidadorEntrada.leerEntero(scanner, "Semestre actual: ", 1, 10);

        try {
            sistema.registrarEstudiante(id, nombre, email, semestre);
            System.out.println("\nEstudiante registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void buscarEstudiantePorId() {
        FormateadorConsola.imprimirSubtitulo("BUSCAR ESTUDIANTE");
        String id = ValidadorEntrada.leerTexto(scanner, "ID: ");
        try {
            Estudiante est = sistema.buscarEstudiante(id);
            System.out.println("\nResultado encontrado:");
            System.out.println(est.mostrarInformacion());
        } catch (EstudianteNoEncontradoException e) {
            FormateadorConsola.imprimirError("EstudianteNoEncontradoException", e.getMessage());
        }
    }

    private static void listarTodosLosEstudiantes() {
        FormateadorConsola.imprimirSubtitulo("LISTA DE ESTUDIANTES REGISTRADOS");
        List<Estudiante> lista = sistema.listarEstudiantes();
        if (lista.isEmpty()) {
            System.out.println("No hay estudiantes registrados en el sistema.");
            return;
        }
        System.out.printf("%-10s | %-25s | %-30s | %-8s | %-8s\n", "ID", "NOMBRE", "EMAIL", "SEMESTRE", "PROMEDIO");
        System.out.println(FormateadorConsola.SEPARADOR_SIMPLE);
        for (Estudiante e : lista) {
            System.out.printf("%-10s | %-25s | %-30s | %-8d | %-8.2f\n",
                e.getId(), e.getNombre(), e.getEmail(), e.getSemestre(), e.calcularPromedioAcumulado());
        }
        System.out.println("Total de estudiantes: " + lista.size());
    }

    private static void eliminarEstudiante() {
        FormateadorConsola.imprimirSubtitulo("ELIMINAR ESTUDIANTE (BAJA DEFINITIVA)");
        String id = ValidadorEntrada.leerTexto(scanner, "ID del estudiante a eliminar: ");
        try {
            Estudiante eliminado = sistema.eliminarEstudiante(id);
            System.out.printf("Estudiante %s (ID: %s) eliminado exitosamente.\n", eliminado.getNombre(), eliminado.getId());
            System.out.println("(Nota: Esta operación fue almacenada en la pila y puede deshacerse desde la opción 19).");
        } catch (EstudianteNoEncontradoException e) {
            FormateadorConsola.imprimirError("EstudianteNoEncontradoException", e.getMessage());
        }
    }

    // =========================================================================
    // 5-10. MATERIAS
    // =========================================================================

    private static void crearMateria() {
        FormateadorConsola.imprimirSubtitulo("CREAR MATERIA");
        String codigo = ValidadorEntrada.leerTexto(scanner, "Código (ej: CALC101): ");
        String nombre = ValidadorEntrada.leerTexto(scanner, "Nombre de la materia: ");
        int cupos = ValidadorEntrada.leerEntero(scanner, "Cupos máximos: ", 1, 500);
        int creditos = ValidadorEntrada.leerEntero(scanner, "Créditos académicos: ", 1, 10);

        try {
            sistema.crearMateria(codigo, nombre, cupos, creditos);
            System.out.println("\nMateria creada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void agregarPrerequisito() {
        FormateadorConsola.imprimirSubtitulo("AGREGAR PRE-REQUISITO");
        String codigoMateria = ValidadorEntrada.leerTexto(scanner, "Código de la materia objetivo: ");
        String codigoPre = ValidadorEntrada.leerTexto(scanner, "Código de la materia pre-requisito: ");

        try {
            boolean agregado = sistema.agregarPrerequisito(codigoMateria, codigoPre);
            if (agregado) {
                System.out.printf("Pre-requisito %s agregado con éxito a %s.\n", codigoPre.toUpperCase(), codigoMateria.toUpperCase());
            } else {
                System.out.println("El pre-requisito ya se encontraba registrado en esta materia.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void mostrarPrerequisitos() {
        FormateadorConsola.imprimirSubtitulo("MOSTRAR PRE-REQUISITOS");
        String codigoMateria = ValidadorEntrada.leerTexto(scanner, "Código de la materia: ");
        try {
            List<String> pres = sistema.obtenerPrerequisitos(codigoMateria);
            System.out.println("Pre-requisitos de " + codigoMateria.toUpperCase() + ":");
            if (pres.isEmpty()) {
                System.out.println("  (Esta materia no tiene pre-requisitos).");
            } else {
                for (String p : pres) {
                    System.out.println("  • " + p);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void inscribirEstudiante() {
        FormateadorConsola.imprimirSubtitulo("INSCRIBIR ESTUDIANTE EN MATERIA");
        String id = ValidadorEntrada.leerTexto(scanner, "ID del estudiante: ");
        String cod = ValidadorEntrada.leerTexto(scanner, "Código de la materia: ");

        try {
            Materia.ResultadoInscripcion res = sistema.inscribirEstudianteEnMateria(id, cod);
            if (res.getEstado() == Materia.EstadoInscripcion.EXITOSA) {
                System.out.println("Inscripcion exitosa -> " + res.getMensaje());
            } else if (res.getEstado() == Materia.EstadoInscripcion.EN_COLA_DE_ESPERA) {
                System.out.println(res.getMensaje());
            } else {
                System.out.println(res.getMensaje());
            }
        } catch (EstudianteNoEncontradoException e) {
            FormateadorConsola.imprimirError("EstudianteNoEncontradoException", e.getMessage());
        } catch (PreRequisitoNoAprobadoException e) {
            FormateadorConsola.imprimirError("PreRequisitoNoAprobadoException", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cancelarInscripcion() {
        FormateadorConsola.imprimirSubtitulo("CANCELAR INSCRIPCION");
        String id = ValidadorEntrada.leerTexto(scanner, "ID del estudiante: ");
        String cod = ValidadorEntrada.leerTexto(scanner, "Código de la materia: ");

        try {
            Materia.ResultadoInscripcion res = sistema.cancelarInscripcionMateria(id, cod);
            System.out.println(res.getMensaje());
            if (res.getEstudiantePromovido() != null) {
                System.out.printf("Asignando cupo a %s (primer estudiante en cola)\n", res.getEstudiantePromovido().getNombre());
            }
        } catch (EstudianteNoEncontradoException e) {
            FormateadorConsola.imprimirError("EstudianteNoEncontradoException", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void mostrarColaEspera() {
        FormateadorConsola.imprimirSubtitulo("MOSTRAR COLA DE ESPERA");
        String cod = ValidadorEntrada.leerTexto(scanner, "Código de la materia: ");

        try {
            Queue<Estudiante> cola = sistema.obtenerColaEspera(cod);
            System.out.println("--- COLA DE ESPERA DE " + cod.toUpperCase() + " ---");
            int pos = 1;
            for (Estudiante e : cola) {
                System.out.printf("Posicion %d: %s (ID: %s)\n", pos++, e.getNombre(), e.getId());
            }
            System.out.println("Total en espera: " + cola.size());
        } catch (ColaDeEsperaVaciaException e) {
            FormateadorConsola.imprimirError("ColaDeEsperaVaciaException", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // 11-13. GESTIÓN DE HORARIOS CON MATRIZ
    // =========================================================================

    private static void reservarHorarioEnAula() {
        FormateadorConsola.imprimirSubtitulo("RESERVAR HORARIO EN AULA");
        String cod = ValidadorEntrada.leerTexto(scanner, "Aula: ");
        System.out.println("Días: 0=Dom, 1=Lun, 2=Mar, 3=Mié, 4=Jue, 5=Vie, 6=Sáb");
        int dia = ValidadorEntrada.leerEntero(scanner, "Dia: ", 0, 6);
        int hora = ValidadorEntrada.leerEntero(scanner, "Hora (0 a 23): ", 0, 23);
        int duracion = ValidadorEntrada.leerEntero(scanner, "Duracion (horas): ", 1, 24 - hora);

        System.out.println("\nVerificando disponibilidad...");
        Aula aula = sistema.buscarAula(cod);
        if (aula == null) {
            System.out.println("Error: El aula " + cod + " no existe.");
            return;
        }

        for (int h = hora; h < hora + duracion; h++) {
            boolean libre = aula.consultarDisponibilidad(dia, h);
            System.out.printf("%s %d:00 -> %s\n", Aula.DIAS_SEMANA[dia], h, (libre ? "LIBRE" : "OCUPADO"));
        }

        try {
            sistema.reservarHorarioAula(cod, dia, hora, duracion);
            System.out.println("\nReserva exitosa.");
        } catch (HorarioConflictivoException e) {
            System.out.println();
            FormateadorConsola.imprimirError("HorarioConflictivoException", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void liberarHorario() {
        FormateadorConsola.imprimirSubtitulo("LIBERAR HORARIO EN AULA");
        String cod = ValidadorEntrada.leerTexto(scanner, "Aula: ");
        int dia = ValidadorEntrada.leerEntero(scanner, "Dia (0-6): ", 0, 6);
        int hora = ValidadorEntrada.leerEntero(scanner, "Hora (0 a 23): ", 0, 23);
        int duracion = ValidadorEntrada.leerEntero(scanner, "Duracion (horas): ", 1, 24 - hora);

        try {
            sistema.liberarHorarioAula(cod, dia, hora, duracion);
            System.out.printf("Horario liberado exitosamente en aula %s (%s de %d:00 a %d:00).\n",
                cod, Aula.DIAS_SEMANA[dia], hora, hora + duracion);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void consultarDisponibilidad() {
        FormateadorConsola.imprimirSubtitulo("CONSULTAR DISPONIBILIDAD DE HORARIO");
        String cod = ValidadorEntrada.leerTexto(scanner, "Aula: ");
        int dia = ValidadorEntrada.leerEntero(scanner, "Dia (0-6): ", 0, 6);
        int hora = ValidadorEntrada.leerEntero(scanner, "Hora (0 a 23): ", 0, 23);

        try {
            boolean disponible = sistema.consultarDisponibilidadAula(cod, dia, hora);
            System.out.printf("Aula %s | %s %d:00 -> Estado: %s\n",
                cod, Aula.DIAS_SEMANA[dia], hora, (disponible ? "LIBRE (Disponible)" : "OCUPADO (Reservado)"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // 14-15. RUTAS ENTRE EDIFICIOS (GRAFO + DIJKSTRA)
    // =========================================================================

    private static void agregarConexionEntreEdificios() {
        FormateadorConsola.imprimirSubtitulo("AGREGAR CONEXION ENTRE EDIFICIOS");
        mostrarEdificiosRegistrados();
        int origen = ValidadorEntrada.leerEntero(scanner, "Edificio Origen (número): ", 0, sistema.getGrafoCampus().getNumEdificios() - 1);
        int destino = ValidadorEntrada.leerEntero(scanner, "Edificio Destino (número): ", 0, sistema.getGrafoCampus().getNumEdificios() - 1);
        if (origen == destino) {
            System.out.println("Error: El origen y destino no pueden ser el mismo edificio.");
            return;
        }
        int distancia = ValidadorEntrada.leerEntero(scanner, "Distancia en metros: ", 1, 10000);

        try {
            sistema.agregarConexionCampus(origen, destino, distancia);
            System.out.printf("Conexión agregada exitosamente entre %s y %s (%d metros).\n",
                sistema.getGrafoCampus().getNombresEdificios()[origen],
                sistema.getGrafoCampus().getNombresEdificios()[destino],
                distancia);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void calcularRutaMasCorta() {
        FormateadorConsola.imprimirSubtitulo("CALCULAR RUTA MAS CORTA");
        mostrarEdificiosRegistrados();
        int origen = ValidadorEntrada.leerEntero(scanner, "Origen (número): ", 0, sistema.getGrafoCampus().getNumEdificios() - 1);
        int destino = ValidadorEntrada.leerEntero(scanner, "Destino (número): ", 0, sistema.getGrafoCampus().getNumEdificios() - 1);

        String[] nombres = sistema.getGrafoCampus().getNombresEdificios();
        System.out.println("\nOrigen: " + nombres[origen]);
        System.out.println("Destino: " + nombres[destino]);

        GrafoCampus.ResultadoRuta res = sistema.calcularRutaMasCorta(origen, destino);
        System.out.println("\n--- RESULTADO ---");
        if (res.isExiste()) {
            System.out.println("Ruta mas corta:");
            System.out.println("  " + res.getDetalleCamino());
            System.out.println("Distancia TOTAL: " + res.getDistanciaTotal() + " metros");
        } else {
            System.out.println(res.getDetalleCamino());
        }
    }

    private static void mostrarEdificiosRegistrados() {
        System.out.println("Edificios registrados:");
        String[] nombres = sistema.getGrafoCampus().getNombresEdificios();
        for (int i = 0; i < nombres.length; i++) {
            System.out.printf("  %d: %s\n", i, nombres[i]);
        }
        System.out.println();
    }

    // =========================================================================
    // 16-18. REPORTES ACADÉMICOS
    // =========================================================================

    private static void registrarNota() {
        FormateadorConsola.imprimirSubtitulo("REGISTRAR NOTA DE ESTUDIANTE");
        String id = ValidadorEntrada.leerTexto(scanner, "ID del estudiante: ");
        int semestre = ValidadorEntrada.leerEntero(scanner, "Semestre (1 a 10): ", 1, 10);
        String cod = ValidadorEntrada.leerTexto(scanner, "Código de la materia: ");
        double nota = ValidadorEntrada.leerDouble(scanner, "Calificación (0.0 a 5.0): ", 0.0, 5.0);

        try {
            sistema.registrarNotaEstudiante(id, semestre, cod, nota);
            System.out.println("\nNota registrada exitosamente en el arreglo estático Double[10][20].");
            System.out.println("(Operación apilada en el sistema de deshacer).");
        } catch (EstudianteNoEncontradoException e) {
            FormateadorConsola.imprimirError("EstudianteNoEncontradoException", e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verReporteAcademico() {
        FormateadorConsola.imprimirSubtitulo("VER REPORTE ACADEMICO");
        String id = ValidadorEntrada.leerTexto(scanner, "ID del estudiante: ");
        try {
            String reporte = sistema.generarReporteAcademico(id);
            System.out.println("\n" + reporte);
            System.out.println("ℹ El reporte ha sido almacenado en el historial de navegación.");
        } catch (EstudianteNoEncontradoException e) {
            FormateadorConsola.imprimirError("EstudianteNoEncontradoException", e.getMessage());
        }
    }

    private static void navegadorDeReportes() {
        FormateadorConsola.imprimirSubtitulo("NAVEGADOR DE REPORTES (CON PILAS)");
        var nav = sistema.getNavegadorReportes();
        System.out.println("Pila Atrás: " + nav.getHistorialAtrasCount() + " reportes | Pila Adelante: " + nav.getHistorialAdelanteCount() + " reportes");
        System.out.println("1. Ver reporte actual");
        System.out.println("2. Retroceder (Atrás)");
        System.out.println("3. Avanzar (Adelante)");
        System.out.println("4. Volver al menú principal");

        int op = ValidadorEntrada.leerEntero(scanner, "Seleccione opción de navegación: ", 1, 4);
        switch (op) {
            case 1 -> {
                String act = nav.getReporteActual();
                if (act == null) System.out.println("No hay ningún reporte visualizado recientemente.");
                else System.out.println("\n[REPORTE ACTUAL]\n" + act);
            }
            case 2 -> {
                String ant = nav.retroceder();
                if (ant == null) System.out.println("No hay más reportes hacia atrás en la pila.");
                else System.out.println("\n[REPORTE ANTERIOR]\n" + ant);
            }
            case 3 -> {
                String sig = nav.avanzar();
                if (sig == null) System.out.println("No hay más reportes hacia adelante en la pila.");
                else System.out.println("\n[REPORTE SIGUIENTE]\n" + sig);
            }
            case 4 -> {}
        }
    }

    // =========================================================================
    // 19-20. SISTEMA DESHACER / REHACER
    // =========================================================================

    private static void deshacerUltimaOperacion() {
        FormateadorConsola.imprimirSubtitulo("DESHACER OPERACIÓN");
        try {
            String res = sistema.deshacerOperacion();
            System.out.println("Operacion deshecha: " + res);
        } catch (PilaDeshacerVaciaException e) {
            FormateadorConsola.imprimirError("PilaDeshacerVaciaException", e.getMessage());
        }
    }

    private static void rehacerUltimaOperacion() {
        FormateadorConsola.imprimirSubtitulo("REHACER OPERACIÓN");
        try {
            String res = sistema.rehacerOperacion();
            System.out.println("Operacion rehecha: " + res);
        } catch (PilaDeshacerVaciaException e) {
            FormateadorConsola.imprimirError("PilaDeshacerVaciaException", e.getMessage());
        }
    }

    // =========================================================================
    // 21. PROCESAMIENTO POR LOTES (BATCH - CSV)
    // =========================================================================

    private static void procesarArchivoCSV() {
        FormateadorConsola.imprimirSubtitulo("PROCESAMIENTO MASIVO (BATCH)");
        String ruta = ValidadorEntrada.leerTextoOpcional(scanner, "Ruta del archivo CSV [Por defecto: inscripciones.csv]: ", "inscripciones.csv");

        try {
            System.out.println("Leyendo archivo y encolando solicitudes...");
            var resumen = sistema.procesarArchivoBatch(ruta);
            System.out.println("Se encolaron " + resumen.getTotal() + " solicitudes.");
            System.out.println("\nProcesando cola...");

            for (var reg : resumen.getDetalles()) {
                System.out.printf("[%d/%d] %s → %s  %s%s\n",
                    reg.getNumero(), resumen.getTotal(), reg.getIdEstudiante(), reg.getCodigoMateria(),
                    (reg.isExitosa() ? "Exitosa" : "Fallida"),
                    (!reg.isExitosa() ? " (" + reg.getMotivo() + ")" : "")
                );
            }

            System.out.println("\n=== RESUMEN ===");
            System.out.println("Exitosas: " + resumen.getExitosas());
            System.out.println("Fallidas: " + resumen.getFallidas());

        } catch (ArchivoInvalidoException e) {
            FormateadorConsola.imprimirError("ArchivoInvalidoException", e.getMessage());
        }
    }
}

