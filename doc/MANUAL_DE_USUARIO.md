# MANUAL DE USUARIO TÉCNICO Y GUÍA DE OPERACIÓN PASO A PASO

**Sistema de Gestión Académica y Planificación de Rutas**  
**Proyecto Final – Programación III (Estructuras de Datos)**  
**Docente:** Ing. Jhon Haide Cano Beltran MSc.  

---

## Tabla de Contenido
1. [Instrucciones de Compilación y Ejecución](#1-instrucciones-de-compilación-y-ejecución)
2. [Estructura del Menú y Arquitectura de Ejecución](#2-estructura-del-menú-y-arquitectura-de-ejecución)
3. [Manual Detallado Paso a Paso: Opciones 1 a 22](#3-manual-detallado-paso-a-paso-opciones-1-a-22)
   - [Módulo 1: Gestión de Estudiantes (Opciones 1 - 4)](#módulo-1-gestión-de-estudiantes-opciones-1---4)
   - [Módulo 2: Gestión de Materias (Opciones 5 - 10)](#módulo-2-gestión-de-materias-opciones-5---10)
   - [Módulo 3: Gestión de Horarios con Matriz (Opciones 11 - 13)](#módulo-3-gestión-de-horarios-con-matriz-opciones-11---13)
   - [Módulo 4: Rutas entre Edificios con Dijkstra (Opciones 14 - 15)](#módulo-4-rutas-entre-edificios-con-dijkstra-opciones-14---15)
   - [Módulo 5: Reportes Académicos y Navegador con Pilas (Opciones 16 - 18)](#módulo-5-reportes-académicos-y-navegador-con-pilas-opciones-16---18)
   - [Módulo 6: Sistema Deshacer / Rehacer con Pilas (Opciones 19 - 20)](#módulo-6-sistema-deshacer--rehacer-con-pilas-opciones-19---20)
   - [Módulo 7: Procesamiento por Lotes (Batch) con Colas (Opción 21)](#módulo-7-procesamiento-por-lotes-batch-con-colas-opción-21)
   - [Módulo 8: Salida del Sistema (Opción 22)](#módulo-8-salida-del-sistema-opción-22)
4. [Resumen de Excepciones del Sistema](#4-resumen-de-excepciones-del-sistema)
5. [Guion de Grabación del Video de Sustentación (10 Minutos)](#5-guion-de-grabación-del-video-de-sustentación-10-minutos)

---

## 1. Instrucciones de Compilación y Ejecución

El proyecto está construido en **Java estándar (Java 17 o superior)** sin frameworks ni dependencias externas de terceros:

### Compilación por Consola (PowerShell / CMD)
```powershell
javac --release 17 -encoding UTF-8 -d bin src/app/*.java src/estructuras/*.java src/excepciones/*.java src/modelo/*.java src/servicio/*.java src/util/*.java
```

### Ejecución de la Plataforma Interactiva
```powershell
java -cp bin app.Main
```

### Ejecución en IDEs
Abra la carpeta raíz en **Visual Studio Code**, **IntelliJ IDEA**, **Apache NetBeans** o **Eclipse**. La clase con el método `public static void main(String[] args)` reside en:
`src/app/Main.java`.

---

## 2. Estructura del Menú y Arquitectura de Ejecución

Cada opción seleccionada en el menú principal de [Main.java](file:///c:/Users/mov06/Documents/Uniajc/4%20Semestre/Programación%20III/Parcial%203/src/app/Main.java) desencadena un flujo estricto de 3 etapas:
1. **Capa de Interfaz / Vista (`app.Main` + `util.ValidadorEntrada`):** Captura y valida tipos de datos por teclado para evitar caídas imprevistas (*InputMismatchException*).
2. **Capa de Negocio / Fachada (`servicio.SistemaAcademico`):** Ejecuta reglas de negocio, valida restricciones de dominio, registra operaciones reversibles y orquesta las estructuras.
3. **Capa de Estructuras y Modelo (`estructuras` y `modelo`):** Opera sobre arreglos nativos (`boolean[7][24]`, `int[N][N]`, `Double[10][20]`, `Facultad[5]`) y colecciones (`HashMap`, `TreeMap`, `Queue`, `Stack`, `ArrayList`).

---

## 3. Manual Detallado Paso a Paso: Opciones 1 a 22

---

### MÓDULO 1: GESTIÓN DE ESTUDIANTES (Opciones 1 - 4)

#### Opción 1: Registrar estudiante
* **¿Qué hace?**  
  Solicita los datos personales y académicos de un estudiante, valida su unicidad e inicializa su ficha académica en el sistema con su matriz de notas vacía y listas de seguimiento.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.registrarEstudiante()`
  - Lógica: `servicio.SistemaAcademico.registrarEstudiante(id, nombre, email, semestre)`
  - Modelo: Constructor `modelo.Estudiante(id, nombre, email, semestre)`
  - Estructura: Almacena en `Map<String, Estudiante> estudiantes` mediante `HashMap.put(id, nuevo)`.
* **Condiciones para que se cumpla:**  
  1. `ID`: Cadena no vacía. **Condición estricta:** No debe existir previamente en el `HashMap` (`!estudiantes.containsKey(id)`). Si ya existe, lanza `IllegalArgumentException`.
  2. `Nombre` y `Email`: Cadenas de texto no vacías.
  3. `Semestre`: Entero validado entre 1 y 10.
* **Ejemplo de ejecución:**
  ```text
  --- REGISTRO DE ESTUDIANTE ---
  ID: 2024010
  Nombre: Carlos Andres Mendoza
  Email: carlos.mendoza@universidad.edu
  Semestre actual: 2

  Estudiante registrado exitosamente.
  ```

---

#### Opción 2: Buscar estudiante por ID
* **¿Qué hace?**  
  Localiza al estudiante de forma inmediata en tiempo constante $O(1)$ y presenta su información general y estado académico.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.buscarEstudiantePorId()`
  - Lógica: `servicio.SistemaAcademico.buscarEstudiante(id)`
  - Estructura: `estudiantes.get(id)` sobre `HashMap<String, Estudiante>`
  - Polimorfismo: Invoca a `estudiante.mostrarInformacion()` sobrescrito en `modelo.Estudiante`.
* **Condiciones para que se cumpla:**  
  1. El `ID` ingresado debe existir en el `HashMap`.
  2. Si el ID no existe (`est == null`), se lanza y captura:  
     `EstudianteNoEncontradoException: No existe estudiante con ID: <id>`.
* **Ejemplo de ejecución (Éxito):**
  ```text
  --- BUSCAR ESTUDIANTE ---
  ID: 2024001

  Resultado encontrado:
  ID: 2024001
  Nombre: Ana Maria Gomez
  Email: ana.gomez@universidad.edu
  Semestre: 3
  Promedio acumulado: 3.55
  ```
* **Ejemplo de ejecución (Fallo controlado):**
  ```text
  --- BUSCAR ESTUDIANTE ---
  ID: 2024999

  Error: EstudianteNoEncontradoException - No existe estudiante con ID: 2024999
  ```

---

#### Opción 3: Listar todos los estudiantes
* **¿Qué hace?**  
  Itera sobre los estudiantes registrados, ordenándolos por ID y mostrándolos en una tabla formateada con su promedio acumulado.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.listarTodosLosEstudiantes()`
  - Lógica: `servicio.SistemaAcademico.listarEstudiantes()`
  - Estructura: `new ArrayList<>(estudiantes.values())` con ordenamiento `lista.sort(Comparator.comparing(Estudiante::getId))`.
* **Condiciones para que se cumpla:**  
  1. No requiere parámetros de entrada.
  2. Si la colección está vacía, notifica al usuario.
  3. Si existen registros, calcula dinámicamente `e.calcularPromedioAcumulado()` desde el arreglo `Double[10][20]`.

---

#### Opción 4: Eliminar estudiante (Baja definitiva)
* **¿Qué hace?**  
  Da de baja a un estudiante del sistema universitario, retirándolo del `HashMap` y guardando su estado completo en la pila de deshacer para permitir recuperación.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.eliminarEstudiante()`
  - Lógica: `servicio.SistemaAcademico.eliminarEstudiante(id)`
  - Estructura: `estudiantes.remove(id)`
  - Pila Undo: `historialOperaciones.registrarOperacion(new Operacion.EliminarEstudiante(estudiantes, est))`
* **Condiciones para que se cumpla:**  
  1. El estudiante debe existir en el `HashMap` (si no, lanza `EstudianteNoEncontradoException`).
  2. El objeto eliminado se retiene en la pila `pilaDeshacer` con todas sus notas y materias para restauración reversible.

---

### MÓDULO 2: GESTIÓN DE MATERIAS (Opciones 5 - 10)

#### Opción 5: Crear materia
* **¿Qué hace?**  
  Registra una nueva asignatura en el catálogo académico con su cupo límite de aulas y créditos.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.crearMateria()`
  - Lógica: `servicio.SistemaAcademico.crearMateria(codigo, nombre, cuposMaximos, creditos)`
  - Modelo: Constructor `modelo.Materia(codigo, nombre, cuposMaximos, creditos)`
  - Estructura: `materias.put(codigoNormalizado, nuevaMateria)` en `Map<String, Materia>`.
* **Condiciones para que se cumpla:**  
  1. `Código`: Cadena sin espacios, normalizada a mayúsculas (`trim().toUpperCase()`).
  2. **Unicidad:** No debe existir otra materia con el mismo código. Si existe, lanza `IllegalArgumentException`.
  3. `Cupos máximos`: Entero positivo $\ge 1$.
  4. `Créditos`: Entero entre 1 y 10.

---

#### Opción 6: Agregar pre-requisito
* **¿Qué hace?**  
  Establece una dependencia académica obligatoria: la materia objetivo no podrá ser inscrita a menos que la materia pre-requisito haya sido aprobada previamente.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.agregarPrerequisito()`
  - Lógica: `servicio.SistemaAcademico.agregarPrerequisito(codigoMateria, codigoPrerequisito)`
  - Modelo: `materia.agregarPrerequisito(codigoPrerequisito)`
  - Estructura: `List<String> prerequisitos` (`ArrayList`).
* **Condiciones para que se cumpla:**  
  1. Ambas asignaturas deben existir previamente en el catálogo de materias.
  2. Una materia no puede ser pre-requisito de sí misma (`!codigo.equalsIgnoreCase(this.codigo)`).
  3. No debe estar repetido en la lista de pre-requisitos.

---

#### Opción 7: Mostrar pre-requisitos
* **¿Qué hace?**  
  Consulta y despliega la lista enlazada/List de asignaturas requeridas para cursar una materia específica.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.mostrarPrerequisitos()`
  - Lógica: `servicio.SistemaAcademico.obtenerPrerequisitos(codigoMateria)`
  - Estructura: Retorna `materia.getPrerequisitos()` (`List<String>`).
* **Condiciones para que se cumpla:**  
  1. La materia consultada debe existir en el catálogo.

---

#### Opción 8: Inscribir estudiante en materia
* **¿Qué hace?**  
  Evalúa el cumplimiento de pre-requisitos de un estudiante y gestiona su cupo: si hay cupos lo inscribe; si la materia está llena, lo coloca en la **Cola de Espera (FIFO)**.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.inscribirEstudiante()`
  - Lógica: `servicio.SistemaAcademico.inscribirEstudianteEnMateria(idEstudiante, codigoMateria)`
  - Modelo: `materia.inscribirEstudiante(estudiante)`
  - Estructuras involucradas:
    * `List<String> historialMaterias` del estudiante para validar pre-requisitos.
    * `List<Estudiante> inscritos` de la materia.
    * `Queue<Estudiante> colaEspera` (`ArrayDeque`/`LinkedList`) para sobrecupo.
    * `pilaDeshacer` registra `Operacion.Inscripcion`.
* **Condiciones para que se cumpla:**  
  1. El estudiante y la materia deben existir.
  2. El estudiante no debe estar previamente inscrito.
  3. **Validación de pre-requisitos:** El estudiante debe tener aprobada ($\ge 3.0$) en su historial cada materia pre-requisito. Si falta alguna, se interrumpe y lanza:  
     `PreRequisitoNoAprobadoException: El estudiante no cumple el pre-requisito: <codigo>`.
  4. **Si hay cupo:** Se agrega a `inscritos`, se descuenta un cupo disponible y se retorna estado `EXITOSA`.
  5. **Si no hay cupo:** Se encola en `colaEspera` (FIFO) y retorna estado `EN_COLA_DE_ESPERA`.

---

#### Opción 9: Cancelar inscripción
* **¿Qué hace?**  
  Cancela la matrícula de un estudiante en una asignatura. Si la materia estaba llena y hay estudiantes esperando en la cola, **promueve automáticamente al primer estudiante de la cola** asignándole el cupo liberado.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.cancelarInscripcion()`
  - Lógica: `servicio.SistemaAcademico.cancelarInscripcionMateria(idEstudiante, codigoMateria)`
  - Modelo: `materia.cancelarInscripcion(idEstudiante)`
  - Estructuras: Retira de `inscritos`, llama a `colaEspera.poll()` y promueve al estudiante a `inscritos`. Registra `Operacion.Cancelacion` en `pilaDeshacer`.
* **Condiciones para que se cumpla:**  
  1. El estudiante debe encontrarse inscrito o en cola en la asignatura indicada.
  2. La promoción de cola es automática e inmediata respetando el orden de llegada (FIFO).
* **Ejemplo de ejecución:**
  ```text
  --- CANCELAR INSCRIPCION ---
  ID del estudiante: 2024002
  Código de la materia: CALC101

  Cancelacion exitosa. Cupo liberado.
  Asignando cupo a Laura Diaz (primer estudiante en cola)
  ```

---

#### Opción 10: Mostrar cola de espera
* **¿Qué hace?**  
  Inspecciona la estructura tipo Cola (`Queue`) de la materia seleccionada y despliega los estudiantes en espera con su número de turno ordinal.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.mostrarColaEspera()`
  - Lógica: `servicio.SistemaAcademico.obtenerColaEspera(codigoMateria)`
  - Estructura: Recorre `Queue<Estudiante>` de `materia.getColaEspera()`.
* **Condiciones para que se cumpla:**  
  1. La materia debe existir.
  2. Si la cola está vacía (`cola.isEmpty()`), lanza y captura:  
     `ColaDeEsperaVaciaException: La cola de espera de la materia se encuentra vacía.`
* **Ejemplo de ejecución:**
  ```text
  --- COLA DE ESPERA DE CALC101 ---
  Posicion 1: Laura Diaz (ID: 2024005)
  Posicion 2: Pablo Ortiz (ID: 2024006)
  Total en espera: 2
  ```

---

### MÓDULO 3: GESTIÓN DE HORARIOS CON MATRIZ (Opciones 11 - 13)

#### Opción 11: Reservar horario en aula
* **¿Qué hace?**  
  Verifica la disponibilidad bloque a bloque sobre la matriz nativa `boolean[7][24]` del aula seleccionada y bloquea las horas solicitadas.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.reservarHorarioEnAula()`
  - Lógica: `servicio.SistemaAcademico.reservarHorarioAula(codigoAula, dia, hora, duracion)`
  - Modelo: `aula.reservar(dia, hora, duracion)`
  - Estructura: Matriz `boolean[7][24]` donde `0=Domingo..6=Sábado`, `0..23` horas.
* **Condiciones para que se cumpla:**  
  1. El aula debe existir en el `TreeMap<String, Aula>`.
  2. `Día`: Entre 0 y 6. `Hora`: Entre 0 y 23.
  3. `hora + duracion <= 24` (no desbordar el día).
  4. **Regla de No Conflicto:** Ninguna de las horas en el rango puede estar previamente reservada (`horario[dia][h] == true`). Si alguna celda está ocupada, se cancela la operación y lanza:  
     `HorarioConflictivoException: <Día> <Hora>:00 ya esta reservado`.
* **Ejemplo de ejecución (Reserva Exitosa):**
  ```text
  --- RESERVAR HORARIO EN AULA ---
  Aula: 101
  Dia: 1
  Hora (0 a 23): 8
  Duracion (horas): 2

  Verificando disponibilidad...
  Lunes 8:00 -> LIBRE
  Lunes 9:00 -> LIBRE

  Reserva exitosa.
  ```

---

#### Opción 12: Liberar horario en aula
* **¿Qué hace?**  
  Restaura a `false` (LIBRE) las celdas correspondientes al bloque de horas indicado en la matriz `boolean[7][24]`.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.liberarHorario()`
  - Lógica: `servicio.SistemaAcademico.liberarHorarioAula(codigoAula, dia, hora, duracion)`
  - Modelo: `aula.liberar(dia, hora, duracion)`
  - Pila Undo: Registra `Operacion.Horario` con flag `eraReserva = false`.
* **Condiciones para que se cumpla:**  
  1. Aula existente y coordenadas de día, hora y duración dentro del rango válido.

---

#### Opción 13: Consultar disponibilidad puntual
* **¿Qué hace?**  
  Examina una celda específica de la matriz `boolean[7][24]` en tiempo inmediato $O(1)$ informando si se encuentra `LIBRE` u `OCUPADO`.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.consultarDisponibilidad()`
  - Lógica: `servicio.SistemaAcademico.consultarDisponibilidadAula(codigoAula, dia, hora)`
  - Modelo: `aula.consultarDisponibilidad(dia, hora)` $\rightarrow$ retorna `!horario[dia][hora]`.
* **Condiciones para que se cumpla:**  
  1. Coordenadas de día (0 a 6) y hora (0 a 23) válidas.

---

### MÓDULO 4: RUTAS ENTRE EDIFICIOS CON DIJKSTRA (Opciones 14 - 15)

#### Opción 14: Agregar conexión entre edificios
* **¿Qué hace?**  
  Modifica la matriz de adyacencia nativa `int[N][N]` del campus para enlazar dos edificios mediante una arista bidireccional no dirigida con su distancia métrica.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.agregarConexionEntreEdificios()`
  - Lógica: `servicio.SistemaAcademico.agregarConexionCampus(origen, destino, distanciaMetros)`
  - Estructura: `grafoCampus.agregarConexion(origen, destino, distanciaMetros)`  
    Ejecuta: `matrizAdyacencia[origen][destino] = distancia; matrizAdyacencia[destino][origen] = distancia;`
* **Condiciones para que se cumpla:**  
  1. Índices de origen y destino válidos dentro del número de edificios ($0$ a $N-1$).
  2. `origen != destino` (no lazos reflexivos).
  3. `distancia > 0` (peso estrictamente positivo en metros).

---

#### Opción 15: Calcular ruta más corta (Dijkstra)
* **¿Qué hace?**  
  Ejecuta el **algoritmo de Dijkstra** para encontrar la distancia mínima entre dos edificios, reconstruyendo la ruta paso a paso mediante el vector de predecesores e imprimiendo la distancia total en metros.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.calcularRutaMasCorta()`
  - Lógica: `servicio.SistemaAcademico.calcularRutaMasCorta(origen, destino)`
  - Estructura/Algoritmo: `estructuras.GrafoCampus.calcularRutaMasCorta(origen, destino)`
* **Condiciones para que se cumpla:**  
  1. Índices dentro del rango $0 \le \text{nodo} < N$.
  2. Debe existir un camino conexo (distancia resultante $< \infty$).
* **Ejemplo de ejecución (Ejemplo 5 oficial):**
  ```text
  --- CALCULAR RUTA MAS CORTA ---
  Edificios registrados:
    0: Ingenieria
    1: Biblioteca
    2: Cafeteria
    3: Rectoria
    4: Laboratorios

  Origen (número): 0
  Destino (número): 3

  Origen: Ingenieria
  Destino: Rectoria

  --- RESULTADO ---
  Ruta mas corta:
    Ingenieria -> Cafeteria (150m) -> Rectoria (180m)
  Distancia TOTAL: 330 metros
  ```

---

### MÓDULO 5: REPORTES ACADÉMICOS Y NAVEGADOR CON PILAS (Opciones 16 - 18)

#### Opción 16: Registrar nota de estudiante
* **¿Qué hace?**  
  Inserta una calificación en el arreglo estático bidimensional `Double[10][20]` del estudiante en la fila correspondiente al semestre y columna de la materia. Si la calificación es $\ge 3.0$, la añade al historial de aprobadas.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.registrarNota()`
  - Lógica: `servicio.SistemaAcademico.registrarNotaEstudiante(idEstudiante, semestre, codigoMateria, nota)`
  - Modelo: `estudiante.registrarNota(semestre, codigoMateria, nota)`
  - Estructura: Asignación `notas[semestre - 1][columna] = nota;` en `Double[10][20]`.
  - Pila Undo: Registra `Operacion.Nota` en `pilaDeshacer`.
* **Condiciones para que se cumpla:**  
  1. El estudiante y la materia deben existir.
  2. `Semestre`: Entre 1 y 10.
  3. `Nota`: Decimal entre 0.0 y 5.0.
  4. `conteoMateriasSemestre[semestre-1] < 20` (no exceder el tope de 20 materias por periodo).

---

#### Opción 17: Ver reporte académico completo
* **¿Qué hace?**  
  Calcula el promedio de cada semestre cursado, el promedio acumulado ponderado, el conteo de materias aprobadas y reprobadas, y guarda el reporte en el **Navegador de Reportes con Pilas**.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.verReporteAcademico()`
  - Lógica: `servicio.SistemaAcademico.generarReporteAcademico(idEstudiante)`
  - Modelo: `estudiante.generarReporteAcademico()` (itera sobre `Double[10][20]`).
  - Estructura: Almacena el texto en `navegadorReportes.registrarReporte(reporte)`.
* **Condiciones para que se cumpla:**  
  1. El estudiante debe existir en el `HashMap`.
* **Ejemplo de ejecución (Ejemplo 6 oficial):**
  ```text
  --- REPORTE ACADEMICO ---
  Estudiante: Ana Maria Gomez (ID: 2024001)

  Semestre 1:
    CALC101: 4.5
    FIS101: 2.8
    PROG101: 5.0
    Promedio: 4.10

  Semestre 2:
    CALC102: 3.5
    FIS102: 3.0
    MATDIS: 2.5
    Promedio: 3.00

  === RESUMEN ===
  Promedio acumulado: 3.55
  Materias aprobadas: 4
  Materias reprobadas: 2
  ```

---

#### Opción 18: Navegador de reportes (Atrás / Adelante con Pilas)
* **¿Qué hace?**  
  Permite navegar cronológicamente entre los reportes previamente generados mediante una arquitectura de doble pila (LIFO): `pilaAtras` y `pilaAdelante`, simulando las flechas de un navegador web.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.navegadorDeReportes()`
  - Estructura: `estructuras.NavegadorReportes`
    * Ver actual: `nav.getReporteActual()`
    * Retroceder: `nav.retroceder()` $\rightarrow$ hace `pilaAdelante.push(reporteActual)` y `reporteActual = pilaAtras.pop()`.
    * Avanzar: `nav.avanzar()` $\rightarrow$ hace `pilaAtras.push(reporteActual)` y `reporteActual = pilaAdelante.pop()`.
* **Condiciones para que se cumpla:**  
  1. Para retroceder, `!pilaAtras.isEmpty()`. Si está vacía, indica que no hay reportes anteriores.
  2. Para avanzar, `!pilaAdelante.isEmpty()`. Si está vacía, indica que no hay reportes siguientes.

---

### MÓDULO 6: SISTEMA DESHACER / REHACER CON PILAS (Opciones 19 - 20)

#### Opción 19: Deshacer última operación (Undo)
* **¿Qué hace?**  
  Desapila la última acción registrada en `pilaDeshacer`, invoca polimórficamente su método `deshacer()`, restaura el estado anterior y traslada el objeto a la pila de rehacer.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.deshacerUltimaOperacion()`
  - Lógica: `servicio.SistemaAcademico.deshacerOperacion()`
  - Estructura: `historialOperaciones.deshacer()` sobre `Stack<Operacion> pilaDeshacer`.
* **Condiciones para que se cumpla:**  
  1. La pila `pilaDeshacer` no debe estar vacía (`!pilaDeshacer.isEmpty()`).
  2. Si está vacía, lanza y captura:  
     `PilaDeshacerVaciaException: No hay operaciones para deshacer`.
* **Operaciones reversibles soportadas:**
  - Inscripción de estudiante $\rightarrow$ lo desinscribe o lo retira de la cola.
  - Cancelación de inscripción $\rightarrow$ restaura la inscripción y devuelve al promovido a la cola.
  - Cambio de horario de aula $\rightarrow$ invierte el estado de reserva/liberación.
  - Registro de nota $\rightarrow$ retira la calificación de `Double[10][20]` y del historial.
  - Eliminación de estudiante $\rightarrow$ reinserta al estudiante con todos sus atributos en el `HashMap`.
* **Ejemplo de ejecución:**
  ```text
  --- DESHACER ---
  Operacion deshecha: Ana Maria Gomez ya NO esta inscrita en CALC102
  ```

---

#### Opción 20: Rehacer última operación (Redo)
* **¿Qué hace?**  
  Desapila la operación desde `pilaRehacer`, invoca polimórficamente su método `rehacer()`, reaplica el cambio y regresa la operación a la `pilaDeshacer`.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.rehacerUltimaOperacion()`
  - Lógica: `servicio.SistemaAcademico.rehacerOperacion()`
  - Estructura: `historialOperaciones.rehacer()` sobre `Stack<Operacion> pilaRehacer`.
* **Condiciones para que se cumpla:**  
  1. La pila `pilaRehacer` no debe estar vacía (`!pilaRehacer.isEmpty()`).
  2. Si no hay acciones para rehacer, lanza y captura:  
     `PilaDeshacerVaciaException: No hay operaciones para rehacer`.
* **Ejemplo de ejecución:**
  ```text
  --- REHACER ---
  Operacion rehecha: Ana Maria Gomez inscrita en CALC102
  ```

---

### MÓDULO 7: PROCESAMIENTO POR LOTES CON COLAS (Opción 21)

#### Opción 21: Procesar archivo CSV (Batch Processing)
* **¿Qué hace?**  
  Carga masivamente peticiones desde el archivo [inscripciones.csv](file:///c:/Users/mov06/Documents/Uniajc/4%20Semestre/Programación%20III/Parcial%203/inscripciones.csv), las encola todas en una estructura tipo **Cola (Queue)** y las atiende secuencialmente en orden estricto de llegada (FIFO), reportando solicitudes exitosas y fallidas con su motivo.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.procesarArchivoCSV()`
  - Utilidad: `util.LectorCSV.leerSolicitudesCSV(rutaArchivo)`
  - Lógica: `servicio.SistemaAcademico.procesarArchivoBatch(rutaArchivo)`
  - Estructura: Encolamiento y vaciado con `Queue<SolicitudInscripcion> colaBatch` (`LinkedList`/`ArrayDeque`).
* **Condiciones para que se cumpla:**  
  1. El archivo `inscripciones.csv` debe existir físicamente y tener permisos de lectura.
  2. Cada línea debe cumplir el formato `idEstudiante,codigoMateria`. Si el archivo tiene errores estructurales graves, lanza `ArchivoInvalidoException`.
  3. Criterios de evaluación de cada solicitud encolada:
     * Si el estudiante no existe en el `HashMap` $\rightarrow$ Fallida (`Estudiante no existe`).
     * Si la materia no existe en el catálogo $\rightarrow$ Fallida (`Materia no existe`).
     * Si el estudiante no ha aprobado los pre-requisitos $\rightarrow$ Fallida (`Pre-requisitos no cumplidos`).
     * Si la materia no tiene cupos $\rightarrow$ Fallida (`Cupo lleno (En cola de espera)`).
     * Si todo es conforme $\rightarrow$ Exitosa.
* **Ejemplo de ejecución (Ejemplo 7 oficial):**
  ```text
  --- PROCESAMIENTO MASIVO (BATCH) ---
  Ruta del archivo CSV [Por defecto: inscripciones.csv]: 
  Leyendo archivo y encolando solicitudes...
  Se encolaron 100 solicitudes.

  Procesando cola...
  [1/100] 2024001 → CALC102 → Exitosa
  [2/100] 2024002 → FIS102 → Fallida (Pre-requisitos no cumplidos)
  [3/100] 2024002 → MATDIS → Exitosa
  ...
  === RESUMEN ===
  Exitosas: 78
  Fallidas: 22
  ```

---

### MÓDULO 8: SALIDA DEL SISTEMA (Opción 22)

#### Opción 22: Salir
* **¿Qué hace?**  
  Termina de forma limpia y ordenada el bucle interactivo de la consola y finaliza la ejecución de la JVM.
* **Código/Método que genera la función:**  
  - Vista: `app.Main.main()` $\rightarrow$ asigna `salir = true` y rompe el bucle `while (!salir)`.
* **Condiciones para que se cumpla:**  
  - No requiere validaciones adicionales; finaliza el programa sin errores en código de salida 0.

---

## 4. Resumen de Excepciones del Sistema

Todas las excepciones requeridas por el enunciado heredan de `java.lang.Exception` y residen en el paquete `excepciones`:

| Excepción | ¿Cuándo se lanza? | Clase que la arroja |
| :--- | :--- | :--- |
| `PreRequisitoNoAprobadoException` | Un estudiante intenta inscribir una materia sin haber aprobado sus requisitos previos ($\ge 3.0$). | `Materia.java` |
| `HorarioConflictivoException` | Se intenta reservar un bloque de horas en un aula donde al menos una celda ya está en `true` (ocupada). | `Aula.java` |
| `CupoLlenoException` | Una materia supera su capacidad máxima admisible de estudiantes. | `Materia.java` |
| `EstudianteNoEncontradoException` | Se busca o elimina un estudiante cuyo ID no existe en el `HashMap`. | `SistemaAcademico.java` |
| `ColaDeEsperaVaciaException` | Se solicita ver la cola de espera de una materia que no tiene ningún estudiante en espera. | `SistemaAcademico.java` |
| `PilaDeshacerVaciaException` | Se intenta deshacer o rehacer pero la pila respectiva (`pilaDeshacer` o `pilaRehacer`) no tiene elementos. | `HistorialOperaciones.java` |
| `ArchivoInvalidoException` | El archivo CSV para el batch no existe, no puede leerse o tiene líneas malformadas. | `LectorCSV.java` |

---

## 5. Guion de Grabación del Video de Sustentación (10 Minutos)

Para asegurar la máxima nota en el criterio de presentación (pág. 15):

* **Minuto 0:00 - 1:30 | Presentación de Arquitectura:**  
  Mostrar la estructura de paquetes (`modelo`, `estructuras`, `excepciones`, `util`, `servicio`, `app`) y la herencia de `Persona` $\rightarrow$ `Estudiante` y `Profesor`.
* **Minuto 1:30 - 3:30 | Demostración de Cola de Espera (Ejemplo 2):**  
  Inscribir en la opción 8 a 5 estudiantes en `CALC101` (cupo 3). Mostrar cómo los últimos 2 van a la cola (Opción 10). Cancelar a uno con cupo (Opción 9) y mostrar la auto-asignación a Laura Díaz.
* **Minuto 3:30 - 5:30 | Demostración de Pilas Undo/Redo (Ejemplo 3):**  
  Inscribir a Ana en `CALC102`. Ejecutar Opción 19 (Deshacer) y mostrar que ya no está inscrita. Ejecutar Opción 20 (Rehacer) y comprobar que vuelve a inscribirse. Intentar rehacer nuevamente para mostrar `PilaDeshacerVaciaException`.
* **Minuto 5:30 - 7:00 | Demostración de Matriz de Aula y Grafo Dijkstra (Ejemplos 4 y 5):**  
  Reservar en Aula 101 (Lunes 8:00, 2 horas). Intentar reservar con conflicto (Lunes 9:00) para mostrar `HorarioConflictivoException`. Luego, calcular ruta más corta entre Ingeniería (0) y Rectoría (3) obteniendo los 330 metros.
* **Minuto 7:00 - 8:30 | Reportes y Navegador con Pilas (Ejemplo 6 y Opción 18):**  
  Generar reporte de `2024001` (Ana María Gómez). Abrir el navegador de reportes (Opción 18) y desplazarse con Atrás y Adelante entre vistas.
* **Minuto 8:30 - 10:00 | Procesamiento Masivo CSV (Bonus Batch - Opción 21) y Cierre:**  
  Ejecutar la opción 21 con `inscripciones.csv`, mostrar el encolamiento de las 100 peticiones y el resumen de aprobadas/rechazadas.
