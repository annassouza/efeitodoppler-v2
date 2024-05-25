package com.example.efeitodopplerv2;

import java.util.ArrayList;

public class SimuladorCalculos {
    private double distanciaInicialX;
    private double distanciaY = 3; // Distância fixa no eixo Y
    private double frequencia;
    private double potencia;
    private double velocidadeFonte;
    private final double velocidadeSom = 343; // velocidade fixa do som

    public void Simulador(double distanciaInicialX, double frequencia, double potencia, double velocidadeFonte) {
        this.distanciaInicialX = distanciaInicialX;
        this.frequencia = frequencia;
        this.potencia = potencia;
        this.velocidadeFonte = velocidadeFonte;
        System.out.println("Simulador inicializado com valores: ");
        System.out.println("distanciaInicialX: " + distanciaInicialX);
        System.out.println("frequencia: " + frequencia);
        System.out.println("potencia: " + potencia);
        System.out.println("velocidadeFonte: " + velocidadeFonte);
    }

    public double calcularDistanciaTotal(double distanciaX) {
        return Math.sqrt(Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2));
    }

    public double calcularFrequenciaPercebida(double distanciaX) {
        double vfEfetivo = distanciaX < 0 ? -this.velocidadeFonte : this.velocidadeFonte;
        return this.frequencia * (this.velocidadeSom / (this.velocidadeSom - vfEfetivo));
    }

    public double calcularAmplitude(double distanciaX) {
        double distanciaTotal = calcularDistanciaTotal(distanciaX);
        return Math.sqrt(this.potencia / (4 * Math.PI * Math.pow(distanciaTotal, 2)));
    }

    public String simular() throws Exception {
        System.out.println("Iniciando simulação..."); // Adicionado para depuração
        double distanciaX = this.distanciaInicialX;
        int duracaoSegundos = (int) (2 * Math.abs(distanciaInicialX / velocidadeFonte));
        double tempo = 0;
        double incrementoTempo = 0.00002083;
        ArrayList<Double> arrayValores = new ArrayList<>();
        ArrayList<Double> arrayFreq = new ArrayList<>();
        ArrayList<Double> arrayTempo = new ArrayList<>();
        ArrayList<Double> arrayAmpli = new ArrayList<>();
        ArrayList<Double> arrayDist = new ArrayList<>();
        StringBuilder resultados = new StringBuilder();

        while (tempo <= duracaoSegundos) {
            if (Math.abs(distanciaX) >= 0.01) {
                double freqAtual = calcularFrequenciaPercebida(distanciaX);
                double ampAtual = calcularAmplitude(distanciaX);
                double x = 2 * Math.PI * freqAtual * tempo;

                // Normalização de x para o intervalo [0, 2π]
                x = x % (2 * Math.PI);
                if (x < 0) {
                    x += 2 * Math.PI;
                }

                // Cálculo da série de Maclaurin para seno com precisão de 10^-15
                double valorY = 0.0;
                double term = x; // Primeiro termo da série
                valorY += term;
                double xSquared = Math.pow(x, 2);
                for (int k = 1; k <= 8; k++) {
                    term *= -xSquared / ((2 * k) * (2 * k + 1));
                    valorY += term;
                }

                valorY *= ampAtual; // Multiplica pela amplitude para obter o valorY correto

                arrayValores.add(valorY);
                arrayFreq.add(freqAtual);
                arrayAmpli.add(ampAtual);
                arrayDist.add(calcularDistanciaTotal(distanciaX));
            }

            arrayTempo.add(tempo);
            tempo += incrementoTempo;
            distanciaX -= this.velocidadeFonte * incrementoTempo;

            if (distanciaX <= 0 && this.velocidadeFonte > 0) {
                this.velocidadeFonte = -this.velocidadeFonte;
            }
        }

        int intervaloTempo = 1; // Intervalo de tempo desejado
        int indiceTempo = 0; // Índice correspondente ao tempo 1, 2, 3, ...

        for (double tempoAtual = 1; tempoAtual <= duracaoSegundos; tempoAtual += intervaloTempo) {
            // Encontrar o índice correspondente ao tempo atual ou o mais próximo
            while (indiceTempo < arrayTempo.size() && arrayTempo.get(indiceTempo) < tempoAtual) {
                indiceTempo++;
            }

            // Verificar se o índice está dentro dos limites do ArrayList
            if (indiceTempo < arrayTempo.size()) {
                double freqAtual = arrayFreq.get(indiceTempo);
                double ampAtual = arrayAmpli.get(indiceTempo);
                double valorYAtual = arrayValores.get(indiceTempo);
                double distAtual = arrayDist.get(indiceTempo);

                resultados.append(String.format("Tempo: %.5f s, Frequência Percebida: %.2f Hz, Amplitude: %.4f, Y: %.16f, Distância: %.2f m\n",
                        tempoAtual, freqAtual, ampAtual, valorYAtual, distAtual));
            } else {
                // Caso não tenha dados suficientes para o último segundo, exibir uma mensagem padrão
                resultados.append(String.format("Tempo: %.5f s, Dados insuficientes\n", tempoAtual));
            }
        }

        GeradorSom.gerarSom(arrayValores, duracaoSegundos);

        return resultados.toString();
    }
}
