package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Conta;
import model.ContaBonus;

public class ContaController {
    
    private ArrayList<Conta> contas = new ArrayList();

    public ContaController() {
        popularContas();
    }
    
    public void popularContas(){
        contas.add(new Conta(123));
        contas.add(new Conta(456));
        contas.add(new Conta(789));
        contas.add(new Conta(159));
        contas.add(new ContaBonus(444));
        contas.add(new ContaBonus(555));
    }

    public void cadastrarConta(int numero, int tipo) {
        if(tipo == 1){
            contas.add(new Conta(numero));
        } else if (tipo == 2) {
            contas.add(new ContaBonus(numero));
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
                return "O saldo da conta é " + conta.getSaldo();
            }
        }

        return "Conta não existe!";

    }

    public void debitarConta(int numero, double valor) {

        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                final var novoSaldo = conta.getSaldo() - valor;
                if (novoSaldo < 0) {
                    System.out.println("Saldo insuficiente");
                    break;
                }
                conta.setSaldo(conta.getSaldo() - valor);
                break;
            }
        }
    }

    public void creditarConta(int numero, double valor) {
        
        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                conta.setSaldo(conta.getSaldo() + valor);
                break;
            }
        }
    }

    public void transferir(int numeroOrigem, int numeroDestino, double valorTransferencia) {
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
            }
        } else {
            System.out.println("Conta de origem ou destino não encontrada");
        }
    }
}
