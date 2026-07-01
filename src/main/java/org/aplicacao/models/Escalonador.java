package org.aplicacao.models;

import java.util.*;

public class Escalonador {

    // ==========================================
    // ALGORITMO 1: ROUND ROBIN
    // ==========================================
    public static void simularRoundRobin(List<Processo> processosOriginais, int quantum, int tTroca) {
        // Clonamos os processos para não afetar a lista original
        List<Processo> processos = clonarLista(processosOriginais);
        Queue<Processo> filaProntos = new LinkedList<>();
        List<String> linhaTempo = new ArrayList<>();

        int tempoAtual = 0;
        int chaveamentos = 0;
        int tempoTrocaTotal = 0;
        int processosFinalizados = 0;
        int nProc = processos.size();

        Processo emExecucao = null;
        int quantumRestante = 0;

        // O simulador roda enquanto houver processos pendentes ou na fila
        while (processosFinalizados < nProc) {

            // 1. Verifica se novos processos chegaram neste milissegundo
            // Se houver mais de um, a lista original já deve estar ordenada por ID para desempatar corretamente
            for (Processo p : processos) {
                if (p.getTempoChegada() == tempoAtual) {
                    filaProntos.add(p);
                }
            }

            // 2. Se não há ninguém executando, pega o próximo da fila
            if (emExecucao == null && !filaProntos.isEmpty()) {
                emExecucao = filaProntos.poll();
                quantumRestante = Math.min(quantum, emExecucao.getTempoRestante());
            }

            // 3. Executa o processo se houver um na CPU
            if (emExecucao != null) {
                linhaTempo.add(String.valueOf(emExecucao.getId()));
                emExecucao.executarUmMilissegundo(tempoAtual);
                quantumRestante--;

                // Caso A: O processo terminou de executar
                if (emExecucao.isFinalizado()) {
                    emExecucao.setTempoConclusao(tempoAtual + 1);
                    processosFinalizados++;

                    // Se ainda existem processos para rodar, aplica a troca de contexto imediatamente
                    if (processosFinalizados < nProc) {
                        for (int t = 0; t < tTroca; t++) {
                            linhaTempo.add("Escalonador");
                        }
                        tempoAtual += tTroca;
                        chaveamentos++;
                        tempoTrocaTotal += tTroca;
                        // Checa se chegou alguém durante a troca de contexto
                        for (int tCheck = tempoAtual - tTroca + 1; tCheck <= tempoAtual; tCheck++) {
                            for (Processo p : processos) {
                                if (p.getTempoChegada() == tCheck) filaProntos.add(p);
                            }
                        }
                    }
                    emExecucao = null;
                }
                // Caso B: O quantum expirou, mas o processo não terminou
                else if (quantumRestante == 0) {
                    // Só troca se houver alguém esperando, senão ele ganha outro quantum
                    if (!filaProntos.isEmpty()) {
                        // Aplica a troca de contexto
                        for (int t = 0; t < tTroca; t++) {
                            linhaTempo.add("Escalonador");
                        }
                        tempoAtual += tTroca;
                        chaveamentos++;
                        tempoTrocaTotal += tTroca;

                        // Checa chegadas durante a troca
                        for (int tCheck = tempoAtual - tTroca + 1; tCheck <= tempoAtual; tCheck++) {
                            for (Processo p : processos) {
                                if (p.getTempoChegada() == tCheck) filaProntos.add(p);
                            }
                        }

                        // Coloca o processo atual de volta no fim da fila e libera a CPU
                        filaProntos.add(emExecucao);
                        emExecucao = null;
                    } else {
                        // Renova o quantum se a fila estiver vazia
                        quantumRestante = Math.min(quantum, emExecucao.getTempoRestante());
                    }
                }
            } else {
                // CPU Ociosa (nenhum processo chegou ainda)
                linhaTempo.add("Ocioso");
            }

            tempoAtual++;
        }

        exibirRelatorio("ROUND ROBIN", processos, linhaTempo, chaveamentos, tempoTrocaTotal, tempoAtual);
    }

    // ==========================================
    // ALGORITMO 2: BASEADO EM PRIORIDADE (PREEMPTIVO)
    // ==========================================
    public static void simularPrioridadePreemptivo(List<Processo> processosOriginais, int tTroca) {
        List<Processo> processos = clonarLista(processosOriginais);

        // PriorityQueue configurada estritamente com as regras do professor:
        // 1º critério: Menor valor numérico de prioridade = Maior prioridade
        // 2º critério (Desempate): Menor ID ganha
        PriorityQueue<Processo> filaProntos = new PriorityQueue<>((p1, p2) -> {
            if (p1.getPrioridade() != p2.getPrioridade()) {
                return Integer.compare(p1.getPrioridade(), p2.getPrioridade());
            }
            return Integer.compare(p1.getId(), p2.getId());
        });

        List<String> linhaTempo = new ArrayList<>();
        int tempoAtual = 0;
        int chaveamentos = 0;
        int tempoTrocaTotal = 0;
        int processosFinalizados = 0;
        int nProc = processos.size();

        Processo emExecucao = null;

        while (processosFinalizados < nProc) {
            boolean chegouMaisPrioritario = false;

            // 1. Verifica se novos processos chegaram
            for (Processo p : processos) {
                if (p.getTempoChegada() == tempoAtual) {
                    filaProntos.add(p);
                    // Se o processo que chegou é mais prioritário que o atual (ou se há empate com menor ID), sinaliza preempção
                    if (emExecucao != null) {
                        if (p.getPrioridade() < emExecucao.getPrioridade() ||
                                (p.getPrioridade() == emExecucao.getPrioridade() && p.getId() < emExecucao.getId())) {
                            chegouMaisPrioritario = true;
                        }
                    }
                }
            }

            // 2. Lógica de Preempção (Interrupção por prioridade)
            if (emExecucao != null && chegouMaisPrioritario) {
                // Realiza a troca de contexto
                for (int t = 0; t < tTroca; t++) {
                    linhaTempo.add("Escalonador");
                }
                tempoAtual += tTroca;
                chaveamentos++;
                tempoTrocaTotal += tTroca;

                // Checa se alguém chegou durante essa troca de contexto
                for (int tCheck = tempoAtual - tTroca + 1; tCheck <= tempoAtual; tCheck++) {
                    for (Processo p : processos) {
                        if (p.getTempoChegada() == tCheck) filaProntos.add(p);
                    }
                }

                filaProntos.add(emExecucao); // Devolve o interrompido para a fila
                emExecucao = null; // Libera a CPU para pegar o novo topo da PriorityQueue
            }

            // 3. Se a CPU está livre, pega o topo da fila de prioridades
            if (emExecucao == null && !filaProntos.isEmpty()) {
                emExecucao = filaProntos.poll();
            }

            // 4. Execução na CPU
            if (emExecucao != null) {
                linhaTempo.add(String.valueOf(emExecucao.getId()));
                emExecucao.executarUmMilissegundo(tempoAtual);

                // Se o processo terminou
                if (emExecucao.isFinalizado()) {
                    emExecucao.setTempoConclusao(tempoAtual + 1);
                    processosFinalizados++;

                    if (processosFinalizados < nProc) {
                        for (int t = 0; t < tTroca; t++) {
                            linhaTempo.add("Escalonador");
                        }
                        tempoAtual += tTroca;
                        chaveamentos++;
                        tempoTrocaTotal += tTroca;

                        for (int tCheck = tempoAtual - tTroca + 1; tCheck <= tempoAtual; tCheck++) {
                            for (Processo p : processos) {
                                if (p.getTempoChegada() == tCheck) filaProntos.add(p);
                            }
                        }
                    }
                    emExecucao = null;
                }
            } else {
                linhaTempo.add("Ocioso");
            }

            tempoAtual++;
        }

        exibirRelatorio("PRIORIDADE PREEMPTIVO", processos, linhaTempo, chaveamentos, tempoTrocaTotal, tempoAtual);
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    private static List<Processo> clonarLista(List<Processo> lista) {
        List<Processo> listaClonada = new ArrayList<>();
        for (Processo p : lista) {
            listaClonada.add(p.clone());
        }
        return listaClonada;
    }

    private static void exibirRelatorio(String algoritmo, List<Processo> processos, List<String> linhaTempo, int chaveamentos, int tempoTroca, int tempoTotal) {
        double somaRetorno = 0;
        for (Processo p : processos) {
            somaRetorno += p.getTempoRetorno();
        }
        double tempoMedioRetorno = somaRetorno / processos.size();
        double overhead = (double) tempoTroca / tempoTotal;

        System.out.println("\n===== RESULTADO: " + algoritmo + " =====");
        System.out.printf("Tempo médio de retorno: %.2f ms\n", tempoMedioRetorno);
        System.out.println("Número de chaveamento de processos: " + chaveamentos);
        System.out.printf("Overhead de chaveamento: %.4f (%.2f%%)\n", overhead, overhead * 100);
        System.out.println("Tempo total de execução: " + tempoTotal + " ms");
        System.out.println("Linha do tempo da CPU: " + String.join(" -> ", linhaTempo));
    }
}
