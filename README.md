# Flappy Bird - Parcial Programación Gráfica

## Descripción del Proyecto

**Flappy Bird** implementado en Java con **OpenGL 3.3** usando la librería **LWJGL 3.3**. 

El juego incluye:
- Modo dos jugadores simultáneos
- Física realista de caída y salto
- Dificultad progresiva según puntuación
- Interfaz gráfica mejorada con HUD y menús
- Arquitectura limpia dividida en clases especializadas

**Tecnologías**: Java 11+, LWJGL 3.3, OpenGL 3.3 Core Profile, JOML 1.10.5

## Controles

### Jugador 1 (Pájaro Amarillo)
- **ESPACIO**: Saltar

### Jugador 2 (Pájaro Azul)
- **W** o **FLECHA ARRIBA**: Saltar

### Menú y Game Over
- **R**: Reiniciar juego
- **ESC**: Salir

## Instalación y Compilación

### Requisitos Previos
- **Java JDK 11+**
- **Maven 3.6+**
- **OpenGL 3.3+** compatible

### Paso 1: Compilar el Proyecto
```bash
cd d:\Projects\ParcialPG
mvn clean package -DskipTests
```

### Paso 2: Ejecutar el Juego
```bash
java -jar target/flappy-bird-opengl-1.0-SNAPSHOT.jar
```

### Compilar y Ejecutar en Una Línea
```bash
mvn clean package -DskipTests && java -jar target/flappy-bird-opengl-1.0-SNAPSHOT.jar
```

## Estructura del Proyecto

```
ParcialPG/
├── pom.xml                                    # Configuración Maven
├── README.md                                  # Este archivo
├── src/main/
│   ├── java/com/flappybird/
│   │   ├── AppFlappyBird.java                # Punto de entrada principal
│   │   ├── Game.java                         # Lógica del juego
│   │   ├── Bird.java                         # Física del pájaro
│   │   ├── Pipe.java                         # Estructura de tuberías
│   │   ├── Renderer.java                     # Sistema de renderizado OpenGL
│   │   ├── InputManager.java                 # Gestor de entrada
│   │   └── SoundManager.java                 # Sistema de sonido
│   └── resources/shaders/
│       ├── vertex.glsl                       # Shader de vértices
│       └── fragment.glsl                     # Shader de fragmentos
└── target/
    └── flappy-bird-opengl-1.0-SNAPSHOT.jar  # JAR compilado
```

---

## REQUISITOS IMPLEMENTADOS

### 1. Pájaro Compuesto por Figuras Geométricas (5%)

**Características:**
- Cuerpo principal - Rectángulo central (25×30)
- Cabeza - Círculo arriba (15×15)
- Pico - Triángulo distintivo (rotado con el pájaro)
- Ojo - Con pupila interior negra
- Ala izquierda - Animada con movimiento suave (sin fin)
- Cola - Rectángulo en la parte posterior
- Animación coherente - Rotación según velocidad vertical
- Sincronización - Aleteo en tiempo real

**Archivos:** `Bird.java`, `Renderer.dibujarPajaro()`

---

### 2. Modo Dos Jugadores Simultáneos (10%)

**Características:**
- Dos pájaros independientes - Posición y velocidad separadas
- Controles independientes
  - P1: ESPACIO (amarillo)
  - P2: W o ARRIBA (azul)
- Puntajes individuales - Mostrados en título
- Estado independiente - Cada uno vivo/muerto por separado
- Tuberías compartidas - Mismo escenario
- Fin de juego - Cuando AMBOS mueren
- Colores distintivos - Fácil identificación

**Archivos:** `Game.java`, `Bird.java`, `InputManager.java`

---

### 3. Incremento Progresivo de Velocidad (5%)

**Características:**
- Dificultad escala con puntaje
  - Base: 4 unidades/frame
  - Máximo: 12 unidades/frame
  - Incremento: +0.5 cada 5 puntos
- Frecuencia de tuberías aumenta (120 → 80 frames)
- Claramente perceptible - Cambio gradual
- Límite superior razonable - Sigue siendo jugable
- Visible en HUD - Mostrado como "Nivel" y "Vel"
- Cálculo: Nivel = (velocidad - 4) / 0.5 + 1

**Archivos:** `Game.actualizarDificultad()`

---

### 4. Mejora de Interfaz (5%)

**Características:**
- Fondo: Cielo azul + nubes semitransparentes + suelo verde
- Pantalla de inicio: Menú con instrucciones claras
- HUD en tiempo real:
  - Panel amarillo: Puntaje Jugador 1 (arriba izq)
  - Panel azul: Puntaje Jugador 2 (arriba der)
  - Panel verde: Nivel actual (centro)
- Pantalla de Game Over: 
  - Título rojo "GAME OVER"
  - Puntajes ambos jugadores
  - Botón REINICIAR (verde) grande
  - Botón ESC (rojo)
- Animaciones: Aleteo sincronizado
- Sonidos (javax.sound.sampled):
  - Salto: 440 Hz, 100ms
  - Punto: 880 Hz, 150ms
  - Game Over: 220 Hz, 300ms
- Colores distintivos: Pájaros amarillo/azul

**Archivos:** `Renderer.java`, `SoundManager.java`

---

### 5. Calidad de Código (5%)

**Características:**
- Arquitectura limpia: 6 clases especializadas
- Separación de responsabilidades: Cada clase una función
- Nombres descriptivos: Variables y métodos claros
- Comentarios: Español e inglés (Javadoc completo)
- Sin código muerto: Todo funcional
- Métodos pequeños: Responsabilidad única
- Fácil de modificar: Estructura clara para cambios

**Clases:**
- `Bird.java` - Pájaro con física y colisiones
- `Pipe.java` - Tuberías procedurales
- `Renderer.java` - OpenGL rendering
- `InputManager.java` - Entrada de teclado
- `Game.java` - Lógica principal + velocidad progresiva
- `AppFlappyBird.java` - Coordinación

---

## Compilación y Ejecución

### Compilar
```bash
mvn clean compile
```

### Empaquetar
```bash
mvn package
```

### Ejecutar
```bash
java -jar target/flappy-bird-opengl-1.0-SNAPSHOT-shaded.jar
```

### Resultado Esperado
Ventana 800×600 con:
- Dos pájaros (amarillo y azul)
- Tuberías verdes generadas proceduralmente
- HUD en título mostrando puntajes y nivel
- Velocidad aumentando con puntos

---

## Cambios vs Proyecto Base

### Refactorización
- De código monolítico a 6 clases
- Separación de OpenGL, entrada y lógica

### Nuevas Características
- Pájaro geométrico compuesto
- Dos jugadores con controles independientes
- Velocidad progresiva automática
- Interfaz mejorada (fondo, HUD)
- Animación de aleteo

---

## Problemas Comunes y Soluciones

### "Failed to create GLFW window"
- Verifica GPU con OpenGL 3.3+
- Actualiza drivers
- Intenta en otra máquina

### "Unable to initialize GLFW"
- Ejecuta `mvn clean`
- Elimina carpeta `target/`
- Compila de nuevo

### Bajo rendimiento
- Cierra otras aplicaciones
- Actualiza drivers GPU
- Reduce complejidad geométrica

---


## Dependencias

- **LWJGL 3.3.1** - OpenGL + GLFW bindings
- **OpenGL 3.3 Core** - Pipeline gráfico
- **JOML 1.10.5** - Matemáticas (matrices)

---

## Notas Técnicas

### Pipeline Gráfico
- **Proyección**: Ortográfica 2D (800×600)
- **Perfil OpenGL**: Core 3.3
- **GLSL**: Versión 150
- **Renderizado**: Quads primitivos con transformadas de modelo

### Física
- **Gravedad**: 0.5 unidades/frame²
- **Fuerza de salto**: -10 unidades/frame
- **Límites**: Colisión en y ≤ 0 o y ≥ ALTO

### Colisiones
- **Tipo**: AABB (Axis-Aligned Bounding Box)
- **Pájaro vs límites**: Altura mínima/máxima
- **Pájaro vs tuberías**: Rectángulos de aproximación

### Audio
- **Librería**: javax.sound.sampled (Java estándar)
- **Formato**: PCM 16-bit, 44.1 kHz, mono
- **Generación**: Ondas sinusoidales procedurales con fade-out


