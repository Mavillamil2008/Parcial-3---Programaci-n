# Sistema de Gestión Académica y Planificación de Rutas de Aprendizaje

**Proyecto Final – Programación III (Estructuras de Datos)**  
**Docente:** Ing. Jhon Haide Cano Beltran MSc.  
**Plataforma:** Java 17+ (Orientación a Objetos, Colecciones y Estructuras Nativas)  

---

## Descripción General

Este proyecto implementa una solución integral para la administración académica universitaria y la optimización de rutas dentro del campus. Desarrollado con una arquitectura limpia, modular y orientada a objetos, el sistema articula estructuras de datos nativas (matrices y arreglos estáticos) junto con la potencia de la API de Colecciones de Java (`List`, `ArrayList`, `Queue`, `Stack`, `HashMap`, `TreeMap`), cumpliendo rigurosamente con los 22 requerimientos funcionales y técnicos de la rúbrica oficial.


---

## Estructura del Proyecto y Paquetes

```
Parcial 3/
├── src/
│   ├── modelo/                  # Entidades del dominio y herencia obligatoria
│   │   ├── Persona.java         # Clase abstracta base (nombre, id, email)
│   │   ├── Estudiante.java      # Subclase con Double[10][20], semestre y listas de materias
│   │   ├── Profesor.java        # Subclase con departamento y salario
│   │   ├── Materia.java         # Pre-requisitos (List) y cola de espera FIFO (Queue)
│   │   ├── Aula.java            # Disponibilidad horaria con matriz boolean[7][24]
│   │   ├── Facultad.java        # Entidad para arreglo fijo Facultad[5]
│   │   └── SolicitudInscripcion.java # Modelo para procesamiento masivo CSV
│   ├── estructuras/             # Estructuras de datos especializadas
│   │   ├── GrafoCampus.java     # Matriz int[N][N] y algoritmo de Dijkstra
│   │   ├── HistorialOperaciones.java # Doble pila (Stack) para Undo/Redo
│   │   ├── Operacion.java       # Contrato e implementaciones Command/Memento reversibles
│   │   └── NavegadorReportes.java    # Doble pila (Stack) para navegación Atrás/Adelante
│   ├── excepciones/             # Las 7 excepciones personalizadas obligatorias
│   │   ├── PreRequisitoNoAprobadoException.java
│   │   ├── HorarioConflictivoException.java
│   │   ├── CupoLlenoException.java
│   │   ├── EstudianteNoEncontradoException.java
│   │   ├── ColaDeEsperaVaciaException.java
│   │   ├── PilaDeshacerVaciaException.java
│   │   └── ArchivoInvalidoException.java
│   ├── util/                    # Utilidades de soporte
│   │   ├── LectorCSV.java       # Lector y validador del archivo inscripciones.csv
│   │   ├── ValidadorEntrada.java# Captura segura de teclado (anti-crashes)
│   │   └── FormateadorConsola.java # Tablas, títulos y divisores limpios
│   ├── servicio/
│   │   └── SistemaAcademico.java# Fachada central (Lógica de negocio, HashMap, TreeMap, Queue)
│   └── app/
│       └── Main.java            # Menú interactivo por consola (22 opciones exactas)
├── inscripciones.csv            # Archivo CSV de prueba con 100 solicitudes batch
└── README.md                    # Portada del proyecto
```

---

## Matriz de Estructuras de Datos Utilizadas

| Estructura | Caso de Uso en el Sistema | Tipo | Complejidad Clave |
| :--- | :--- | :--- | :--- |
| `boolean[7][24]` | Disponibilidad semanal de aulas (días y horas) | Arreglo nativo | $O(1)$ consulta / reserva |
| `int[N][N]` | Distancias en metros entre edificios (Grafo Campus) | Arreglo nativo | $O(1)$ consulta arista |
| `Double[10][20]` | Registro histórico de notas semestrales por estudiante | Arreglo nativo | $O(1)$ acceso por índice |
| `Facultad[5]` | Lista fija de unidades académicas institucionales | Arreglo nativo | $O(1)$ acceso |
| `List` (`ArrayList`) | Pre-requisitos de asignaturas e historial de aprobadas | Java Collections | $O(1)$ amortizado / caché L1/L2 |
| `Queue` (`LinkedList` / `ArrayDeque`) | Cola de espera por cupo lleno y solicitudes batch | Java Collections | $O(1)$ FIFO estricto |
| `Stack` | Historial Deshacer/Rehacer y navegación de reportes | Java Collections | $O(1)$ LIFO estricto |
| `HashMap` | Búsqueda inmediata de estudiantes por ID | Java Collections | $O(1)$ promedio por Hash |
| `TreeMap` | Aulas ordenadas lexicográficamente por código | Java Collections | $O(\log N)$ Red-Black Tree |

---

