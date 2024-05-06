import controller.ContaController;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ContaController controller = new ContaController();

        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Criar Conta");
            System.out.println("2. Consultar Saldo");
            System.out.println("3. Debitar");
            System.out.println("4. Creditar");
            System.out.println("5. Transferir");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    boolean contaCriada = false;
                    do {
                        System.out.println("Digite o número da nova conta:");
                        int numeroConta = scanner.nextInt();
                        if (!controller.verificarContaExistente(numeroConta)) {
                            controller.cadastrarConta(numeroConta);
                            System.out.println("Conta criada com sucesso!");
                            contaCriada = true;
                        } else {
                            System.out.println("Este número de conta já está em uso. Por favor, escolha outro número.");
                        }
                    } while (!contaCriada);
                    break;
                case 2:
                    System.out.println("Digite o número da conta:");
                    int numeroContaConsulta = scanner.nextInt();
                    System.out.println(controller.consultarSaldo(numeroContaConsulta));
                    break;
                case 3:
                    System.out.println("Digite o número da conta:");
                    int numeroContaDebito = scanner.nextInt();
                    System.out.println("Digite o valor a ser debitado:");
                    double valorDebito = scanner.nextDouble();
                    controller.debitarConta(numeroContaDebito, valorDebito);
                    break;
                case 4:
                    System.out.println("Digite o número da conta:");
                    int numero = scanner.nextInt();
                    System.out.println("Digite o número valor:");
                    double valor = scanner.nextDouble();
                    ContaController.creditarConta(numero, valor);
                    break;
                case 5:
                    // Lógica para transferir
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
        scanner.close();
    }
}