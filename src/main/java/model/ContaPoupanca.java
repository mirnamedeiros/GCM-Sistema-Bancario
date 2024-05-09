package model;

public class ContaPoupanca extends Conta{

    public ContaPoupanca(int numero) {
        super(numero);
    }

    public void renderJuros(double taxaJuros) {
        double juros = this.getSaldo() * (taxaJuros / 100);
        this.setSaldo(this.getSaldo() + juros);
    }
}
