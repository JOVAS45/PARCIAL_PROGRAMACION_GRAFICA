package com.flappybird;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Gestiona la entrada del teclado para ambos jugadores.
 */
public class InputManager {
    private long ventana;
    private boolean espacioPresionado = false;
    private boolean wPresionada = false;
    private boolean rPresionada = false;
    private int framesPresionEspacio = 0;  // Contador sensibilidad P1
    private int framesPresionW = 0;         // Contador sensibilidad P2
    private boolean espacioWasPressed = false;
    private boolean wWasPressed = false;

    public InputManager(long ventana) {
        this.ventana = ventana;
    }

    /**
     * Actualiza el estado de entrada. Debe llamarse cada frame.
     */
    public void actualizar() {
        // ESPACIO (Jugador 1)
        boolean espacioAhora = glfwGetKey(ventana, GLFW_KEY_SPACE) == GLFW_PRESS;
        if (espacioAhora) {
            framesPresionEspacio++;
        } else {
            framesPresionEspacio = 0;
        }
        espacioPresionado = espacioAhora && !espacioWasPressed; // Solo primer frame
        espacioWasPressed = espacioAhora;
        
        // W (Jugador 2)
        boolean wAhora = glfwGetKey(ventana, GLFW_KEY_W) == GLFW_PRESS;
        if (wAhora) {
            framesPresionW++;
        } else {
            framesPresionW = 0;
        }
        wPresionada = wAhora && !wWasPressed; // Solo primer frame
        wWasPressed = wAhora;
        
        // R (Reiniciar)
        rPresionada = glfwGetKey(ventana, GLFW_KEY_R) == GLFW_PRESS;
    }

    /**
     * Verifica si el jugador 1 presionó saltar (ESPACIO).
     */
    public boolean jugador1Salta() {
        return espacioPresionado;
    }
    
    /**
     * Retorna sensibilidad de toque P1: 0=tap corto, 1=tap largo.
     */
    public int jugador1Sensibilidad() {
        return framesPresionEspacio >= 10 ? 1 : 0;
    }

    /**
     * Verifica si el jugador 2 presionó saltar (W o ARRIBA).
     */
    public boolean jugador2Salta() {
        return wPresionada || glfwGetKey(ventana, GLFW_KEY_UP) == GLFW_PRESS;
    }
    
    /**
     * Retorna sensibilidad de toque P2: 0=tap corto, 1=tap largo.
     */
    public int jugador2Sensibilidad() {
        return framesPresionW >= 10 ? 1 : 0;
    }

    /**
     * Verifica si se presionó R para reiniciar.
     */
    public boolean reiniciar() {
        return rPresionada;
    }

    /**
     * Verifica si se presionó ESC para salir.
     */
    public boolean salir() {
        return glfwGetKey(ventana, GLFW_KEY_ESCAPE) == GLFW_PRESS;
    }
}
