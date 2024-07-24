package br.imd.sistemabancario.service;


import br.imd.sistemabancario.controller.dto.ContaDTO;
import br.imd.sistemabancario.exception.BadRequestException;
import br.imd.sistemabancario.exception.NotFoundException;
import br.imd.sistemabancario.model.Conta;
import br.imd.sistemabancario.model.ContaBonus;
import br.imd.sistemabancario.model.ContaPoupanca;
import br.imd.sistemabancario.repository.ContaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContaService {
    private static final double LIMITE_NEGATIVO = -1_000.0;

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public void cadastrarConta(int numero, int tipo, double saldo) {
        if (contaRepository.existsByNumero(numero)) {
            throw new BadRequestException("Este número de conta já está em uso. Por favor, escolha outro número.");
        }

        if (numero <= 0) {
            throw new BadRequestException("O número da conta deve ser um valor positivo.");
        }

        if (saldo < 0) {
            throw new BadRequestException("O saldo da conta não pode ser negativo.");
        }

        if (tipo != 1 && tipo != 2 && tipo != 3) {
            throw new BadRequestException("Tipo de conta inválido. Os valores válidos são 1, 2 e 3.");
        }
        if (tipo == 1) {
            contaRepository.save(new Conta(numero, saldo));
        } else if (tipo == 2) {
            contaRepository.save(new ContaBonus(numero, saldo));
        } else if (tipo == 3) {
            contaRepository.save(new ContaPoupanca(numero, saldo));
        }
    }

    public ContaDTO consultarDados(int numero) {
        Conta conta = contaRepository.findByNumero(numero)
                .orElseThrow(NotFoundException::new);

        int tipo = 1;
        if (conta instanceof ContaBonus) {
            tipo = 2;
        } else if (conta instanceof ContaPoupanca) {
            tipo = 3;
        }

        return new ContaDTO(conta.getNumero(), tipo, conta.getSaldo());
    }

    public boolean verificarContaExistente(int numero) {
        return contaRepository.existsByNumero(numero);
    }

    public Optional<Double> consultarSaldo(int numero) {
        return contaRepository.findByNumero(numero)
                .map(conta -> {
                    if (conta instanceof ContaBonus contaBonus) {
                        return contaBonus.getSaldo() + contaBonus.getBonus();
                    }
                    return conta.getSaldo();
                });
    }

    public void debitarConta(int numero, double valor) {
        validarValor(valor);

        contaRepository.findByNumero(numero).ifPresentOrElse(conta -> {
            final var novoSaldo = conta.getSaldo() - valor;
            validaNovoSaldo(conta, novoSaldo);
            conta.setSaldo(conta.getSaldo() - valor);
            if (conta instanceof ContaBonus contaBonus) {
                contaBonus.setBonus((int) (contaBonus.getBonus() + valor / 100));
            }
            contaRepository.save(conta);
        }, () -> {
            throw new NotFoundException("Conta de origem ou destino não encontrada");
        });
    }

    public void creditarConta(int numero, double valor) {
        validarValor(valor);

        contaRepository.findByNumero(numero)
                .ifPresentOrElse(conta -> {
                    conta.setSaldo(conta.getSaldo()); //  + valor
                    contaRepository.save(conta);
                }, () -> {
                    throw new NotFoundException("Conta de origem ou destino não encontrada");
                });
    }

    public void transferir(int numeroOrigem, int numeroDestino, double valorTransferencia) {
        validarValor(valorTransferencia);
        final var contaOrigem = contaRepository.findByNumero(numeroOrigem);
        final var contaDestino = contaRepository.findByNumero(numeroDestino);

        if (contaOrigem.isEmpty() || contaDestino.isEmpty()) {
            throw new NotFoundException("Conta de origem ou destino não encontrada");
        }
        final var origem = contaOrigem.get();
        final var destino = contaDestino.get();
        final var novoSaldoOrigem = origem.getSaldo() - valorTransferencia;
        final var novoSaldoDestino = destino.getSaldo() + valorTransferencia;

        validaNovoSaldo(origem, novoSaldoOrigem);

        origem.setSaldo(novoSaldoOrigem);
        destino.setSaldo(novoSaldoDestino);
        if (destino instanceof ContaBonus contaBonus) {
            final var bonusTotal = (int) (contaBonus.getBonus() + valorTransferencia / 150);
            contaBonus.setBonus(bonusTotal);
        }
        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    private void validarValor(double valor) {
        final var isValorNegativo = valor < 0;
        if (isValorNegativo) {
            throw new BadRequestException("Valor não pode ser negativo");
        }
    }

    public void contabilizarJuros(double taxaJuros) {
        contaRepository.findAll().stream()
                .filter(ContaPoupanca.class::isInstance)
                .map(ContaPoupanca.class::cast)
                .peek(conta -> conta.renderJuros(taxaJuros))
                .forEach(contaRepository::save);
    }

    private void validaNovoSaldo(Conta conta, double novoSaldo) {
        var saldoInvalido = novoSaldo < 0;
        if (!(conta instanceof ContaPoupanca)) {
            saldoInvalido = novoSaldo < LIMITE_NEGATIVO;
        }
        if (saldoInvalido) {
            throw new BadRequestException("Saldo insuficiente");
        }
    }
}
