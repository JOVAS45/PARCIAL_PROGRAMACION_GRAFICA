package com.flappybird;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Aplicación principal de Flappy Bird.
 * Gestiona la ventana, el loop principal y la coordinación de componentes.
 */
public class AppFlappyBird {
    private long window;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "Flappy Bird - OpenGL 3.3";

    private Game game;
    private Renderer renderer;
    private InputManager inputManager;
    private boolean ventanaAbierta = true;

    // Timing para FPS
    private double deltaTime = 0;
    private long lastTime = System.nanoTime();

    public static void main(String[] args) {
        new AppFlappyBird().run();
    }

    /**
     * Inicia la aplicación.
     */
    public void run() {
        inicializarGLFW();
        crearVentana();
        inicializarOpenGL();

        game = new Game();
        renderer = new Renderer();
        inputManager = new InputManager(window);

        // Loop principal
        while (ventanaAbierta && !glfwWindowShouldClose(window)) {
            calcularDeltaTime();
            inputManager.actualizar();
            
            if (inputManager.salir()) {
                ventanaAbierta = false;
            }

            game.actualizar(inputManager);

            renderer.iniciarFrame();
            renderer.dibujarFondo();

            // Dibujar UI según estado del juego
            if (game.getEstado() == Game.EstadoJuego.MENU) {
                renderer.dibujarPantallaInicio();
            } else if (game.getEstado() == Game.EstadoJuego.JUGANDO) {
                // Dibujar tuberías
                for (Pipe tuberia : game.getTuberias()) {
                    renderer.dibujarTuberia(tuberia);
                }

                // Dibujar pájaros
                renderer.dibujarPajaro(game.getPajaro1());
                renderer.dibujarPajaro(game.getPajaro2());
                
                // Dibujar contadores
                renderer.dibujarContadores(
                    game.getPuntajeJugador1(),
                    game.getPuntajeJugador2(),
                    game.getNivelDificultad(),
                    game.getVelocidadActual()
                );
            } else if (game.getEstado() == Game.EstadoJuego.GAME_OVER) {
                // Dibujar tuberías y pájaros (estado final)
                for (Pipe tuberia : game.getTuberias()) {
                    renderer.dibujarTuberia(tuberia);
                }
                renderer.dibujarPajaro(game.getPajaro1());
                renderer.dibujarPajaro(game.getPajaro2());
                
                renderer.dibujarPantallaGameOver(game.getPuntajeJugador1(), game.getPuntajeJugador2());
            }

            renderer.terminarFrame();
            dibujarHUD();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        limpiar();
    }

    /**
     * Calcula el tiempo transcurrido desde el frame anterior.
     */
    private void calcularDeltaTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
        lastTime = currentTime;
    }

    /**
     * Dibuja el HUD (información de puntos y nivel).
     */
    private void dibujarHUD() {
        // En versiones futuras, aquí se dibujará el texto con puntos y nivel
        String titulo = String.format("P1: %d | P2: %d | Nivel: %d | Vel: %.1f",
            game.getPuntajeJugador1(),
            game.getPuntajeJugador2(),
            game.getNivelDificultad(),
            game.getVelocidadActual());
        
        glfwSetWindowTitle(window, TITLE + " - " + titulo);
    }

    /**
     * Inicializa GLFW.
     */
    private void inicializarGLFW() {
        if (!glfwInit()) {
            throw new IllegalStateException("No se pudo inicializar GLFW");
        }
    }

    /**
     * Crea la ventana de la aplicación.
     */
    private void crearVentana() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, 0, 0);
        if (window == 0) {
            throw new RuntimeException("No se pudo crear ventana GLFW");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // VSync
        glfwShowWindow(window);
    }

    /**
     * Inicializa OpenGL.
     */
    private void inicializarOpenGL() {
        GL.createCapabilities();
        glClearColor(0.7f, 0.9f, 1.0f, 1.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Limpia recursos.
     */
    private void limpiar() {
        renderer.limpiar();
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
