# JUSTIFICACIÓN TÉCNICA DE DECISIONES DE ESTRUCTURAS DE DATOS

**Proyecto Final – Programación III (Estructuras de Datos)**  
**Docente:** Ing. Jhon Haide Cano Beltran MSc.  
**Asignatura:** Programación III  
**Estudiante:** Ingeniería de Sistemas  
**Fecha:** Noviembre de 2026  

---

## 1. Resumen Ejecutivo y Matriz de Estructuras

En cumplimiento estricto con los requerimientos estipulados en la guía académica del proyecto (Cuadros 1 y 3), a continuación se presenta la matriz de estructuras de datos seleccionadas, su propósito funcional y su justificación técnica:

| Requerimiento de la Guía | Estructura Implementada | Tipo de Implementación | Justificación Técnica de la Elección |
| :--- | :--- | :--- | :--- |
| **Disponibilidad horaria de aulas** | `boolean[7][24]` | Arreglo bidimensional nativo | Acceso directo $O(1)$ por coordenadas `[día][hora]`. Mínima huella de memoria (1 byte o bit por celda). Modela naturalmente la matriz horaria semanal (7 días x 24 horas). |
| **Distancias entre campus / edificios** | `int[N][N]` | Arreglo bidimensional nativo | Matriz de adyacencia de grafo no dirigido. Permite consultas de adyacencia en $O(1)$ requeridas por el algoritmo de Dijkstra para relajar aristas rápidamente. |
| **Notas por semestre del estudiante** | `Double[10][20]` | Arreglo bidimensional nativo | Cumple el límite fijo de 10 semestres y 20 materias por semestre. Permite representar celdas vacías (`null`) y calcular promedios sin reasignaciones dinámicas de memoria. |
| **Lista fija de facultades** | `Facultad[5]` | Arreglo unidimensional nativo | Tamaño invariante conocido a priori (5 facultades). Evita sobrecostos dinámicos. |
| **Pre-requisitos de materias** | `List<String>` (`ArrayList`) | Colección Java (`List` / `ArrayList`) | Alta localidad espacial de caché, búsquedas secuenciales ultra rápidas para listas cortas, menor consumo de memoria que nodos enlazados dispersos. |
| **Historial de materias cursadas** | `List<String>` (`ArrayList`) | Colección Java (`List` / `ArrayList`) | Iteración eficiente, orden cronológico de aprobación preservado, facilidad de serialización y verificación de pre-requisitos. |
| **Cola de espera de materias llenas** | `Queue<Estudiante>` (`ArrayDeque`) | Colección Java (`Queue`) | Estructura FIFO pura (First-In, First-Out). Garantiza equidad en la asignación de cupos liberados en tiempo $O(1)$. |
| **Procesamiento batch de solicitudes** | `Queue<SolicitudInscripcion>` | Colección Java (`Queue`) | Garantiza el procesamiento secuencial ordenado de peticiones de archivo CSV con bajo overhead de memoria. |
| **Pilas Deshacer / Rehacer** | `Stack<Operacion>` | Colección Java (`Stack` / LIFO) | Modelo LIFO (Last-In, First-Out) idóneo para revertir estados en orden inverso a su ejecución (patrón Command / Memento). |
| **Navegación entre reportes** | `Stack<String>` | Colección Java (`Stack` / LIFO) | Emula la pila del historial del navegador (pila "Atrás" y pila "Adelante") con operaciones $push$ y $pop$ en $O(1)$. |
| **Índice rápido de estudiantes** | `HashMap<String, Estudiante>` | Java Collections (`HashMap`) | Búsqueda por ID en tiempo constante promedio $O(1)$ mediante hashing con resolución de colisiones y balanceo a Red-Black Tree. |
| **Orden de aulas por código/capacidad**| `TreeMap<String, Aula>` | Java Collections (`TreeMap`) | Mantiene las aulas ordenadas lexicográficamente en todo momento en $O(\log N)$ mediante un Árbol Rojo-Negro autobalanceado. |

---

## 2. Justificación Profunda: Decisión por `List` y `ArrayList` vs. Listas Enlazadas Propias

La guía del curso otorga al estudiante la libertad de elegir entre implementar estructuras manualmente o emplear las clases provistas por el ecosistema de Java, siempre y cuando dicha decisión esté **técnicamente fundamentada**.

Como estudiante de ingeniería con criterios avanzados de desarrollo y optimización de software, he optado por utilizar **la abstracción `List` fundamentada en `ArrayList`** (así como `ArrayDeque` para colas y deques) para la gestión de pre-requisitos, historial académico y listas de espera, sustentado en los siguientes pilares de la ciencia de la computación:

### 2.1. Localidad Espacial de Referencia y Caché de la CPU (Spatial Locality)

* **En listas enlazadas manuales (`LinkedList` o nodos personalizados `Node<T>`):** Cada elemento reside en una instancia aislada de nodo alojada en posiciones no contiguas del Heap de la JVM. Al recorrer la lista para validar pre-requisitos, la CPU debe seguir punteros dispersos (*pointer chasing*). Esto provoca constantes fallos de caché (*cache misses*) tanto en la caché L1 como en la L2/L3, degradando el rendimiento real en procesadores modernos.
* **En `ArrayList`:** Los elementos se almacenan en un arreglo físico contiguo. Cuando la CPU lee un elemento, la línea de caché (típicamente de 64 bytes) precarga automáticamente los siguientes elementos contiguos. Como resultado, la iteración a través de un `ArrayList` es de **3 a 5 veces más rápida** en microarquitecturas contemporáneas, a pesar de tener la misma cota teórica asintótica $O(n)$ que una lista enlazada.

### 2.2. Huella de Memoria (Memory Overhead) y Presión sobre el Garbage Collector

En sistemas que manejan miles de transacciones o registros:
* Cada objeto `Nodo` en una lista enlazada en una máquina virtual Java de 64 bits con compresión de punteros requiere:
  * Encabezado del objeto (*Object Header*): 12 a 16 bytes.
  * Puntero al elemento (`item`): 4 a 8 bytes.
  * Puntero al siguiente nodo (`next`): 4 a 8 bytes.
  * Puntero al nodo previo (`prev`) si es doblemente enlazada: 4 a 8 bytes.
  * *Alineación de 8 bytes de la JVM*: Entre 24 y 32 bytes de sobrecosto por cada elemento almacenado.
* En `ArrayList`:
  * El sobrecosto es simplemente un arreglo de referencias (`Object[]`).
  * No se instancian nodos intermedios.
  * Se reduce drásticamente la fragmentación del Heap y se disminuyen las pausas por recolección de basura (*Garbage Collection pauses*).

### 2.3. Complejidad Asintótica y Casos de Uso del Dominio Universitario

1. **Lectura y Búsqueda:**  
   * Acceso por índice: `ArrayList` ofrece acceso aleatorio $O(1)$ directo por cálculo de desplazamiento en memoria base + índice $\times$ tamaño. Una lista enlazada requiere $O(n)$ para alcanzar el nodo deseado.
2. **Inserción de Pre-requisitos:**  
   * Las asignaturas universitarias poseen una cantidad reducida y acotada de pre-requisitos (raramente más de 3 o 4 por materia). Para colecciones de tamaño menor a 50 elementos, `ArrayList` supera de forma abrumadora a las listas enlazadas debido al bajo costo de inserción al final ($O(1)$ amortizado) y la ausencia de reserva dinámica individual de objetos `Node`.
3. **Principio de Segregación de Interfaces (ISP de SOLID):**  
   * Al utilizar la interfaz de alto nivel `java.util.List` como tipo de variable (`List<String> prerequisitos = new ArrayList<>();`), el código desacopla las reglas de negocio de la implementación concreta. Si en el futuro se requiriera cambiar la estrategia de almacenamiento a una lista enlazada concurrente o persistente, el resto del modelo no requeriría modificación alguna.

---

## 3. Justificación de los Arreglos y Matrices Nativas Requeridas

La guía del curso estipula de forma mandatoria que las matrices y arreglos de dimensión fija deben implementarse con arreglos nativos de Java (`boolean[][]`, `int[][]`, `Double[][]`, `Facultad[]`). Esta directriz se respetó de manera estricta por las siguientes razones:

1. **Matriz `boolean[7][24]` para Aulas:**
   * La disponibilidad semanal es inherentemente fija: 7 días (0 a 6) y 24 horas (0 a 23).
   * Un arreglo nativo `boolean[7][24]` consume exactamente $7 \times 24 = 168$ booleanos primitivos en memoria contigua. La validación de conflicto de horario o liberación se realiza en tiempo $O(1)$ exacto por celda, sin indirecciones de wrappers ni llamadas polimórficas.
2. **Matriz `int[N][N]` para Grafo de Distancias del Campus:**
   * Al tratarse de un grafo denso/completo entre edificios representativos del campus universitario (Ingeniería, Biblioteca, Cafetería, Rectoría, Laboratorios), la matriz de adyacencia ofrece un tiempo de consulta de arista $O(1)$, facilitando las iteraciones del algoritmo de Dijkstra al comparar $distancia[u] + matriz[u][v] < distancia[v]$.
3. **Matriz `Double[10][20]` para Notas Semestrales:**
   * Modela directamente la estructura de una carrera profesional estándar: máximo 10 semestres y hasta 20 asignaturas por periodo.
   * El uso del tipo envoltura `Double` (en lugar de `double` primitivo) permite almacenar celdas con valor `null`, distinguiendo de manera limpia los espacios sin asignaturas cursadas de aquellos con una nota numérica válida de 0.0.
4. **Arreglo `Facultad[5]`:**
   * Las facultades institucionales son entidades estáticas y acotadas. El arreglo nativo garantiza una lista fija e inmutable en tamaño durante la ejecución del sistema.

---

## 4. Justificación de Estructuras Avanzadas: Hash, Árboles, Pilas y Colas

### 4.1. `HashMap<String, Estudiante>` vs Búsqueda Lineal
* **Búsqueda Lineal en Lista:** Requeriría $O(n)$ comparaciones en cada consulta por ID. Con 5,000 estudiantes registrados, cada búsqueda tomaría en promedio 2,500 pasos.
* **`HashMap` de Java:** Provee operaciones `get()`, `put()` y `remove()` en tiempo constante promedio $O(1)$ gracias a la función hash `hashCode()` de `String` y al mecanismo interno de cubetas (*buckets*). A partir de Java 8, si una cubeta excede 8 colisiones, transmuta internamente a un árbol rojo-negro (*TreeNode*), garantizando el peor caso en $O(\log n)$ frente a ataques de colisión.

### 4.2. `TreeMap<String, Aula>` para Aulas Ordenadas
* La interfaz de usuario y la administración requieren consultar aulas en estricto orden alfabético o de código (ej: Aula 101, 102, 201, 202, 301).
* `TreeMap` implementa un Árbol Rojo-Negro (árbol binario de búsqueda autobalanceado), asegurando que el recorrido en orden *in-order* devuelva siempre las aulas ordenadas sin requerir pasos explícitos de ordenamiento `sort()` de costo $O(n \log n)$ antes de cada despliegue.

### 4.3. Pilas (`Stack`) para Deshacer / Rehacer y Navegación
* El comportamiento reversible de operaciones (inscripción, cancelación, reserva de aula, registro de calificación, baja de estudiante) requiere preservar el orden temporal estricto: la última acción ejecutada debe ser la primera en revertirse. La estructura tipo Pila (LIFO) es la única matemáticamente adecuada para esta semántica.
* La dualidad `pilaDeshacer` y `pilaRehacer` implementa el patrón estándar de la industria (Command / Memento), donde al ejecutar `deshacer()`, la operación se desapila y se traslada a la pila contraria para permitir su posterior re-aplicación.

### 4.4. Colas (`Queue`) para Listas de Espera y Procesamiento Batch
* Las colas modelan el principio de equidad temporal FIFO: el primer estudiante que llegó cuando la materia estaba llena tiene derecho de prioridad preferente cuando otro estudiante cancela su inscripción.
* En el procesamiento batch por CSV, las líneas del archivo se encolan para ser procesadas en el orden cronológico estricto en que fueron registradas en el archivo fuente.

---

## 5. Conclusión

La combinación de **arreglos nativos de Java** para estructuras estáticas de tamaño prefijado junto con **la potencia de la API de Colecciones (`List`, `ArrayList`, `Queue`, `Stack`, `HashMap`, `TreeMap`)** para entidades de tamaño dinámico brinda una solución limpia, elegante, altamente eficiente en memoria y CPU, y completamente alineada con las mejores prácticas de la ingeniería de software y los objetivos pedagógicos de la asignatura Programación III.

