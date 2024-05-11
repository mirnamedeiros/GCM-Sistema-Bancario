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

public class ContaController {

    public ContaController() {}

    public void cadastrarConta(int numero, double saldoInicial) {
        List<Conta> contas = carregarContas();
        Conta novaConta = new Conta(numero, saldoInicial);
        contas.add(novaConta);
        salvarContas(contas);
    }

    public boolean verificarContaExistente(int numero) {
        List<Conta> contas = carregarContas();
        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                return true;
            }
        }
        return false;
    }

    private List<Conta> carregarContas() {
        List<Conta> contas = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("bd.txt"));
            Gson gson = new Gson();
            // Converte o conteúdo do arquivo para um array de contas
            Conta[] contaArray = gson.fromJson(br, Conta[].class);
            br.close();

            contas.addAll(Arrays.asList(contaArray));
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo");
        }
        return contas;
    }

    private void salvarContas(List<Conta> contas) {
        try {
            // Cria um objeto Gson para converter objetos em JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonContas = gson.toJson(contas);

            FileWriter fw = new FileWriter("bd.txt");
            PrintWriter pw = new PrintWriter(fw);

            pw.println(jsonContas);

            pw.close();
        } catch (IOException e) {
            System.out.println("Erro ao cadastrar conta");
        }
    }

    public String consultarSaldo(int numero) {

        List<Conta> contas = carregarContas();
        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                return "O saldo da conta é " + conta.getSaldo();
            }
        }

        return "Conta não existe!";

    }

    public void debitarConta(int numero, double valor) {
        if (valorInvalido(valor)) {
            return;
        }
        List<Conta> contas = carregarContas();

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
        salvarContas(contas);
    }

    public void creditarConta(int numero, double valor) {
        if (valorInvalido(valor)) {
            return;
        }
        List<Conta> contas = carregarContas();

        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                conta.setSaldo(conta.getSaldo() + valor);
                break;
            }
        }
        salvarContas(contas);
    }

    public void transferir(int numeroOrigem, int numeroDestino, double valorTransferencia) {
        if (valorInvalido(valorTransferencia)) {
            return;
        }
        List<Conta> contas = carregarContas();
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
                salvarContas(contas);
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
}
