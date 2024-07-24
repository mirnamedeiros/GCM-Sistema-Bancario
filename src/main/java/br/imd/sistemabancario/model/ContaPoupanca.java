package br.imd.sistemabancario.model;

public class ContaPoupanca extends Conta {

    public ContaPoupanca(int numero, double saldo) {
        super(numero, saldo);
    }

    public void renderJuros(double taxaJuros) {
        double juros = this.getSaldo() * (taxaJuros / 100);
        this.setSaldo(this.getSaldo() + juros);
    }
}
