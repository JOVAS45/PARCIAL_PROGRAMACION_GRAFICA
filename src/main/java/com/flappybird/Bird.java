package com.flappybird;

import org.joml.Matrix4f;

/**
 * Representa un pájaro compuesto por múltiples figuras geométricas.
 * Incluye cuerpo, pico, alas, cola y ojo con animación coherente.
 */
public class Bird {
    public float x;
    public float y;
    public float velocidad;
    public float radio = 15;
    public int playerId; // 0 para jugador 1, 1 para jugador 2
    public boolean vivo = true;
    
    // Animación de alas
    private float tiempoAleteo = 0;
    private static final float VELOCIDAD_ALETEO = 0.1f;
    private static final float AMPLITUDE_ALETEO = 30;
    
    // Física
    private static final float GRAVEDAD = 0.5f;
    private static final float FUERZA_SALTO_CORTO = -5f;   // Tap corto
    private static final float FUERZA_SALTO_LARGO = -10f;  // Tap largo
    private static final float FUERZA_SALTO = -10f;        // Compatibilidad
    
    // Colores del pájaro según jugador
    private float colorR;
    private float colorG;
    private float colorB;

    /**
     * Constructor del pájaro.
     * @param x posición X inicial
     * @param y posición Y inicial
     * @param playerId ID del jugador (0 o 1)
     */
    public Bird(float x, float y, int playerId) {
        this.x = x;
        this.y = y;
        this.playerId = playerId;
        this.velocidad = 0;
        
        // Colores diferentes para cada jugador
        if (playerId == 0) {
            // Jugador 1: Amarillo
            this.colorR = 1.0f;
            this.colorG = 0.8f;
            this.colorB = 0.0f;
        } else {
            // Jugador 2: Azul
            this.colorR = 0.0f;
            this.colorG = 0.6f;
            this.colorB = 1.0f;
        }
    }

    /**
     * Actualiza la física del pájaro.
     */
    public void actualizar() {
        if (!vivo) return;
        
        velocidad += GRAVEDAD;
        y += velocidad;
        
        // Actualizar animación de alas
        tiempoAleteo += VELOCIDAD_ALETEO;
        if (tiempoAleteo > 360) {
            tiempoAleteo = 0;
        }
    }

    /**
     * Hace que el pájaro salte.
     */
    public void saltar() {
        if (vivo) {
            velocidad = FUERZA_SALTO;
        }
    }
    
    /**
     * Hace que el pájaro salte con sensibilidad de toque.
     * @param sensibilidad 0=salto corto, 1=salto largo
     */
    public void saltarConSensibilidad(int sensibilidad) {
        if (vivo) {
            if (sensibilidad == 0) {
                // Tap corto: salto pequeño
                velocidad = FUERZA_SALTO_CORTO;
            } else {
                // Tap largo: salto fuerte
                velocidad = FUERZA_SALTO_LARGO;
            }
        }
    }

    /**
     * Obtiene el ángulo de rotación basado en la velocidad.
     * @return ángulo en grados
     */
    public float obtenerAnguloRotacion() {
        // Mayor velocidad hacia abajo = mayor ángulo hacia abajo
        return Math.min(velocidad * 2, 45);
    }

    /**
     * Obtiene el ángulo de aleteo animado.
     * @return ángulo en grados
     */
    public float obtenerAnguloAleteo() {
        return (float) (Math.sin(Math.toRadians(tiempoAleteo)) * AMPLITUDE_ALETEO);
    }

    /**
     * Detecta colisión con los límites del mapa.
     * @param altoMapa alto de la ventana
     * @return true si colisiona
     */
    public boolean colisionConLimites(int altoMapa) {
        if (y - radio < 0 || y + radio > altoMapa) {
            vivo = false;
            return true;
        }
        return false;
    }

    /**
     * Detecta colisión AABB con una tubería.
     * @param tuberia tubería a verificar
     * @return true si hay colisión
     */
    public boolean colisionConTuberia(Pipe tuberia) {
        float pipeLeft = tuberia.x;
        float pipeRight = tuberia.x + tuberia.ancho;
        
        if (x + radio > pipeLeft && x - radio < pipeRight) {
            float topPipeBottom = tuberia.topY - tuberia.gap / 2;
            float bottomPipeTop = tuberia.topY + tuberia.gap / 2;
            
            if (y - radio < topPipeBottom || y + radio > bottomPipeTop) {
                vivo = false;
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el color RGB del pájaro.
     */
    public float[] obtenerColor() {
        return new float[]{colorR, colorG, colorB, 1.0f};
    }

    /**
     * Reinicia el pájaro.
     */
    public void reiniciar() {
        velocidad = 0;
        vivo = true;
        tiempoAleteo = 0;
    }

    /**
     * Obtiene información de debug del pájaro.
     */
    @Override
    public String toString() {
        return String.format("Bird P%d: (%.1f, %.1f) v=%.1f %s", 
            playerId + 1, x, y, velocidad, vivo ? "VIVO" : "MUERTO");
    }
}
