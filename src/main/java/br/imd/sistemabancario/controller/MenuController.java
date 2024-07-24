package br.imd.sistemabancario.controller;

import br.imd.sistemabancario.exception.BadRequestException;
import br.imd.sistemabancario.service.ContaService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class MenuController {

    private static final double CONTA_ZERADA = 0.0;
    private final ContaService contaService;

    Scanner scanner = new Scanner(System.in);
    int opcao;

    public MenuController(ContaService contaService) {
        this.contaService = contaService;
    }

    public void run() {
        do {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Criar Conta");
            System.out.println("2. Consultar Saldo");
            System.out.println("3. Debitar");
            System.out.println("4. Creditar");
            System.out.println("5. Transferir");
            System.out.println("6. Render Juros");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            try {
                switch (opcao) {
                    case 1:
                        System.out.println("Tipo da conta:");
                        System.out.println("1 - Conta Normal");
                        System.out.println("2 - Conta Bônus");
                        System.out.println("3 - Conta Poupança");
                        System.out.print("Escolha uma opção: ");
                        int tipoConta = scanner.nextInt();

                        boolean contaCriada = false;
                        do {
                            System.out.println("Digite o número da nova conta:");
                            int numeroConta = scanner.nextInt();
                            try {
                                // tipoConta = 1 -> Conta Normal   ||
                                // tipoConta = 2 -> Conta Bonus    ||
                                // tipoConta = 3 -> Conta Poupanca
                                var saldo = CONTA_ZERADA;
                                if (tipoConta == 1 || tipoConta == 3) {
                                    System.out.println("Digite o saldo inicial da conta:");
                                    saldo = scanner.nextDouble();
                                }
                                contaService.cadastrarConta(numeroConta, tipoConta, saldo);
                                System.out.println("Conta criada com sucesso!");
                                contaCriada = true;
                            } catch (BadRequestException e) {
                                System.err.println("Erro: " + e.getMessage());
                            }
                        } while (!contaCriada);
                        break;
                    case 2:
                        System.out.println("Digite o número da conta:");
                        int numeroContaConsulta = scanner.nextInt();
                        contaService.consultarSaldo(numeroContaConsulta).ifPresentOrElse(saldo ->
                                        System.out.println("O saldo da conta é: " + saldo)
                                , () -> System.out.println("Conta não existe!"));
                        break;
                    case 3:
                        System.out.println("Digite o número da conta:");
                        int numeroContaDebito = scanner.nextInt();
                        System.out.println("Digite o valor a ser debitado:");
                        double valorDebito = scanner.nextDouble();
                        contaService.debitarConta(numeroContaDebito, valorDebito);
                        break;
                    case 4:
                        System.out.println("Digite o número da conta:");
                        int numero = scanner.nextInt();
                        System.out.println("Digite o número valor:");
                        double valor = scanner.nextDouble();
                        contaService.creditarConta(numero, valor);
                        break;
                    case 5:
                        System.out.println("Digite o número da conta de origem:");
                        int numeroOrigem = scanner.nextInt();
                        System.out.println("Digite o número da conta de destino:");
                        int numeroDestino = scanner.nextInt();
                        System.out.println("Digite o valor a ser transferido:");
                        double valorTransferencia = scanner.nextDouble();
                        contaService.transferir(numeroOrigem, numeroDestino, valorTransferencia);
                        break;
                    case 6:
                        System.out.println("Digite a taxa de juros:");
                        double taxaJuros = scanner.nextDouble();
                        contaService.contabilizarJuros(taxaJuros);
                        System.out.println("Juros contabilizados com sucesso!");
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
            } catch (BadRequestException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
        scanner.close();
    }
}
