package br.imd.sistemabancario.service;

import br.imd.sistemabancario.model.Conta;
import br.imd.sistemabancario.repository.ContaRepository;
import java.util.List;
import java.util.Objects;
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

    private Conta buscarContaPorNumero(int numero) {
        return carregarContas().stream()
            .filter(c -> c.getNumero() == numero)
            .findAny()
            .orElse(null);
    }

    private List<Conta> carregarContas() {
        return contaRepository.findAll();
    }

    private void salvarContas(List<Conta> contas) {
        contaRepository.saveAll(contas);
    }
}
