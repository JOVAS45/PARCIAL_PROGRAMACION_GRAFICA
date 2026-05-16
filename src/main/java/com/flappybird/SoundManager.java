package com.flappybird;

import javax.sound.sampled.*;

/**
 * Gestor de sonidos para el juego Flappy Bird.
 * Genera y reproduce sonidos simples usando javax.sound.sampled.
 */
public class SoundManager {
    private Clip clipSalto;
    private Clip clipPunto;
    private Clip clipGameOver;

    public SoundManager() {
        try {
            clipSalto = generarSonido(440, 100);        // La (A4), 100ms
            clipPunto = generarSonido(880, 150);        // La octava arriba, 150ms
            clipGameOver = generarSonido(220, 300);     // La octava abajo, 300ms
        } catch (Exception e) {
            System.err.println("Error inicializando sonidos: " + e.getMessage());
        }
    }

    /**
     * Genera un sonido sine wave de frecuencia y duración especificadas.
     */
    private Clip generarSonido(int frecuencia, int duracionMs) throws Exception {
        int sampleRate = 44100;
        int numMuestras = (duracionMs * sampleRate) / 1000;
        byte[] audioData = new byte[numMuestras * 2]; // 16-bit samples

        // Generar onda senoidal
        for (int i = 0; i < numMuestras; i++) {
            double t = (double) i / sampleRate;
            double valor = Math.sin(2 * Math.PI * frecuencia * t);
            
            // Aplicar fade-out para evitar clicks
            double fadeOut = Math.max(0, 1.0 - (double) i / numMuestras);
            valor *= fadeOut;
            
            // Convertir a 16-bit PCM
            short sample = (short) (valor * 32767);
            audioData[i * 2] = (byte) (sample & 0xFF);
            audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        AudioFormat formato = new AudioFormat(sampleRate, 16, 1, true, false);
        AudioInputStream ais = new AudioInputStream(
            new java.io.ByteArrayInputStream(audioData),
            formato,
            numMuestras
        );

        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        return clip;
    }

    /**
     * Reproduce el sonido de salto.
     */
    public void reproducirSalto() {
        reproducirClip(clipSalto);
    }

    /**
     * Reproduce el sonido de punto anotado.
     */
    public void reproducirPunto() {
        reproducirClip(clipPunto);
    }

    /**
     * Reproduce el sonido de game over.
     */
    public void reproducirGameOver() {
        reproducirClip(clipGameOver);
    }

    /**
     * Reproduce un clip desde el inicio.
     */
    private void reproducirClip(Clip clip) {
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Limpia los recursos de sonido.
     */
    public void limpiar() {
        if (clipSalto != null) clipSalto.close();
        if (clipPunto != null) clipPunto.close();
        if (clipGameOver != null) clipGameOver.close();
    }
}
