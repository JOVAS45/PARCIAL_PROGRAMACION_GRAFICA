# Flappy Bird Parcial - Instrucciones para Copilot

Este proyecto es el trabajo parcial de Programación Gráfica - Flappy Bird en OpenGL.

## Objetivo General
Implementar un juego Flappy Bird con Java + LWJGL 3.3 que cumpla con 4 requisitos obligatorios:
1. Pájaro compuesto por figuras geométricas
2. Modo de dos jugadores simultáneos
3. Incremento progresivo de velocidad
4. Mejora de interfaz del juego

## Contexto del Código
- **Proyecto Base**: AppFlappyBird.java (clase monolítica de demostración)
- **Objetivo**: Refactorizar en clases separadas y agregar funcionalidades

## Guías de Implementación

### Arquitectura Recomendada
```
Bird.java          - Lógica del pájaro (posición, física, animación)
Pipe.java          - Lógica de tuberías
Game.java          - Controlador principal del juego
Renderer.java      - Sistema de renderizado OpenGL
InputManager.java  - Manejo de entrada
SceneManager.java  - Gestión de escenas (inicio, juego, game over)
```

### Criterios de Código
- Uso de comentarios en español e inglés
- Métodos pequeños y con responsabilidad única
- Variables con nombres descriptivos
- Sin "código muerto" o comentarios innecesarios

## Fecha Límite
**16 de mayo de 2026** - Defensa oral con modificaciones en vivo
