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
            System.out.println("6. Render Juros");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

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
                        if (!controller.verificarContaExistente(numeroConta)) {
                            // tipoConta = 1 -> Conta Normal   ||
                            // tipoConta = 2 -> Conta Bonus    ||
                            // tipoConta = 3 -> Conta Poupanca
                            controller.cadastrarConta(numeroConta, tipoConta);
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
                    controller.creditarConta(numero, valor);
                    break;
                case 5:
                    System.out.println("Digite o número da conta de origem:");
                    int numeroOrigem = scanner.nextInt();
                    System.out.println("Digite o número da conta de destino:");
                    int numeroDestino = scanner.nextInt();
                    System.out.println("Digite o valor a ser transferido:");
                    double valorTransferencia = scanner.nextDouble();
                    controller.transferir(numeroOrigem, numeroDestino, valorTransferencia);
                    break;
                case 6:
                    System.out.println("Digite a taxa de juros:");
                    double taxaJuros = scanner.nextDouble();
                    controller.contabilizarJuros(taxaJuros);
                    System.out.println("Juros contabilizados com sucesso!");
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