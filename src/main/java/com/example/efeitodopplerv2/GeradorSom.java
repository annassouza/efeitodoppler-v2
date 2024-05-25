package com.example.efeitodopplerv2;
import javax.sound.sampled.*;
import java.io.*;
import java.util.List;

public class GeradorSom {

    public static void gerarSom(List<Double> yValores, int duracaoSegundos) throws LineUnavailableException, IOException {
        float sampleRate = 48000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        int totalSamples = (int) sampleRate * duracaoSegundos;

        byte[] buffer = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            int index = i % yValores.size();
            int sampleValue = (int)(yValores.get(index) * 32767);  // Escala Y para o máximo valor de 16 bits
            buffer[2 * i] = (byte)(sampleValue & 0xFF);
            buffer[2 * i + 1] = (byte)((sampleValue >> 8) & 0xFF);
        }

        File file = new File("output.wav");
        ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream = new AudioInputStream(input, format, totalSamples);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        audioInputStream.close();

        System.out.println("Arquivo de som 'output.wav' gerado com sucesso.");
    }
}
