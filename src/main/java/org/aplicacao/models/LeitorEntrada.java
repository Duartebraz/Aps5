package org.aplicacao.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeitorEntrada {

    // Classe auxiliar para guardar tudo que extraímos do arquivo
    public static class DadosSimulacao {
        public int nProc;
        public int quantum;
        public int tTroca;
        public List<Processo> processos = new ArrayList<>();
    }

    public static DadosSimulacao lerArquivo(String caminhoArquivo) {
        DadosSimulacao dados = new DadosSimulacao();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;

            // 1. Lê a primeira linha com os parâmetros do sistema [cite: 40]
            linha = br.readLine();
            if (linha != null) {
                String[] parametros = linha.split(",");
                dados.nProc = Integer.parseInt(parametros[0].trim());
                dados.quantum = Integer.parseInt(parametros[1].trim());
                dados.tTroca = Integer.parseInt(parametros[2].trim());
            }

            // 2. Lê exatamente a quantidade de processos (nProc) [cite: 46]
            // Isso evita ler as legendas textuais que o professor coloca no final do arquivo
            for (int i = 0; i < dados.nProc; i++) {
                linha = br.readLine();
                if (linha != null) {
                    String[] infoProc = linha.split(",");

                    int id = Integer.parseInt(infoProc[0].trim());
                    int tch = Integer.parseInt(infoProc[1].trim());
                    int prio = Integer.parseInt(infoProc[2].trim());
                    int tcpu = Integer.parseInt(infoProc[3].trim());

                    // Cria o objeto Processo e adiciona na lista
                    Processo p = new Processo(id, tch, prio, tcpu);
                    dados.processos.add(p);
                }
            }

            System.out.println("Arquivo lido com sucesso! " + dados.processos.size() + " processos carregados.");

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro de formatação no arquivo. Verifique se há apenas números separados por vírgula nas linhas de dados.");
        }

        return dados;
    }

}
