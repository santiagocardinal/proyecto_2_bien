[![Santiago Cardinal](https://img.shields.io/badge/GitHub-Santiago_Cardinal-8E412E?logo=github&logoColor=black)](https://github.com/santiagocardinal)
[![Sebastián Chapper](https://img.shields.io/badge/GitHub-Sebastián_Chapper-BA6F4D?logo=github&logoColor=black)](https://github.com/sebachapper)
[![Nicolás Leyton](https://img.shields.io/badge/GitHub-Nicolás_Leyton-E6CEBC?logo=github&logoColor=black)](https://github.com/Nicosley)
[![Hernan López](https://img.shields.io/badge/GitHub-Hernan_López-A2A182?logo=github&logoColor=black)](https://github.com/HL0pez)
[![Rodrigo Montiel](https://img.shields.io/badge/GitHub-Rodrigo_Montiel-687259?logo=github&logoColor=black)](https://github.com/rodrigomontiel-44)
[![Luján Uhalde](https://img.shields.io/badge/GitHub-Luján_Uhalde-F4ECE2?logo=github&logoColor=black)](https://github.com/Lujan448)

---
# $\color{yellow}{\textsf{Proyecto Integrador 2 — Modelado y Algoritmos sobre Grafos}}$

## $\color{yellow}{\textsf{Equipo 2: Red Social}}$

**Algoritmos y Estructuras de Datos** · **Publicación:** `07/10/2026` · **Entrega:** `28/10/2026, 23:59`

---

## $\color{yellow}{\textsf{Aspectos evaluados}}$

Lo que evaluamos es **¿Pueden entender, justificar, probar y modificar el código que entregaron?**

| Dimensión | Qué se evalúa |
|---|---|
| **Implementación** | Que los grafos y sus algoritmos funcionen correctamente y respeten su TDA |
| **Diseño** | Que hayan tomado buenas decisiones sobre representación, modelado del dominio, encapsulamiento y reutilización |
| **Complejidad** | Que puedan explicar el costo temporal y espacial de las operaciones |
| **Testing** | Que hayan diseñado casos que realmente permitan detectar errores |
| **Comprensión/defensa** | Que cada integrante pueda explicar y modificar su código |

> [!NOTE]
> El uso de herramientas de IA está permitido durante el desarrollo del proyecto. Sin embargo, cada integrante debe ser capaz de comprender, explicar, justificar, probar y modificar las implementaciones entregadas por sí mismo.

---

## $\color{yellow}{\textsf{Problema integrador}}$

Diseñen e implementen una biblioteca de grafos y utilícenla para resolver un problema real, justificando cómo el modelo elegido (qué son los vértices, las aristas y sus pesos) y la representación utilizada afectan las operaciones y su complejidad.

---

## $\color{yellow}{\textsf{1. Introducción}}$

En este proyecto deberán desarrollar una biblioteca de grafos dirigidos y no dirigidos, junto con sus principales algoritmos, y utilizarla para modelar y resolver un problema de un dominio concreto.

El objetivo no es solamente implementar algoritmos que funcionen, sino también comprender cómo modelar una situación real mediante grafos: qué representan los vértices y las aristas, qué información se asocia a cada uno, si el grafo debe ser dirigido o no, y qué algoritmo resulta adecuado para cada pregunta que se quiere responder. El sistema deberá utilizar la biblioteca desarrollada por el grupo y **no podrá utilizar bibliotecas externas de grafos** para resolver las operaciones principales.

El proyecto se realizará en los grupos ya establecidos y tendrá fecha de entrega: **28/10/2026 a las 23:59**.

Durante el desarrollo podrán utilizar herramientas de Inteligencia Artificial generativa y agentes de programación. Sin embargo, cada integrante deberá ser capaz de comprender, explicar, justificar, probar y modificar el código entregado.

El proyecto se organiza en **3 desafíos**. Al final del documento se incluye un cronograma de referencia para organizar el trabajo semana a semana.

---

## $\color{yellow}{\textsf{2. Objetivos}}$

Al finalizar el proyecto deberán ser capaces de:

- implementar grafos dirigidos y no dirigidos a partir de su especificación;
- distinguir entre el TDA grafo y sus posibles representaciones (matriz de adyacencia, listas de adyacencia);
- modelar un problema real mediante grafos, decidiendo qué representan los vértices, las aristas y sus pesos;
- implementar y aplicar algoritmos de recorrido, caminos mínimos, cerradura transitiva, clasificación topológica y árboles abarcadores de costo mínimo;
- seleccionar el algoritmo adecuado según la pregunta que se quiere responder;
- analizar la complejidad temporal y espacial de los algoritmos y de las operaciones del sistema;
- identificar oportunidades de optimización;
- diseñar casos de prueba, incluyendo casos borde;
- comparar experimentalmente diferentes implementaciones;
- trabajar colaborativamente en un proyecto de software.

---

## $\color{yellow}{\textsf{3. Desafíos}}$

### $\color{yellow}{\textsf{Desafío 1 — CONSTRUIR: biblioteca de grafos}}$

El primer desafío consiste en construir una biblioteca propia de grafos. Deberán implementar todas las interfaces proporcionadas en el proyecto base de la unidad (`proyecto-base-ut04`):

- **`IGraph`, `IDirectedIGraph` e `IUndirectedGraph`**: grafo dirigido y grafo no dirigido, con sus operaciones de vértices, aristas, adyacencias, sucesores/predecesores, conexidad y detección de ciclos.
- **`IDirectedGraphAlgorithms`**: Dijkstra, Floyd, Warshall, centro del grafo y excentricidad, todos los caminos entre dos vértices, búsqueda en profundidad, búsqueda en amplitud y clasificación topológica.
- **`IUndirectedGraphAlgorithm`**: Prim, Kruskal y búsqueda en amplitud.

Adicionalmente, deberán implementar el algoritmo de **puntos de articulación** visto en el curso.

Para cada estructura y algoritmo deberán asegurarse de que funcione correctamente en diferentes situaciones, incluyendo:

- grafo vacío;
- un único vértice;
- grafos conexos y no conexos;
- grafos con y sin ciclos;
- vértices inexistentes o sin aristas;
- pesos iguales y caminos alternativos de igual costo;
- casos borde propios de cada algoritmo.

Además, deberán desarrollar pruebas que permitan comprobar el correcto funcionamiento de sus implementaciones.

---

### $\color{yellow}{\textsf{Desafío 2 — MODELAR: Red Social}}$

#### $\color{yellow}{\textsf{Escenario}}$

Una nueva red social necesita el núcleo de su sistema de información. En la plataforma, los usuarios pueden seguir a otros usuarios (sin que el seguimiento sea necesariamente recíproco), publicar posteos, comentar posteos de otros usuarios y compartir posteos, lo que hace que el contenido llegue a los seguidores de quien lo comparte. Además, en un posteo o comentario se puede arrobar (mencionar con `@`) a otros usuarios: el usuario mencionado recibe el posteo aunque no siga a su autor, por lo que la mención genera una interacción entre ese posteo y un usuario que no lo tenía en su feed.

Para mantener a los usuarios activos, la plataforma quiere sugerir a quién seguir, en base a las conexiones existentes (por ejemplo, personas seguidas por las personas que uno sigue), y conocer qué tan cerca están dos usuarios entre sí dentro de la red.

Por otro lado, el equipo de análisis de la plataforma necesita entender la estructura de la red: quiénes son los usuarios más influyentes, qué comunidades o grupos de usuarios se forman, qué usuarios funcionan como puente entre comunidades que de otra forma estarían desconectadas, y hasta dónde llega la difusión de un posteo.

Su tarea es diseñar e implementar el sistema de gestión y análisis de la red social utilizando la biblioteca de grafos desarrollada en el Desafío 1.

#### $\color{yellow}{\textsf{¿Qué debe hacer el sistema?}}$

El sistema deberá resolver las necesidades planteadas en el escenario. El grupo deberá analizar qué información necesita representar, cómo modelarla mediante uno o más grafos y qué algoritmos resultan adecuados para cada necesidad. El sistema deberá permitir, como mínimo:

- **Usuarios y seguidores:** registrar usuarios, seguir y dejar de seguir, consultar seguidores, seguidos y seguidores mutuos.
- **Sugerencias y cercanía:** sugerir usuarios a seguir en base a las conexiones existentes (por ejemplo, *"amigos de amigos"*), justificando el criterio de orden de las sugerencias, y calcular los grados de separación entre dos usuarios.
- **Influencia y comunidades:** identificar a los usuarios más influyentes según uno o más criterios definidos y justificados por el grupo, detectar comunidades de usuarios e identificar a los usuarios que funcionan como puente entre comunidades.
- **Posteos y difusión:** registrar posteos, comentarios y posteos compartidos; obtener el feed de un usuario y determinar el alcance de un posteo, es decir, a qué usuarios llega a través de los compartidos y las menciones.
- **Menciones:** permitir arrobar usuarios en posteos y comentarios, de modo que el posteo llegue a usuarios que no lo tenían en su feed. Las menciones deberán tenerse en cuenta en el alcance de un posteo (por ejemplo, un usuario mencionado que luego comparte el posteo lo hace llegar a sus propios seguidores) y podrán utilizarse en las sugerencias y en las métricas de influencia.
- **Consultar información:** el sistema deberá permitir realizar consultas que consideren útiles para el dominio.

> [!IMPORTANT]
> El grupo deberá definir **al menos cinco operaciones/consultas** que considere relevantes y justificar su elección. Las operaciones deberán requerir el uso de los grafos y algoritmos desarrollados y no podrán consistir únicamente en mostrar información directamente almacenada.

#### $\color{yellow}{\textsf{Preguntas guía para el modelado}}$

No es obligatorio responderlas una por una, pero las decisiones que tomen al respecto deberán quedar justificadas en el informe:

- ¿Qué representan los vértices y las aristas? ¿Conviene un único grafo o varios (seguidores, interacciones, compartidos)?
- ¿Qué grafos son dirigidos y cuáles no? ¿Cuándo tiene sentido considerar la relación de seguimiento como no dirigida?
- ¿Las aristas tienen peso? ¿Podría usarse la cantidad de interacciones entre usuarios (comentarios, compartidos, menciones) como peso de una relación?
- ¿Cómo se representa una mención? ¿Es una relación entre usuarios, entre un posteo y un usuario, o ambas? ¿Cómo cambia el recorrido que calcula el alcance de un posteo al incorporar las menciones?
- ¿Qué significa "influyente" y qué significa "comunidad" en términos de grafos?

#### $\color{yellow}{\textsf{Implementación}}$

Una vez definido el modelo, deberán implementar el sistema utilizando **exclusivamente** la biblioteca de grafos desarrollada por el grupo en el Desafío 1. El sistema deberá incluir un conjunto de datos de ejemplo representativo del dominio y demostrar el funcionamiento de las decisiones tomadas mediante un conjunto de casos de prueba (orientados a la solución diseñada y además de las pruebas desarrolladas para validar la biblioteca).

#### $\color{yellow}{\textsf{Análisis de complejidad}}$

Para las operaciones principales del sistema deberán indicar la complejidad temporal de la solución implementada, expresada en función de la cantidad de vértices y aristas. Deberán explicar por qué la operación tiene ese costo en función del algoritmo y la representación elegidos.

> [!NOTE]
> En este desafío no se evalúa solamente que el sistema funcione. Se evaluará especialmente la capacidad del grupo para transformar los requerimientos de un problema real en un modelo basado en grafos, justificando las decisiones tomadas. Una misma situación puede modelarse de diferentes maneras y resolverse con distintos algoritmos, pero no todas las alternativas son igualmente adecuadas.

---

### $\color{yellow}{\textsf{Desafío 3 — OPTIMIZAR}}$

Una implementación que funciona no necesariamente es una implementación eficiente. En este desafío deberán analizar situaciones en las que determinadas operaciones pueden convertirse en un cuello de botella, por ejemplo cuando la red crece o cuando una misma consulta se repite muchas veces. Deberán identificar el problema y proponer una solución.

Una vez implementado el sistema, deberán identificar al menos una operación cuya eficiencia pueda mejorarse mediante un cambio en la representación, en el algoritmo utilizado o en la forma de reutilizar resultados. Después:

1. identificar la operación;
2. medir/analizar su costo;
3. proponer una alternativa;
4. implementarla;
5. comparar ambas soluciones.

---

## $\color{yellow}{\textsf{4. Requisitos generales}}$

El proyecto deberá desarrollarse en **Java**. Deberán entregar:

### $\color{yellow}{\textsf{Código fuente}}$

El proyecto completo, incluyendo:

- implementaciones;
- interfaces;
- aplicación;
- datos de ejemplo;
- pruebas;
- archivos necesarios para ejecutar el proyecto.

### $\color{yellow}{\textsf{Informe}}$

El informe deberá incluir:

- descripción general de la solución;
- modelo de grafos utilizado (qué representan vértices, aristas y pesos) y su justificación;
- representación elegida para los grafos y principales decisiones de diseño;
- algoritmos utilizados para cada funcionalidad y consulta;
- análisis de complejidad;
- decisiones tomadas en el Desafío 2;
- optimizaciones realizadas en el Desafío 3;
- resultados de los experimentos realizados;
- registro del uso de herramientas de IA.

### $\color{yellow}{\textsf{Defensa}}$

Al finalizar el proyecto se realizará una instancia de defensa, en la clase posterior a la entrega (fecha a confirmar).

La defensa será grupal e individual. Durante y después de la defensa podrán realizarse preguntas sobre cualquier parte del proyecto y podrán solicitarse pequeñas modificaciones sobre el código.

---

## $\color{yellow}{\textsf{5. Evaluación}}$

La evaluación del trabajo considerará tanto las decisiones de diseño y su implementación como su justificación, además del análisis e informe que respalden dichas decisiones.

| Criterio | Peso |
|---|:---:|
| Implementación y diseño de la biblioteca de grafos | 20% |
| Modelado, solución implementada y justificación | 20% |
| Correctitud y testing | 10% |
| Análisis de complejidad | 5% |
| Optimización y análisis experimental | 10% |
| Informe y calidad del proyecto | 15% |
| Presentación grupal | 20% |
| **Total** | **100%** |

La calificación individual final de cada alumno será la evaluación grupal del trabajo ponderada por su nota individual de la defensa. Se espera que todos los integrantes del equipo de trabajo conozcan en profundidad la solución propuesta y sean capaces de responder preguntas específicas del diseño de la solución.

---

## $\color{yellow}{\textsf{6. Cronograma de referencia}}$

El siguiente cronograma es una guía para organizar el trabajo. No es obligatorio seguirlo al pie de la letra, pero se recomienda que al cierre de cada semana el equipo tenga el avance indicado.

| Semana | Actividades sugeridas | Avance esperado al cierre |
|---|---|---|
| **Semana 1**<br>`07/10 – 11/10` | • Lectura de la consigna y del proyecto base.<br>• Diseño inicial del modelo: qué son vértices, aristas y pesos; grafo dirigido o no dirigido.<br>• Elección de la representación y reparto de tareas.<br>• Implementación de las estructuras (grafo dirigido y no dirigido) y sus tests. | Modelo inicial documentado. Grafos dirigido y no dirigido implementados y probados. |
| **Semana 2**<br>`12/10 – 18/10` | • Implementación de los algoritmos del Desafío 1 y sus tests.<br>• Carga de datos de ejemplo del dominio.<br>• Implementación de los requerimientos mínimos del Desafío 2 y sus casos de prueba. | Desafío 1 completo. Requerimientos mínimos del Desafío 2 funcionando y probados. |
| **Semana 3**<br>`19/10 – 25/10` | • Definición, justificación e implementación de las cinco consultas propias.<br>• Análisis de complejidad de las operaciones principales.<br>• Desafío 3: identificar la operación, medir, proponer e implementar la alternativa, comparar. | Desafío 2 completo. Optimización implementada con mediciones. |
| **Semana 4**<br>`26/10 – 28/10` | • Documentación del código.<br>• Redacción del informe (incluye resultados de experimentos y registro de uso de IA).<br>• Preparación de la presentación. | **Entrega: miércoles 28/10/2026, 23:59.** |

