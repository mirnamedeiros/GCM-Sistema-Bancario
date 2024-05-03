import controller.ContaController;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o número da conta:");
        int numero = scanner.nextInt();
        System.out.println(ContaController.consultarSaldo(numero));
//        System.out.println("Conta cadastrada com sucesso!");
        scanner.close();
    }
}