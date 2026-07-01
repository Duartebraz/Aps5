package org.aplicacao.models;

public class Processo implements Cloneable {
    private int  id;
    private int tempoChegada;
    private int prioridade;
    private int tempoTotalCpu;

    private int tempoRestante;
    private int tempoConclusao;
    private int tempoPrimeiraExecucao;

    public Processo(int id, int tempoChegada, int prioridade, int tempoTotalCpu){
        this.id = id;
        this.tempoChegada = tempoChegada;
        this.prioridade = prioridade;
        this.tempoTotalCpu = tempoTotalCpu;
        this.tempoRestante = tempoTotalCpu; // No início, o tempo restante é o tempo total
        this.tempoPrimeiraExecucao = -1; // Mostrar que não começou
        this.tempoConclusao= 0;
    }

    // Método para simular a execução de 1 unidade de tempo na CPU
    public void executarUmMilissegundo(int tempoAtual){
        if(this.tempoPrimeiraExecucao == -1){
            this.tempoPrimeiraExecucao=tempoAtual;
        }
        this.tempoRestante--;
    }

    public int getTempoRetorno(){
        return this.tempoConclusao - this.tempoChegada;
    }

    public int getId() { return id; }
    public int getTempoChegada() { return tempoChegada; }
    public int getPrioridade() { return prioridade; }
    public int getTempoTotalCpu() { return tempoTotalCpu; }

    public int getTempoRestante() { return tempoRestante; }
    public boolean isFinalizado() { return tempoRestante <= 0; }

    public int getTempoConclusao() { return tempoConclusao; }
    public void setTempoConclusao(int tempoConclusao) { this.tempoConclusao = tempoConclusao; }

    // Método essencial para podermos resetar o processo entre a simulação do Round Robin e a de Prioridade
    @Override
    public Processo clone() {
        try {
            return (Processo) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Processo(this.id, this.tempoChegada, this.prioridade, this.tempoTotalCpu);
        }
    }

}
