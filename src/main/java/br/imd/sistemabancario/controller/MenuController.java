package br.imd.sistemabancario.controller;

import br.imd.sistemabancario.service.ContaService;
import java.util.Scanner;
import org.springframework.stereotype.Controller;

@Controller
public class MenuController {

    private final ContaService contaService;
    private final Scanner scanner;

    public MenuController(ContaService contaService) {
        this.contaService = contaService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            menu();
            final var opcao = scanner.nextInt();
            switch (opcao) {
                case 1 -> cadastrarConta();
                case 2 -> consultarSaldo();
                case 3 -> realizarSaque();
                case 4 -> realizarDeposito();
                case 5 -> realizarTransferencia();
                case 0 -> System.exit(0);
                default -> System.out.println("Opção inválida!");
            }
        }

    }

    private void cadastrarConta() {
        var contaCriada = false;
        do {
            System.out.println("Digite o número da nova conta:");
            int numeroConta = scanner.nextInt();
            if (!contaService.verificarContaExistente(numeroConta)) {
                contaService.cadastrarConta(numeroConta);
                System.out.println("Conta criada com sucesso!");
                contaCriada = true;
            } else {
                System.out.println("Este número de conta já está em uso. Por favor, escolha outro número.");
            }
        } while (!contaCriada);
    }

    public boolean verificarContaExistente(int numero) {
        return contaService.verificarContaExistente(numero);
    }

    private void consultarSaldo() {
        System.out.print("Digite o número da conta:");
        final var numero = scanner.nextInt();
        System.out.println(contaService.consultarSaldo(numero));
    }

    private void realizarSaque() {
        System.out.println("Digite o número da conta:");
        int numeroContaDebito = scanner.nextInt();
        System.out.println("Digite o valor a ser debitado:");
        double valorDebito = scanner.nextDouble();
        contaService.debitarConta(numeroContaDebito, valorDebito);
    }

    private void realizarDeposito() {

    }

    private void realizarTransferencia() {

    }

    public void menu() {
        System.out.print(
            """
                Bem-vindo ao sistema bancário!
                1 - Cadastrar conta
                2 - Consultar saldo
                3 - Realizar saque
                4 - Realizar depósito
                5 - Realizar transferência
                0 - Sair
                Digite o número da operação que deseja realizar:""");
    }
}
