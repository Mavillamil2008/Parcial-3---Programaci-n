package util;

/**
 * Utilidad gráfica para la consola que presenta separadores, marcos y títulos limpios.
 */
public class FormateadorConsola {

    public static final String SEPARADOR_DOBLE = "============================================================";
    public static final String SEPARADOR_SIMPLE = "------------------------------------------------------------";

    public static void imprimirTitulo(String titulo) {
        System.out.println(SEPARADOR_DOBLE);
        System.out.println("  " + titulo.toUpperCase());
        System.out.println(SEPARADOR_DOBLE);
    }

    public static void imprimirSubtitulo(String subtitulo) {
        System.out.println("\n--- " + subtitulo + " ---");
    }

    public static void imprimirExito(String mensaje) {
        System.out.println("✔ " + mensaje);
    }

    public static void imprimirError(String excepcionNombre, String mensaje) {
        System.out.println("Error: " + excepcionNombre + " - " + mensaje);
    }

    public static void imprimirInfo(String mensaje) {
        System.out.println("ℹ " + mensaje);
    }
}

