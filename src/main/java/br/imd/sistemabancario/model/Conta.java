package br.imd.sistemabancario.model;

public class Conta {

    private int numero;
    private double saldo;

    public Conta(int numero) {
        this.numero = numero;
        this.saldo = 0.0;
    }

    public Conta(int numero, double saldo) {
        this.numero = numero;
        this.saldo = saldo;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conta conta = (Conta) o;
        return numero == conta.numero;
    }

    @Override
    public int hashCode() {
        return numero;
    }
}
