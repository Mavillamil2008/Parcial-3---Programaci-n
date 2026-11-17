package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import excepciones.ArchivoInvalidoException;
import modelo.SolicitudInscripcion;

/**
 * Utilidad encargada de la lectura y validación sintáctica de archivos CSV de solicitudes por lotes.
 */
public class LectorCSV {

    /**
     * Lee un archivo CSV que contiene pares idEstudiante,codigoMateria.
     * @param rutaArchivo Ruta absoluta o relativa del archivo CSV.
     * @return Lista de solicitudes de inscripción leídas.
     * @throws ArchivoInvalidoException Si el archivo no existe, no se puede leer o tiene formato incorrecto.
     */
    public static List<SolicitudInscripcion> leerSolicitudesCSV(String rutaArchivo) throws ArchivoInvalidoException {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new ArchivoInvalidoException("La ruta del archivo no puede ser nula ni vacía.");
        }

        File archivo = new File(rutaArchivo.trim());
        if (!archivo.exists() || !archivo.isFile()) {
            throw new ArchivoInvalidoException("No se encontró el archivo en la ruta especificada: " + archivo.getAbsolutePath());
        }

        List<SolicitudInscripcion> solicitudes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numLinea = 0;
            
            while ((linea = br.readLine()) != null) {
                numLinea++;
                String limpia = linea.trim();
                if (limpia.isEmpty() || limpia.startsWith("#")) {
                    continue; // Ignorar líneas en blanco o comentarios
                }

                // Omitir cabecera si existe
                if (numLinea == 1 && (limpia.toLowerCase().contains("idestudiante") || limpia.toLowerCase().contains("estudiante"))) {
                    continue;
                }

                String[] partes = limpia.split(",");
                if (partes.length < 2) {
                    throw new ArchivoInvalidoException(
                        String.format("Línea %d con formato inválido. Debe contener: idEstudiante,codigoMateria -> '%s'", numLinea, linea)
                    );
                }

                String idEstudiante = partes[0].trim();
                String codigoMateria = partes[1].trim().toUpperCase();

                if (idEstudiante.isEmpty() || codigoMateria.isEmpty()) {
                    throw new ArchivoInvalidoException(
                        String.format("Línea %d contiene campos vacíos -> '%s'", numLinea, linea)
                    );
                }

                solicitudes.add(new SolicitudInscripcion(idEstudiante, codigoMateria));
            }

        } catch (IOException e) {
            throw new ArchivoInvalidoException("Error de entrada/salida al leer el archivo CSV: " + e.getMessage());
        }

        return solicitudes;
    }
}

