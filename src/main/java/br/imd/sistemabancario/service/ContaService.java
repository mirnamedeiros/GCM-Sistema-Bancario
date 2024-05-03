package br.imd.sistemabancario.service;

import br.imd.sistemabancario.model.Conta;
import br.imd.sistemabancario.repository.ContaRepository;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public void cadastrarConta(int numero) {
        final var contas = carregarContas();
        final var novaConta = new Conta(numero);
        contas.add(novaConta);
        salvarContas(contas);
    }

    public String consultarSaldo(int numero) {
        final var conta = buscarContaPorNumero(numero);
        if (Objects.nonNull(conta)) {
            return "O saldo da conta eh " + conta.getSaldo();
        }
        return "Conta não existe!";
    }

    public void debitarConta(int numero, double valor) {
        final var conta = buscarContaPorNumero(numero);
        if (Objects.nonNull(conta)) {
            conta.setSaldo(conta.getSaldo() - valor);
            salvarConta(conta);
            System.out.println("Conta debitada com sucesso!");
        } else {
            System.out.println("Conta não existe!");
        }
    }

    public boolean verificarContaExistente(int numero) {
        return Objects.nonNull(buscarContaPorNumero(numero));
    }

    private Conta buscarContaPorNumero(int numero) {
        return carregarContas().stream()
            .filter(c -> c.getNumero() == numero)
            .findAny()
            .orElse(null);
    }

    private Set<Conta> carregarContas() {
        return contaRepository.findAll();
    }

    private void salvarContas(Set<Conta> contas) {
        contaRepository.saveAll(contas);
    }

    private void salvarConta(Conta conta) {
        final var contas = contaRepository.findAll();
        contas.removeIf(c -> c.getNumero() == conta.getNumero());
        contas.add(conta);
        contaRepository.saveAll(contas);
    }
}
