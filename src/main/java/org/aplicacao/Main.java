package org.aplicacao;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import org.aplicacao.models.Escalonador;
import org.aplicacao.models.LeitorEntrada;
import org.aplicacao.models.Processo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        // Nomes dos arquivos definidos para a simulação
        String arquivoEntrada = "EntradaProcessos.txt";
        String arquivoSaida = "SaidaProcessos.txt";

        System.out.println("Iniciando a leitura do arquivo: " + arquivoEntrada + " ...");

        // 1. Lê os dados do arquivo de entrada usando a classe que criamos
        LeitorEntrada.DadosSimulacao dados = LeitorEntrada.lerArquivo(arquivoEntrada);

        // Trava de segurança: verifica se o arquivo foi lido corretamente
        if (dados == null || dados.processos.isEmpty()) {
            System.err.println("Nenhum processo foi carregado. Verifique se o arquivo de entrada existe e está formatado corretamente.");
            return;
        }

        // 2. Redirecionar a saída para gerar o arquivo .txt exigido pelo professor
        try {
            // Salva a saída original (a tela do terminal) para não perdermos o acesso a ela
            PrintStream consoleOriginal = System.out;

            // Cria o arquivo de saída
            PrintStream arquivoPrintStream = new PrintStream(new File(arquivoSaida));

            // Avisa ao Java: "A partir de agora, todo System.out.println vai para o arquivo!"
            System.setOut(arquivoPrintStream);

            // ====================================================
            // 3. EXECUÇÃO DAS SIMULAÇÕES
            // ====================================================

            System.out.println("RELATÓRIO DE SIMULAÇÃO DE ESCALONAMENTO - APS 05");
            System.out.println("Parâmetros do sistema -> Quantum: " + dados.quantum + "ms | Tempo de Troca: " + dados.tTroca + "ms\n");

            // Executa o Round Robin
            Escalonador.simularRoundRobin(dados.processos, dados.quantum, dados.tTroca);

            // Dá um espaço no arquivo
            System.out.println("\n--------------------------------------------------\n");

            // Executa o Baseado em Prioridade Preemptivo
            Escalonador.simularPrioridadePreemptivo(dados.processos, dados.tTroca);

            // ====================================================

            // 4. Devolve a saída para o terminal do computador e avisa que terminou
            System.setOut(consoleOriginal);
            System.out.println("Simulação concluída com sucesso!");
            System.out.println("O relatório completo foi gerado no arquivo: " + arquivoSaida);
            System.out.println("Lembre-se de preparar os arquivos de teste manual para a apresentação em laboratório!");

        } catch (FileNotFoundException e) {
            System.err.println("Erro ao criar o arquivo de saída: " + e.getMessage());
        }
    }
}