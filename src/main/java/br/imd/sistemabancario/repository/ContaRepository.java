package br.imd.sistemabancario.repository;

import br.imd.sistemabancario.model.Conta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ContaRepository {
    public List<Conta> findAll() {
        final var contas = new ArrayList<Conta>();
        try {
            final var file = getFile();
            final var br = new BufferedReader(new FileReader(file));
            final var gson = new Gson();
            // Converte o conteúdo do arquivo para um array de contas
            Conta[] contaArray = gson.fromJson(br, Conta[].class);
            br.close();
            contas.addAll(Arrays.asList(contaArray));
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo");
        }
        return contas;
    }

    public void saveAll(List<Conta> contas) {
        try {
            // Cria um objeto Gson para converter objetos em JSON
            final var gson = new GsonBuilder().setPrettyPrinting().create();
            final var jsonContas = gson.toJson(contas);
            final var file = getFile();
            final var fw = new FileWriter(file);
            final var pw = new PrintWriter(fw);

            pw.println(jsonContas);

            pw.close();
        } catch (IOException e) {
            System.out.println("Erro ao cadastrar conta");
        }
    }

    private File getFile() throws IOException {
        return new ClassPathResource("bd.txt").getFile();
    }
}
