package com.flappybird;

/**
 * Representa una tubería en el juego.
 */
public class Pipe {
    public float x;
    public float topY; // Posición del hueco (Y central del gap)
    public float ancho = 50;
    public float gap = 150; // Espacio entre tuberías
    public boolean puntuada = false;

    /**
     * Constructor de la tubería.
     * 
     * @param x    posición X
     * @param topY posición Y del centro del hueco
     */
    public Pipe(float x, float topY) {
        this.x = x;
        this.topY = topY;
    }

    /**
     * Actualiza la posición de la tubería.
     * 
     * @param velocidad velocidad de desplazamiento
     */
    public void actualizar(float velocidad) {
        x -= velocidad;
    }

    /**
     * Verifica si la tubería está fuera de pantalla.
     * 
     * @param ancho ancho de la ventana
     * @return true si está completamente fuera a la izquierda
     */
    public boolean estafueraDepantalla(int ancho) {
        return x + this.ancho < 0;
    }

    /**
     * Obtiene la posición inferior de la tubería superior.
     */
    public float obtenerPIPETopBottom() {
        return topY - gap / 2;
    }

    /**
     * Obtiene la posición superior de la tubería inferior.
     */
    public float obtenerPIPEBottomTop() {
        return topY + gap / 2;
    }

    @Override
    public String toString() {
        return String.format("Pipe: (%.1f, %.1f) gap=%.1f", x, topY, gap);
    }
}
