package com.flappybird;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del juego. Gestiona:
 * - Dos jugadores simultáneos
 * - Estado del juego (menú, jugando, game over)
 * - Física y colisiones
 * - Velocidad progresiva según puntaje
 * - Generación de tuberías
 */
public class Game {
    private Bird pajaro1;
    private Bird pajaro2;
    private List<Pipe> tuberias = new ArrayList<>();
    private int puntajeJugador1 = 0;
    private int puntajeJugador2 = 0;
    private SoundManager soundManager;
    
    // Velocidad y dificultad progresiva
    private float velocidadTuberias = 4;
    private float tiempoEntreTuberias = 250; // Más grande al inicio (más SEPARADOS horizontalmente)
    private float tiempoActual = 0;
    private static final float VELOCIDAD_INICIAL = 4;
    private static final float VELOCIDAD_MAXIMA = 12;
    private static final float TIEMPO_INICIAL = 500; // Tubos MUY separados al inicio (fácil)
    private static final float TIEMPO_MINIMO = 60;  // Más juntados en dificultad máxima
    
    // Estado del juego
    public enum EstadoJuego {
        MENU, JUGANDO, GAME_OVER
    }
    private EstadoJuego estado = EstadoJuego.MENU;
    
    // Dimensiones
    private static final int ANCHO = 800;
    private static final int ALTO = 600;
    private static final float ANCHO_TUBERIA = 50;
    private static final float GAP_TUBERIA = 150;

    public Game() {
        soundManager = new SoundManager();
        reiniciar();
    }

    /**
     * Reinicia el juego a estado inicial.
     */
    public void reiniciar() {
        pajaro1 = new Bird(100, ALTO / 2, 0);
        pajaro2 = new Bird(100, ALTO / 2 - 50, 1);
        tuberias.clear();
        puntajeJugador1 = 0;
        puntajeJugador2 = 0;
        estado = EstadoJuego.MENU;
        velocidadTuberias = VELOCIDAD_INICIAL;
        tiempoEntreTuberias = TIEMPO_INICIAL;
        tiempoActual = 0;
    }

    /**
     * Actualiza la lógica del juego. Llamar cada frame.
     * @param input gestor de entrada
     */
    public void actualizar(InputManager input) {
        if (estado == EstadoJuego.MENU) {
            // En menú: presionar ESPACIO o W para empezar, o R para reiniciar
            if (input.jugador1Salta() || input.jugador2Salta()) {
                estado = EstadoJuego.JUGANDO;
            }
            return;
        }

        if (estado == EstadoJuego.GAME_OVER) {
            // En game over: R para reiniciar, ESC para salir
            if (input.reiniciar()) {
                reiniciar();
            }
            return;
        }

        // Jugando
        // Entrada de jugadores con sensibilidad de toque
        if (input.jugador1Salta()) {
            pajaro1.saltarConSensibilidad(input.jugador1Sensibilidad());
            soundManager.reproducirSalto();
        }
        if (input.jugador2Salta()) {
            pajaro2.saltarConSensibilidad(input.jugador2Sensibilidad());
            soundManager.reproducirSalto();
        }

        // Actualizar pájaros
        pajaro1.actualizar();
        pajaro2.actualizar();

        // Colisiones con límites
        if (pajaro1.colisionConLimites(ALTO)) {
            pajaro1.vivo = false;
        }
        if (pajaro2.colisionConLimites(ALTO)) {
            pajaro2.vivo = false;
        }

        // Generar tuberías
        tiempoActual += velocidadTuberias;
        if (tiempoActual >= tiempoEntreTuberias) {
            spawnTuberia();
            tiempoActual = 0;
        }

        // Actualizar tuberías
        for (Pipe tuberia : tuberias) {
            tuberia.actualizar(velocidadTuberias);
        }

        // Eliminar tuberías fuera de pantalla y detectar puntuación
        tuberias.removeIf(tuberia -> {
            if (tuberia.estafueraDepantalla(ANCHO)) {
                return true;
            }
            
            // Puntuación para jugador 1
            if (!tuberia.puntuada && tuberia.x + ANCHO_TUBERIA < pajaro1.x) {
                puntajeJugador1++;
                actualizarDificultad();
                tuberia.puntuada = true;
                soundManager.reproducirPunto();
            }
            
            // Puntuación para jugador 2
            if (!tuberia.puntuada && tuberia.x + ANCHO_TUBERIA < pajaro2.x) {
                puntajeJugador2++;
                actualizarDificultad();
                soundManager.reproducirPunto();
            }
            
            return false;
        });

        // Detectar colisiones con tuberías
        for (Pipe tuberia : tuberias) {
            pajaro1.colisionConTuberia(tuberia);
            pajaro2.colisionConTuberia(tuberia);
        }

        // Verificar si ambos pájaros están muertos (game over)
        if (!pajaro1.vivo && !pajaro2.vivo) {
            estado = EstadoJuego.GAME_OVER;
            soundManager.reproducirGameOver();
        }
    }

    /**
     * Actualiza la dificultad según el puntaje máximo.
     * - Velocidad: aumenta progresivamente
     * - Tiempo entre tuberías: disminuye progresivamente
     */
    private void actualizarDificultad() {
        int puntajeMax = Math.max(puntajeJugador1, puntajeJugador2);
        
        // Incremento progresivo: +0.5 velocidad cada 5 puntos
        float nuevaVelocidad = VELOCIDAD_INICIAL + (puntajeMax / 5f) * 0.5f;
        velocidadTuberias = Math.min(nuevaVelocidad, VELOCIDAD_MAXIMA);
        
        // Tuberías más frecuentes: reducir tiempo entre ellas
        // Comienza en 150 y baja a 80 máximo
        float nuevoTiempo = TIEMPO_INICIAL - (puntajeMax * 1.5f);
        tiempoEntreTuberias = Math.max(nuevoTiempo, TIEMPO_MINIMO);
    }

    /**
     * Genera un PAR de tuberías (una desde arriba, otra desde abajo).
     * Como en Flappy Bird original: tubería completa de tope a base con gap en medio.
     */
    private void spawnTuberia() {
        // El gap (espacio entre tubo arriba y abajo) está CENTRADO en posición variable
        // gapY = posición central del gap
        float gapY = 150 + (float) (Math.random() * 300); // Gap entre 150-450
        tuberias.add(new Pipe(ANCHO, gapY));
    }

    // Getters
    public Bird getPajaro1() { return pajaro1; }
    public Bird getPajaro2() { return pajaro2; }
    public List<Pipe> getTuberias() { return tuberias; }
    public int getPuntajeJugador1() { return puntajeJugador1; }
    public int getPuntajeJugador2() { return puntajeJugador2; }
    public EstadoJuego getEstado() { return estado; }
    public float getVelocidadActual() { return velocidadTuberias; }
    public int getNivelDificultad() { return (int) ((velocidadTuberias - VELOCIDAD_INICIAL) / 0.5f) + 1; }

    @Override
    public String toString() {
        return String.format("Game [Estado=%s, P1=%d, P2=%d, Vel=%.1f, Nivel=%d]",
            estado, puntajeJugador1, puntajeJugador2, velocidadTuberias, getNivelDificultad());
    }
}
