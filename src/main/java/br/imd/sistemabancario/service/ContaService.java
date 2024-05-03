package br.imd.sistemabancario.service;


import br.imd.sistemabancario.model.Conta;
import br.imd.sistemabancario.model.ContaBonus;
import br.imd.sistemabancario.model.ContaPoupanca;
import br.imd.sistemabancario.repository.ContaRepository;
import org.springframework.stereotype.Service;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public void cadastrarConta(int numero, int tipo) {
        if (tipo == 1) {
            contaRepository.save(new Conta(numero));
        } else if (tipo == 2) {
            contaRepository.save(new ContaBonus(numero));
        } else if (tipo == 3) {
            contaRepository.save(new ContaPoupanca(numero));
        }
    }

    public boolean verificarContaExistente(int numero) {
        return contaRepository.existsByNumero(numero);
    }

    public void consultarSaldo(int numero) {
        contaRepository.findByNumero(numero).ifPresentOrElse(conta -> {
            if (conta instanceof ContaBonus) {
                System.out.println("O bônus da conta é: " + ((ContaBonus) conta).getBonus());
            }
            System.out.println("O saldo da conta é: " + conta.getSaldo());
        }, () -> System.out.println("Conta não existe!"));
    }

    public void debitarConta(int numero, double valor) {
        if (valorInvalido(valor)) {
            return;
        }

        contaRepository.findByNumero(numero).ifPresent(conta -> {
            final var novoSaldo = conta.getSaldo() - valor;
            if (novoSaldo < 0) {
                System.out.println("Saldo insuficiente");
            } else {
                conta.setSaldo(conta.getSaldo() - valor);
                if (conta instanceof ContaBonus) {
                    ((ContaBonus) conta).setBonus((int) (((ContaBonus) conta).getBonus() + (int) valor / 100));
                }
                contaRepository.save(conta);
            }
        });
    }

    public void creditarConta(int numero, double valor) {
        if (valorInvalido(valor)) {
            return;
        }
        contaRepository.findByNumero(numero)
                .ifPresent(conta -> {
                    conta.setSaldo(conta.getSaldo() + valor);
                    contaRepository.save(conta);
                });
    }

    public void transferir(int numeroOrigem, int numeroDestino, double valorTransferencia) {
        if (valorInvalido(valorTransferencia)) {
            return;
        }
        final var contaOrigem = contaRepository.findByNumero(numeroOrigem);
        final var contaDestino = contaRepository.findByNumero(numeroDestino);

        if (contaOrigem.isPresent() && contaDestino.isPresent()) {
            final var origem = contaOrigem.get();
            final var destino = contaDestino.get();
            final var novoSaldoOrigem = origem.getSaldo() - valorTransferencia;
            if (novoSaldoOrigem < 0) {
                System.out.println("Saldo insuficiente");
            } else {
                origem.setSaldo(origem.getSaldo() - valorTransferencia);
                destino.setSaldo(destino.getSaldo() + valorTransferencia);
                if (destino instanceof ContaBonus) {
                    int bonus_total = (int) ((ContaBonus) destino).getBonus() + (int) valorTransferencia / 150;
                    ((ContaBonus) destino).setBonus(bonus_total);
                }
                contaRepository.save(origem);
                contaRepository.save(destino);
            }
        } else {
            System.out.println("Conta de origem ou destino não encontrada");
        }
    }

    private boolean valorInvalido(double valor) {
        final var isValorNegativo = valor < 0;
        if (isValorNegativo) {
            System.out.println("Valor não pode ser negativo");
        }
        return isValorNegativo;
    }

    public void contabilizarJuros(double taxaJuros) {
        contaRepository.findAll().stream()
                .filter(conta -> conta instanceof ContaPoupanca)
                .map(conta -> {
                    ((ContaPoupanca) conta).renderJuros(taxaJuros);
                    return conta;
                })
                .forEach(contaRepository::save);
    }
}
