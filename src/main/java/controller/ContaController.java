package controller;

import java.util.ArrayList;
import model.Conta;
import model.ContaBonus;
import model.ContaPoupanca;

public class ContaController {

    private ArrayList<Conta> contas = new ArrayList();

    public ContaController() {
        popularContas();
    }

    public void popularContas() {
        contas.add(new Conta(123));
        contas.get(0).setSaldo(568);
        contas.add(new Conta(456));
        contas.get(1).setSaldo(59);
        contas.add(new Conta(789));
        contas.get(2).setSaldo(15);
        contas.add(new ContaPoupanca(159));
        contas.get(3).setSaldo(1000);
        contas.add(new ContaBonus(444));
        contas.get(4).setSaldo(65);
        contas.add(new ContaBonus(555));
        contas.get(5).setSaldo(1900);
    }

    public void cadastrarConta(int numero, int tipo) {
        if (tipo == 1) {
            contas.add(new Conta(numero));
        } else if (tipo == 2) {
            contas.add(new ContaBonus(numero));
        } else if (tipo == 3) {
            contas.add(new ContaPoupanca(numero));
        }

    }

    public boolean verificarContaExistente(int numero) {
        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                return true;
            }
        }
        return false;
    }

    public String consultarSaldo(int numero) {

        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                if (conta instanceof ContaBonus) {
                    System.out.println("O bônus da conta é: " + ((ContaBonus) conta).getBonus());
                }
                return "O saldo da conta é: " + conta.getSaldo();
            }
        }

        return "Conta não existe!";

    }

    public void debitarConta(int numero, double valor) {
        if (valorInvalido(valor)) {
            return;
        }
        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                final var novoSaldo = conta.getSaldo() - valor;
                if (novoSaldo < 0) {
                    System.out.println("Saldo insuficiente");
                    break;
                }
                conta.setSaldo(conta.getSaldo() - valor);
                if (conta instanceof ContaBonus) {
                    ((ContaBonus) conta).setBonus((int) (((ContaBonus) conta).getBonus() + (int) valor / 100));
                }
                break;
            }
        }
    }

    public void creditarConta(int numero, double valor) {

        if (valorInvalido(valor)) {
            return;
        }
        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                conta.setSaldo(conta.getSaldo() + valor);
                break;
            }
        }
    }

    public void transferir(int numeroOrigem, int numeroDestino, double valorTransferencia) {
        if (valorInvalido(valorTransferencia)) {
            return;
        }
        Conta contaOrigem = null;
        Conta contaDestino = null;

        for (Conta conta : contas) {
            if (conta.getNumero() == numeroOrigem) {
                contaOrigem = conta;
            } else if (conta.getNumero() == numeroDestino) {
                contaDestino = conta;
            }
        }

        if (contaOrigem != null && contaDestino != null) {
            final var novoSaldoOrigem = contaOrigem.getSaldo() - valorTransferencia;
            if (novoSaldoOrigem < 0) {
                System.out.println("Saldo insuficiente");
            } else {
                contaOrigem.setSaldo(contaOrigem.getSaldo() - valorTransferencia);
                contaDestino.setSaldo(contaDestino.getSaldo() + valorTransferencia);
                if (contaDestino instanceof ContaBonus) {
                    int bonus_total = (int) ((ContaBonus) contaDestino).getBonus() + (int) valorTransferencia / 150;
                    ((ContaBonus) contaDestino).setBonus(bonus_total);
                }
            }
        } else {
            System.out.println("Conta de origem ou destino não encontrada");
        }
    }

    private boolean valorInvalido(double valor) {
        final var valorNegativo = valor < 0;
        if (valorNegativo) {
            System.out.println("Valor não pode ser negativo");
        }
        return valorNegativo;
    }

    public void contabilizarJuros(double taxaJuros) {
        for (Conta conta : contas) {
            if (conta instanceof ContaPoupanca) {
                ((ContaPoupanca) conta).renderJuros(taxaJuros);
            }
        }
    }
}
