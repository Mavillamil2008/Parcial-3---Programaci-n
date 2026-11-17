package estructuras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Gestiona la infraestructura física del campus universitario mediante un Grafo No Dirigido.
 * Utiliza una matriz de adyacencia nativa int[N][N] para representar distancias en metros.
 * Implementa el algoritmo de Dijkstra para determinar la ruta óptima entre dos edificios.
 */
public class GrafoCampus {
    private final int numEdificios;
    private final String[] nombresEdificios;
    private final int[][] matrizAdyacencia; // Matriz nativa int[N][N]
    public static final int INFINITO = Integer.MAX_VALUE / 2; // Evita desbordamiento aritmético

    public GrafoCampus(String[] nombres) {
        this.numEdificios = nombres.length;
        this.nombresEdificios = Arrays.copyOf(nombres, nombres.length);
        this.matrizAdyacencia = new int[numEdificios][numEdificios];

        // Inicializar matriz de adyacencia con INFINITO excepto la diagonal principal (distancia a sí mismo = 0)
        for (int i = 0; i < numEdificios; i++) {
            for (int j = 0; j < numEdificios; j++) {
                if (i == j) {
                    matrizAdyacencia[i][j] = 0;
                } else {
                    matrizAdyacencia[i][j] = INFINITO;
                }
            }
        }
    }

    /**
     * Agrega o actualiza una conexión bidireccional entre dos edificios.
     * @param origen Índice del edificio origen.
     * @param destino Índice del edificio destino.
     * @param distancia Distancia en metros (debe ser positiva).
     */
    public void agregarConexion(int origen, int destino, int distancia) {
        if (origen < 0 || origen >= numEdificios || destino < 0 || destino >= numEdificios) {
            throw new IllegalArgumentException("Índices de edificio fuera de rango válido.");
        }
        if (distancia <= 0) {
            throw new IllegalArgumentException("La distancia en metros debe ser mayor a cero.");
        }
        matrizAdyacencia[origen][destino] = distancia;
        matrizAdyacencia[destino][origen] = distancia; // Grafo no dirigido
    }

    /**
     * Calcula la ruta más corta entre origen y destino mediante el algoritmo de Dijkstra.
     * @param origen Índice del edificio de inicio.
     * @param destino Índice del edificio meta.
     * @return ResultadoRuta con la distancia total y la secuencia detallada de tramos.
     */
    public ResultadoRuta calcularRutaMasCorta(int origen, int destino) {
        if (origen < 0 || origen >= numEdificios || destino < 0 || destino >= numEdificios) {
            throw new IllegalArgumentException("Índices de edificio fuera de rango.");
        }

        int[] distancias = new int[numEdificios];
        boolean[] visitados = new boolean[numEdificios];
        int[] predecesores = new int[numEdificios];

        Arrays.fill(distancias, INFINITO);
        Arrays.fill(predecesores, -1);
        distancias[origen] = 0;

        for (int i = 0; i < numEdificios; i++) {
            // Encontrar el vértice no visitado con menor distancia
            int u = -1;
            int menorDistancia = INFINITO;
            for (int v = 0; v < numEdificios; v++) {
                if (!visitados[v] && distancias[v] < menorDistancia) {
                    menorDistancia = distancias[v];
                    u = v;
                }
            }

            if (u == -1 || distancias[u] == INFINITO) {
                break; // Nodos restantes inalcanzables
            }

            visitados[u] = true;

            // Relajar aristas de vecinos
            for (int v = 0; v < numEdificios; v++) {
                int pesoArista = matrizAdyacencia[u][v];
                if (!visitados[v] && pesoArista != INFINITO) {
                    if (distancias[u] + pesoArista < distancias[v]) {
                        distancias[v] = distancias[u] + pesoArista;
                        predecesores[v] = u;
                    }
                }
            }
        }

        if (distancias[destino] == INFINITO) {
            return new ResultadoRuta(false, -1, Collections.emptyList(), "No existe una ruta transitable entre los edificios seleccionados.");
        }

        // Reconstrucción del camino desde destino hasta origen
        List<Integer> caminoIndices = new ArrayList<>();
        int actual = destino;
        while (actual != -1) {
            caminoIndices.add(actual);
            actual = predecesores[actual];
        }
        Collections.reverse(caminoIndices);

        // Construcción de la cadena detallada requerida en el ejemplo:
        // Ej: Ingenieria -> Cafeteria (150m) -> Rectoria (180m)
        StringBuilder sb = new StringBuilder();
        sb.append(nombresEdificios[caminoIndices.get(0)]);
        for (int i = 0; i < caminoIndices.size() - 1; i++) {
            int u = caminoIndices.get(i);
            int v = caminoIndices.get(i + 1);
            int tramo = matrizAdyacencia[u][v];
            sb.append(" -> ")
              .append(nombresEdificios[v])
              .append(" (")
              .append(tramo)
              .append("m)");
        }

        return new ResultadoRuta(true, distancias[destino], caminoIndices, sb.toString());
    }

    public int getNumEdificios() {
        return numEdificios;
    }

    public String[] getNombresEdificios() {
        return nombresEdificios;
    }

    public int[][] getMatrizAdyacencia() {
        return matrizAdyacencia;
    }

    public static class ResultadoRuta {
        private final boolean existe;
        private final int distanciaTotal;
        private final List<Integer> rutaNodos;
        private final String detalleCamino;

        public ResultadoRuta(boolean existe, int distanciaTotal, List<Integer> rutaNodos, String detalleCamino) {
            this.existe = existe;
            this.distanciaTotal = distanciaTotal;
            this.rutaNodos = rutaNodos;
            this.detalleCamino = detalleCamino;
        }

        public boolean isExiste() {
            return existe;
        }

        public int getDistanciaTotal() {
            return distanciaTotal;
        }

        public List<Integer> getRutaNodos() {
            return rutaNodos;
        }

        public String getDetalleCamino() {
            return detalleCamino;
        }
    }
}
