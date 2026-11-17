package util;

import java.util.Scanner;

/**
 * Provee métodos utilitarios para capturar y validar entradas del usuario por teclado,
 * evitando excepciones no controladas por formato (como InputMismatchException).
 */
public class ValidadorEntrada {

    public static int leerEntero(Scanner scanner, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor < min || valor > max) {
                    System.out.printf("  [!] Error: El valor debe estar entre %d y %d. Intente de nuevo.\n", min, max);
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Error: Debe ingresar un número entero válido.");
            }
        }
    }

    public static double leerDouble(Scanner scanner, String mensaje, double min, double max) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(linea);
                if (valor < min || valor > max) {
                    System.out.printf("  [!] Error: El valor debe estar entre %.2f y %.2f. Intente de nuevo.\n", min, max);
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Error: Debe ingresar un número decimal válido (ej: 4.5).");
            }
        }
    }

    public static String leerTexto(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) {
                System.out.println("  [!] Error: Este campo no puede estar vacío.");
                continue;
            }
            return linea;
        }
    }

    public static String leerTextoOpcional(Scanner scanner, String mensaje, String valorPorDefecto) {
        System.out.print(mensaje);
        String linea = scanner.nextLine().trim();
        if (linea.isEmpty()) {
            return valorPorDefecto;
        }
        return linea;
    }
}

