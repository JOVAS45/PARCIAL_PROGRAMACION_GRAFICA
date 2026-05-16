package com.flappybird;

import org.lwjgl.opengl.*;
import org.joml.Matrix4f;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Sistema de renderizado OpenGL 3.3 mejorado con soporte para:
 * - Múltiples figuras geométricas
 * - Transformaciones (traslación, rotación, escala)
 * - Dibujado de texto simple (HUD)
 * - Fondo y elementos de interfaz
 */
public class Renderer {
    private int shaderProgram;
    private int VAO;
    private int VBO;
    private int EBO;
    private static final int ANCHO = 800;
    private static final int ALTO = 600;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    public Renderer() {
        setupGraphics();
    }

    /**
     * Inicializa los recursos de OpenGL.
     */
    private void setupGraphics() {
        String vertexSource = loadResource("/shaders/vertex.glsl");
        String fragmentSource = loadResource("/shaders/fragment.glsl");

        int vertexShader = compileShader(vertexSource, GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentSource, GL_FRAGMENT_SHADER);

        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);

        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == 0) {
            System.err.println("Error enlazando programa: " + GL20.glGetProgramInfoLog(shaderProgram));
        }

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        // Crear geometría de quad
        float[] vertices = {
            -1, -1, 0,
             1, -1, 0,
             1,  1, 0,
            -1,  1, 0
        };

        int[] indices = {0, 1, 2, 2, 3, 0};

        VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAO);

        VBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        EBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 12, 0);
        GL20.glEnableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        // Configurar OpenGL
        GL11.glClearColor(0.7f, 0.9f, 1.0f, 1.0f); // Fondo azul cielo
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        // Configurar proyección ortográfica 2D
        projectionMatrix = new Matrix4f().ortho(0, ANCHO, ALTO, 0, -1, 1);
        viewMatrix = new Matrix4f(); // Matriz identidad
    }

    /**
     * Inicia el renderizado de un frame.
     */
    public void iniciarFrame() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL20.glUseProgram(shaderProgram);
        GL30.glBindVertexArray(VAO);
    }

    /**
     * Termina el renderizado del frame.
     */
    public void terminarFrame() {
        GL30.glBindVertexArray(0);
    }

    /**
     * Dibuja un quad (rectángulo) con transformaciones.
     * @param x posición X
     * @param y posición Y
     * @param ancho ancho del quad
     * @param alto alto del quad
     * @param rotacion rotación en grados
     * @param r componente rojo
     * @param g componente verde
     * @param b componente azul
     * @param a componente alfa
     */
    public void dibujarQuad(float x, float y, float ancho, float alto, float rotacion,
                            float r, float g, float b, float a) {
        Matrix4f model = new Matrix4f();
        model.translate(x, y, 0);
        model.rotateZ((float) Math.toRadians(rotacion));
        model.scale(ancho / 2f, alto / 2f, 1);

        int projLoc = GL20.glGetUniformLocation(shaderProgram, "projection");
        int viewLoc = GL20.glGetUniformLocation(shaderProgram, "view");
        int modelLoc = GL20.glGetUniformLocation(shaderProgram, "model");
        int colorLoc = GL20.glGetUniformLocation(shaderProgram, "color");

        float[] projArray = new float[16];
        float[] viewArray = new float[16];
        float[] modelArray = new float[16];
        
        projectionMatrix.get(projArray);
        viewMatrix.get(viewArray);
        model.get(modelArray);

        GL20.glUniformMatrix4fv(projLoc, false, projArray);
        GL20.glUniformMatrix4fv(viewLoc, false, viewArray);
        GL20.glUniformMatrix4fv(modelLoc, false, modelArray);
        GL20.glUniform4f(colorLoc, r, g, b, a);

        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
    }

    /**
     * Dibuja un pájaro compuesto por figuras geométricas.
     * @param pajaro pájaro a dibujar
     */
    public void dibujarPajaro(Bird pajaro) {
        // Dibujar siempre para debug (comentar después)
        // if (!pajaro.vivo) return;

        float[] color = pajaro.obtenerColor();
        float rotacion = pajaro.obtenerAnguloRotacion();
        float anguloAleteo = pajaro.obtenerAnguloAleteo();

        // Cuerpo principal (oval/círculo)
        dibujarQuad(pajaro.x, pajaro.y, 25, 30, rotacion, color[0], color[1], color[2], 1);

        // Cabeza (círculo pequeño en la parte superior)
        dibujarQuad(pajaro.x + 2, pajaro.y - 8, 15, 15, rotacion, color[0], color[1], color[2], 1);

        // Pico (triángulo - simulado con quad rotado)
        dibujarQuad(pajaro.x + 12, pajaro.y - 6, 12, 8, rotacion - 20, color[0] * 0.9f, color[1] * 0.7f, 0, 1);

        // Ojo
        dibujarQuad(pajaro.x + 6, pajaro.y - 10, 6, 6, 0, 1, 1, 1, 1);
        
        // Pupila
        dibujarQuad(pajaro.x + 7, pajaro.y - 10, 3, 3, 0, 0, 0, 0, 1);

        // Ala izquierda (animada)
        float alaRotacion = rotacion + anguloAleteo;
        dibujarQuad(pajaro.x - 8, pajaro.y, 18, 10, alaRotacion, color[0], color[1], color[2], 0.8f);

        // Cola
        dibujarQuad(pajaro.x - 15, pajaro.y + 5, 12, 8, rotacion + 20, color[0] * 0.8f, color[1] * 0.8f, color[2] * 0.8f, 1);
    }

    /**
     * Dibuja una tubería.
     * @param tuberia tubería a dibujar
     */
    public void dibujarTuberia(Pipe tuberia) {
        float colorR = 0.2f;
        float colorG = 0.8f;
        float colorB = 0.2f;

        // Tubería superior - MÁS ALTA para que llegue desde arriba
        float topBottomY = tuberia.obtenerPIPETopBottom() - 150;
        dibujarQuad(tuberia.x, topBottomY, tuberia.ancho, 300, 0, colorR, colorG, colorB, 1);

        // Tubería inferior - MÁS ALTA para que llegue desde abajo
        float bottomTopY = tuberia.obtenerPIPEBottomTop() + 150;
        dibujarQuad(tuberia.x, bottomTopY, tuberia.ancho, 300, 0, colorR, colorG, colorB, 1);
    }

    /**
     * Dibuja los contadores de puntos durante el juego CON NÚMAROSVISIBLES.
     */
    public void dibujarContadores(int puntajeP1, int puntajeP2, int nivel, float velocidad) {
        // Panel P1 (Amarillo)
        dibujarQuad(80, 40, 140, 60, 0, 1, 0.8f, 0, 0.7f);
        dibujarNumeros(puntajeP1, 50, 25, 2.0f, 1, 1, 1); // Blanco
        
        // Panel P2 (Azul)
        dibujarQuad(ANCHO - 80, 40, 140, 60, 0, 0, 0.6f, 1, 0.7f);
        dibujarNumeros(puntajeP2, ANCHO - 110, 25, 2.0f, 1, 1, 1); // Blanco
        
        // Panel Nivel (centro)
        dibujarQuad(ANCHO / 2, 40, 120, 60, 0, 0.2f, 0.9f, 0.2f, 0.7f);
        dibujarNumeros(nivel, ANCHO / 2 - 15, 25, 2.0f, 1, 1, 1); // Blanco
        
        // Bordes para resaltar
        dibujarQuad(80, 40, 135, 55, 0, 1, 1, 1, 0.3f);
        dibujarQuad(ANCHO - 80, 40, 135, 55, 0, 1, 1, 1, 0.3f);
        dibujarQuad(ANCHO / 2, 40, 115, 55, 0, 1, 1, 1, 0.3f);
    }
    
    /**
     * Dibuja números usando bloques simples (visible).
     * Soporta números 0-999 con dígitos de tamaño 10x20 px.
     */
    private void dibujarNumeros(int numero, float x, float y, float escala, float r, float g, float b) {
        String texto = String.valueOf(numero);
        float offset = 0;
        
        for (char c : texto.toCharArray()) {
            int digito = c - '0';
            dibujarDigito(x + offset, y, digito, escala, r, g, b);
            offset += 12 * escala; // Espaciado entre dígitos
        }
    }
    
    /**
     * Dibuja UN dígito individual (0-9) usando quads.
     * Patrón simple: rectangulos que forman el dígito.
     */
    private void dibujarDigito(float x, float y, int digito, float escala, float r, float g, float b) {
        float w = 6 * escala;    // Ancho de segmento
        float h = 10 * escala;   // Alto de segmento
        float t = 2 * escala;    // Grosor de segmento
        
        // Dibujamos usando patrón de 7-segmentos simplificado
        // Cada dígito ocupa un rectángulo de w x (2*h+t*3)
        
        switch (digito) {
            case 0: // 0: arriba, arriba-der, arriba-izq, abajo, abajo-der, abajo-izq
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x + w, y + h/2, t, h/2, 0, r, g, b, 1); // Der-arriba
                dibujarQuad(x, y + h/2, t, h/2, 0, r, g, b, 1); // Izq-arriba
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Abajo
                dibujarQuad(x + w, y + h + h/2, t, h/2, 0, r, g, b, 1); // Der-abajo
                dibujarQuad(x, y + h + h/2, t, h/2, 0, r, g, b, 1); // Izq-abajo
                break;
            case 1: // 1: solo derecha
                dibujarQuad(x + w, y, t, h, 0, r, g, b, 1);
                dibujarQuad(x + w, y + h, t, h, 0, r, g, b, 1);
                break;
            case 2: // 2
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x + w, y + h/2, t, h/2, 0, r, g, b, 1); // Der-arriba
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Medio
                dibujarQuad(x, y + h + h/2, t, h/2, 0, r, g, b, 1); // Izq-abajo
                dibujarQuad(x + w/2, y + 2*h, w, t, 0, r, g, b, 1); // Abajo
                break;
            case 3: // 3
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x + w, y + h/2, t, h/2, 0, r, g, b, 1); // Der-arriba
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Medio
                dibujarQuad(x + w, y + h + h/2, t, h/2, 0, r, g, b, 1); // Der-abajo
                dibujarQuad(x + w/2, y + 2*h, w, t, 0, r, g, b, 1); // Abajo
                break;
            case 4: // 4
                dibujarQuad(x, y, t, h/2, 0, r, g, b, 1); // Izq-arriba
                dibujarQuad(x + w, y, t, h, 0, r, g, b, 1); // Der (completa)
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Medio
                break;
            case 5: // 5
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x, y + h/2, t, h/2, 0, r, g, b, 1); // Izq-arriba
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Medio
                dibujarQuad(x + w, y + h + h/2, t, h/2, 0, r, g, b, 1); // Der-abajo
                dibujarQuad(x + w/2, y + 2*h, w, t, 0, r, g, b, 1); // Abajo
                break;
            case 6: // 6
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x, y + h/2, t, h/2, 0, r, g, b, 1); // Izq-arriba
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Medio
                dibujarQuad(x + w, y + h + h/2, t, h/2, 0, r, g, b, 1); // Der-abajo
                dibujarQuad(x, y + h + h/2, t, h/2, 0, r, g, b, 1); // Izq-abajo
                dibujarQuad(x + w/2, y + 2*h, w, t, 0, r, g, b, 1); // Abajo
                break;
            case 7: // 7
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x + w, y + h/2, t, h, 0, r, g, b, 1); // Der
                dibujarQuad(x + w, y + h, t, h, 0, r, g, b, 1); // Der-abajo
                break;
            case 8: // 8 (completo)
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x + w, y + h/2, t, h/2, 0, r, g, b, 1); // Der-arriba
                dibujarQuad(x, y + h/2, t, h/2, 0, r, g, b, 1); // Izq-arriba
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Medio
                dibujarQuad(x + w, y + h + h/2, t, h/2, 0, r, g, b, 1); // Der-abajo
                dibujarQuad(x, y + h + h/2, t, h/2, 0, r, g, b, 1); // Izq-abajo
                dibujarQuad(x + w/2, y + 2*h, w, t, 0, r, g, b, 1); // Abajo
                break;
            case 9: // 9
                dibujarQuad(x + w/2, y, w, t, 0, r, g, b, 1); // Arriba
                dibujarQuad(x + w, y + h/2, t, h/2, 0, r, g, b, 1); // Der-arriba
                dibujarQuad(x, y + h/2, t, h/2, 0, r, g, b, 1); // Izq-arriba
                dibujarQuad(x + w/2, y + h, w, t, 0, r, g, b, 1); // Medio
                dibujarQuad(x + w, y + h + h/2, t, h/2, 0, r, g, b, 1); // Der-abajo
                dibujarQuad(x + w/2, y + 2*h, w, t, 0, r, g, b, 1); // Abajo
                break;
            default:
                // Dígito inválido, dibuja cuadro vacío
                dibujarQuad(x + w/2, y + h, w, h, 0, r, g, b, 0.5f);
        }
    }

    /**
     * Dibuja el fondo mejorado.
     */
    public void dibujarFondo() {
        // Cielo (ya cubierto por glClearColor)
        
        // Nube simple
        dibujarQuad(150, 100, 60, 20, 0, 1, 1, 1, 0.5f);
        dibujarQuad(200, 120, 50, 15, 0, 1, 1, 1, 0.5f);
        
        dibujarQuad(600, 150, 70, 25, 0, 1, 1, 1, 0.5f);
        dibujarQuad(650, 160, 55, 18, 0, 1, 1, 1, 0.5f);

        // Suelo (verde)
        dibujarQuad(ANCHO / 2, ALTO - 30, ANCHO, 60, 0, 0.5f, 0.7f, 0.2f, 1);
    }

    /**
     * Dibuja la pantalla de inicio CON TEXTO VISIBLE.
     */
    public void dibujarPantallaInicio() {
        // Fondo oscuro semi-transparente
        dibujarQuad(ANCHO / 2, ALTO / 2, ANCHO, ALTO, 0, 0, 0, 0, 0.5f);
        
        // Título (cuadro amarillo)
        dibujarQuad(ANCHO / 2, 80, 400, 60, 0, 1, 0.8f, 0, 1);
        dibujarNumeros(1, ANCHO / 2 - 50, 50, 3.0f, 0.2f, 0.2f, 0.2f); // "1" en gris
        
        // Cuadro principal
        dibujarQuad(ANCHO / 2, ALTO / 2 - 50, 500, 300, 0, 0.2f, 0.4f, 0.8f, 0.9f);
        
        // Panel P1 - Amarillo
        dibujarQuad(ANCHO / 2 - 150, ALTO / 2 - 80, 200, 100, 0, 1, 0.8f, 0, 0.7f);
        dibujarNumeros(1, ANCHO / 2 - 180, ALTO / 2 - 110, 2.0f, 1, 1, 1); // "1" en blanco
        
        // Panel P2 - Azul
        dibujarQuad(ANCHO / 2 + 150, ALTO / 2 - 80, 200, 100, 0, 0, 0.6f, 1, 0.7f);
        dibujarNumeros(2, ANCHO / 2 + 120, ALTO / 2 - 110, 2.0f, 1, 1, 1); // "2" en blanco
        
        // Botón Presiona ESPACIO (izq)
        dibujarQuad(ANCHO / 2 - 150, ALTO / 2 + 100, 200, 60, 0, 0.2f, 0.9f, 0.2f, 1);
        dibujarNumeros(0, ANCHO / 2 - 180, ALTO / 2 + 70, 1.5f, 0.2f, 0.2f, 0.2f);
        
        // Botón Presiona W (der)
        dibujarQuad(ANCHO / 2 + 150, ALTO / 2 + 100, 200, 60, 0, 0.2f, 0.9f, 0.2f, 1);
        dibujarNumeros(0, ANCHO / 2 + 120, ALTO / 2 + 70, 1.5f, 0.2f, 0.2f, 0.2f);
        
        // Instrucción START
        dibujarQuad(ANCHO / 2, ALTO / 2 + 200, 300, 50, 0, 1, 1, 0, 1);
    }

    /**
     * Dibuja la pantalla de game over CON PUNTUACIONES VISIBLES.
     */
    public void dibujarPantallaGameOver(int puntajeP1, int puntajeP2) {
        // Fondo oscuro semi-transparente
        dibujarQuad(ANCHO / 2, ALTO / 2, ANCHO, ALTO, 0, 0, 0, 0, 0.6f);
        
        // Título GAME OVER - Rojo brillante
        dibujarQuad(ANCHO / 2, 80, 500, 80, 0, 1, 0, 0, 1);
        
        // Panel Jugador 1 - Amarillo
        dibujarQuad(ANCHO / 2 - 150, ALTO / 2 - 80, 200, 120, 0, 1, 0.8f, 0, 0.9f);
        dibujarQuad(ANCHO / 2 - 150, ALTO / 2 - 80, 195, 115, 0, 1, 1, 0.2f, 0.3f);
        dibujarNumeros(puntajeP1, ANCHO / 2 - 180, ALTO / 2 - 110, 2.0f, 1, 1, 1); // Puntaje P1 en blanco
        
        // Panel Jugador 2 - Azul
        dibujarQuad(ANCHO / 2 + 150, ALTO / 2 - 80, 200, 120, 0, 0, 0.6f, 1, 0.9f);
        dibujarQuad(ANCHO / 2 + 150, ALTO / 2 - 80, 195, 115, 0, 0.2f, 1, 1, 0.3f);
        dibujarNumeros(puntajeP2, ANCHO / 2 + 120, ALTO / 2 - 110, 2.0f, 1, 1, 1); // Puntaje P2 en blanco
        
        // Botón REINICIAR - Verde brillante - GRANDE y NOTABLE
        dibujarQuad(ANCHO / 2, ALTO / 2 + 120, 350, 70, 0, 0.2f, 1, 0.2f, 1);
        dibujarQuad(ANCHO / 2, ALTO / 2 + 120, 340, 60, 0, 0.1f, 0.8f, 0.1f, 0.5f);
        dibujarNumeros(1, ANCHO / 2 - 30, ALTO / 2 + 90, 2.5f, 1, 1, 1); // "1" para REINICIAR
        
        // Botón ESC - Rojo
        dibujarQuad(ANCHO / 2, ALTO / 2 + 220, 150, 50, 0, 1, 0, 0, 1);
    }

    /**
     * Limpia los recursos de OpenGL.
     */
    public void limpiar() {
        GL30.glDeleteVertexArrays(VAO);
        GL15.glDeleteBuffers(VBO);
        GL15.glDeleteBuffers(EBO);
        GL20.glDeleteProgram(shaderProgram);
    }

    /**
     * Compila un shader.
     */
    private int compileShader(String source, int type) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("Error compilando shader: " + GL20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    /**
     * Carga un recurso del classpath.
     */
    private String loadResource(String path) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(path)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (Exception e) {
            System.err.println("Error cargando recurso: " + path);
            e.printStackTrace();
        }
        return result.toString();
    }
}
