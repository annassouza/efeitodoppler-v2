/*
        package com.example.efeitodopplerv2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
//teste
public class SimuladorCalculos {
    private double distanciaInicialX;
    private double distanciaY = 3; // Distância fixa no eixo Y
    private double frequencia;
    private double potencia;
    private double velocidadeFonte;
    private final double velocidadeSom = 343; // velocidade fixa do som
    private int codFonte;

    public void Simulador(double distanciaInicialX, double frequencia, double potencia, double velocidadeFonte, String login) {
        this.distanciaInicialX = distanciaInicialX;
        this.frequencia = frequencia;
        this.potencia = potencia;
        this.velocidadeFonte = velocidadeFonte;
        System.out.println("Simulador inicializado com valores: ");
        System.out.println("distanciaInicialX: " + distanciaInicialX);
        System.out.println("frequencia: " + frequencia);
        System.out.println("potencia: " + potencia);
        System.out.println("velocidadeFonte: " + velocidadeFonte);

        // Chama a stored procedure para inserir os dados na tabela fontes
        inserirFonte(frequencia, potencia, velocidadeFonte);

        // Chama a stored procedure para inserir a simulação
        inserirSimulacao(distanciaInicialX, codFonte, login);
    }

    private void inserirFonte(double frequenciaPadrao, double potencia, double velocidade) {
        Connection connection = ConexaoBanco.getConnection();

        if (connection != null) {
            try {
                System.out.println("Inserindo fonte no banco de dados...");
                String sql = "{call sp_inserir_fontes(?, ?, ?, ?)}";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setDouble(1, frequenciaPadrao);
                statement.setDouble(2, potencia);
                statement.setDouble(3, velocidade);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.executeUpdate();
                codFonte = statement.getInt(4);

                System.out.println("Fonte inserida no banco de dados com cod_fonte: " + codFonte);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        } else {
            System.out.println("Falha ao conectar ao banco de dados.");
        }
    }

    private void inserirSimulacao(double distanciaInicial, int codFonte, String login) {
        Connection connection = ConexaoBanco.getConnection();

        if (connection != null) {
            try {
                System.out.println("Inserindo simulação no banco de dados...");
                String sql = "{call sp_inserir_simulacao(?, ?, ?, ?, ?)}";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setDouble(1, 2 * Math.abs(distanciaInicial / velocidadeFonte)); // duração
                statement.setDouble(2, distanciaInicial);
                statement.setNull(3, Types.VARBINARY); // arquivo_mov_serializado
                statement.setInt(4, codFonte);
                statement.setString(5, login);

                statement.executeUpdate();

                System.out.println("Simulação inserida no banco de dados.");

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        } else {
            System.out.println("Falha ao conectar ao banco de dados.");
        }
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

    public List<Double> simular(String login) throws Exception {
        System.out.println("Iniciando simulação...");
        double distanciaX = this.distanciaInicialX;
        int duracaoSegundos = (int) (2 * Math.abs(distanciaInicialX / velocidadeFonte));
        double tempo = 0;
        double incrementoTempo = 0.00002083;
        ArrayList<Double> arrayValores = new ArrayList<>();
        ArrayList<Double> arrayFreq = new ArrayList<>();
        ArrayList<Double> arrayTempo = new ArrayList<>();
        ArrayList<Double> arrayAmpli = new ArrayList<>();
        ArrayList<Double> arrayDist = new ArrayList<>();

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

        StringBuilder resultados = new StringBuilder();

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

                // Armazenar no banco de dados
                inserirEfeito((int) tempoAtual, freqAtual, ampAtual, valorYAtual);
            } else {
                // Caso não tenha dados suficientes para o último segundo, exibir uma mensagem padrão
                resultados.append(String.format("Tempo: %.5f s, Dados insuficientes\n", tempoAtual));
            }
        }

        GeradorSom.gerarSom(arrayValores, duracaoSegundos);

        System.out.println(resultados.toString());
        return arrayValores;
    }

    private void inserirEfeito(int instanteSegundos, double frequenciaPercebida, double intensidade, double aproximacaoSeno) {
        Connection connection = ConexaoBanco.getConnection();

        if (connection != null) {
            try {
                System.out.println("Inserindo efeito no banco de dados...");
                String sql = "{call sp_inserir_efeitos(?, ?, ?, ?)}";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setInt(1, instanteSegundos);
                statement.setDouble(2, frequenciaPercebida);
                statement.setDouble(3, intensidade);
                statement.setDouble(4, aproximacaoSeno);

                statement.executeUpdate();
                System.out.println("Dados inseridos no banco de dados: " +
                        "Tempo: " + instanteSegundos + "s, Frequência: " + frequenciaPercebida +
                        "Hz, Amplitude: " + intensidade + ", Y: " + aproximacaoSeno);

            }

 */


package com.example.efeitodopplerv2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SimuladorCalculos {
    private double distanciaInicialX;
    private double distanciaY = 3; // Distância fixa no eixo Y
    private double frequencia;
    private double potencia;
    private double velocidadeFonte;
    private final double velocidadeSom = 343; // velocidade fixa do som
    private int codFonte;
// NO BANCO ESTÁ FALTANDO ARMAZENAR A DISTANCIA INICIAL
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

        // Chama a stored procedure para inserir os dados na tabela fontes
        inserirFonte(frequencia, potencia, velocidadeFonte);
    }

    private void inserirFonte(double frequenciaPadrao, double potencia, double velocidade) {
        Connection connection = ConexaoBanco.getConnection();

        if (connection != null) {
            try {
                System.out.println("Inserindo fonte no banco de dados...");
                String sql = "{call sp_inserir_fontes(?, ?, ?, ?)}";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setDouble(1, frequenciaPadrao);
                statement.setDouble(2, potencia);
                statement.setDouble(3, velocidade);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.executeUpdate();
                codFonte = statement.getInt(4);

                System.out.println("Fonte inserida no banco de dados com cod_fonte: " + codFonte);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        } else {
            System.out.println("Falha ao conectar ao banco de dados.");
        }
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

    public List<Double> simular() throws Exception {
        System.out.println("Iniciando simulação...");
        double distanciaX = this.distanciaInicialX;
        int duracaoSegundos = (int) (2 * Math.abs(distanciaInicialX / velocidadeFonte));
        double tempo = 0;
        double incrementoTempo = 0.00002083;
        ArrayList<Double> arrayValores = new ArrayList<>();
        ArrayList<Double> arrayFreq = new ArrayList<>();
        ArrayList<Double> arrayTempo = new ArrayList<>();
        ArrayList<Double> arrayAmpli = new ArrayList<>();
        ArrayList<Double> arrayDist = new ArrayList<>();

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

        StringBuilder resultados = new StringBuilder();

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

                // Armazenar no banco de dados
                inserirEfeito((int) tempoAtual, freqAtual, ampAtual, valorYAtual);
            } else {
                // Caso não tenha dados suficientes para o último segundo, exibir uma mensagem padrão
                resultados.append(String.format("Tempo: %.5f s, Dados insuficientes\n", tempoAtual));
            }
        }

        GeradorSom.gerarSom(arrayValores, duracaoSegundos);

        System.out.println(resultados.toString());
        return arrayValores;
    }

    private void inserirEfeito(int instanteSegundos, double frequenciaPercebida, double intensidade, double aproximacaoSeno) {
        Connection connection = ConexaoBanco.getConnection();

        if (connection != null) {
            try {
                System.out.println("Inserindo efeito no banco de dados...");
                String sql = "{call sp_inserir_efeitos(?, ?, ?, ?)}";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setInt(1, instanteSegundos);
                statement.setDouble(2, frequenciaPercebida);
                statement.setDouble(3, intensidade);
                statement.setDouble(4, aproximacaoSeno);

                statement.executeUpdate();
                System.out.println("Dados inseridos no banco de dados: " +
                        "Tempo: " + instanteSegundos + "s, Frequência: " + frequenciaPercebida +
                        "Hz, Amplitude: " + intensidade + ", Y: " + aproximacaoSeno);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        } else {
            System.out.println("Falha ao conectar ao banco de dados.");
        }
    }
}
