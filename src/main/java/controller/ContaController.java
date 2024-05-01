package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Conta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContaController {

    public static void cadastrarConta(int numero) {
        List<Conta> contas = carregarContas();
        Conta novaConta = new Conta(numero);
        contas.add(novaConta);
        salvarContas(contas);
    }

    private static List<Conta> carregarContas() {
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

    private static void salvarContas(List<Conta> contas) {
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
}
