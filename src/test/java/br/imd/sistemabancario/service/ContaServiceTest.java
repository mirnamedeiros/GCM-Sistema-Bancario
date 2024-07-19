/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package br.imd.sistemabancario.service;

import br.imd.sistemabancario.exception.BadRequestException;
import br.imd.sistemabancario.exception.NotFoundException;
import br.imd.sistemabancario.model.Conta;
import br.imd.sistemabancario.model.ContaBonus;
import br.imd.sistemabancario.model.ContaPoupanca;
import br.imd.sistemabancario.repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @InjectMocks
    private ContaService contaService;

    @Spy
    private ContaRepository contaRepository;

    @Test
    @DisplayName("Valor negativo para debitar")
    public void testDebitarValorNegativo() {
        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.debitarConta(1, -100));

        assertThat(exception).hasMessage("Valor não pode ser negativo");

        verify(contaRepository, never()).findByNumero(1);
    }

    @Test
    @DisplayName("Debito com saldo insuficiente")
    public void testDebitarSaldoInsuficiente() {
        final var conta = new ContaPoupanca(1, 95);
        final var valorADebitar = 100;

        when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.debitarConta(conta.getNumero(), valorADebitar));

        assertThat(exception).hasMessage("Saldo insuficiente");

        verify(contaRepository).findByNumero(conta.getNumero());
    }

    @Test
    @DisplayName("Debito concluido conta normal")
    public void testDebitarSuccessNormal() {
        final var contaCaptor = ArgumentCaptor.forClass(Conta.class);
        final var conta = new Conta(1, 250);
        final var valor = 100;
        final var novoSaldo = conta.getSaldo() - valor;

        when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        contaService.debitarConta(conta.getNumero(), valor);

        verify(contaRepository).save(contaCaptor.capture());

        assertThat(contaCaptor.getValue())
                .matches(c -> c.getSaldo() == novoSaldo)
                .matches(c -> c.getNumero() == conta.getNumero());

    }

    @Test
    @DisplayName("Debito concluido conta bonus")
    public void testDebitarSuccessBonus() {
        final var conta = new ContaBonus(1, 250);
        final var valor = 249.99;

        when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        contaService.debitarConta(conta.getNumero(), valor);

        conta.setSaldo((conta.getSaldo() - valor) + valor / 100);
        verify(contaRepository).save(conta);

    }

    @Test
    @DisplayName("Transferir valor invalido")
    public void testTransferirValorInvalido() {
        double valor = -1;

        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.transferir(1, 2, valor));

        assertThat(exception).hasMessage("Valor não pode ser negativo");

        verify(contaRepository, never()).findByNumero(1);

    }

    @Test
    @DisplayName("Transferir com conta origem nao existente")
    public void testTransferirContaOrigemInexistente() {
        final var co = new Conta(1, 100);
        final var cd = new Conta(2, 90);
        final var valor = 10;

        when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.empty());
        when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));

        final var exception = assertThrows(NotFoundException.class,
                () -> contaService.transferir(co.getNumero(), cd.getNumero(), valor));

        assertThat(exception).hasMessage("Conta de origem ou destino não encontrada");
    }

    @Test
    @DisplayName("Transferir com conta destino nao existente")
    public void testTransferirContaDestinoInexistente() {
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2, 90);
        double valor = 10;

        when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co));
        when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.empty());

        final var exception = assertThrows(NotFoundException.class,
                () -> contaService.transferir(co.getNumero(), cd.getNumero(), valor));

        assertThat(exception).hasMessage("Conta de origem ou destino não encontrada");
    }

    @Test
    @DisplayName("Transferir com conta origem e destino nao existente")
    public void testTransferirContaOrigemDestinoInexistente() {
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2, 90);
        double valor = 10;

        when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.empty());
        when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.empty());

        final var exception = assertThrows(NotFoundException.class,
                () -> contaService.transferir(co.getNumero(), cd.getNumero(), valor));

        assertThat(exception).hasMessage("Conta de origem ou destino não encontrada");
    }

    @Test
    @DisplayName("Saldo da transferencia da origem insuficiente")
    public void testTransferirContaNovoSaldoInsuficiente() {
        Conta co = new ContaPoupanca(1, 100);
        Conta cd = new ContaPoupanca(2, 90);
        double valor = 101;

        when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co));
        when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));

        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.transferir(co.getNumero(), cd.getNumero(), valor));

        assertThat(exception).hasMessage("Saldo insuficiente");
    }

    @Test
    @DisplayName("Transferencia concluida")
    public void testTransferirSuccess() {
        final var contaCaptor = ArgumentCaptor.forClass(Conta.class);
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2, 90);
        double valor = 99.9;

        final var saldoExperadoOrigem = co.getSaldo() - valor;
        final var saldoExperadoDestino = cd.getSaldo() + valor;

        when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co));
        when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));

        contaService.transferir(co.getNumero(), cd.getNumero(), valor);

        verify(contaRepository, times(2)).save(contaCaptor.capture());

        assertThat(contaCaptor.getAllValues().get(0))
                .matches(c -> c.getSaldo() == saldoExperadoOrigem)
                .matches(c -> c.getNumero() == co.getNumero());

        assertThat(contaCaptor.getAllValues().get(1))
                .matches(c -> c.getSaldo() == saldoExperadoDestino)
                .matches(c -> c.getNumero() == cd.getNumero());
    }

    @Test
    @DisplayName("Transferencia concluida em conta bonus")
    public void testTransferirSuccessContaBonus() {
        final var contaCaptor = ArgumentCaptor.forClass(Conta.class);
        final var co = new Conta(1, 100);
        final var cd = new ContaBonus(2, 90);
        final var valor = 99.9;

        final var saldoExperadoOrigem = co.getSaldo() - valor;
        final var saldoExperadoDestino = cd.getSaldo() + valor;
        int bonus = (int) (cd.getBonus() + (int) valor / 150);

        when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co));
        when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));

        contaService.transferir(co.getNumero(), cd.getNumero(), valor);

        verify(contaRepository, times(2)).save(contaCaptor.capture());

        assertThat(contaCaptor.getAllValues().get(0))
                .isExactlyInstanceOf(Conta.class)
                .matches(c -> c.getSaldo() == saldoExperadoOrigem)
                .matches(c -> c.getNumero() == co.getNumero());

        assertThat(contaCaptor.getAllValues().get(1))
                .isExactlyInstanceOf(ContaBonus.class)
                .matches(c -> c.getSaldo() == saldoExperadoDestino)
                .matches(c -> c.getNumero() == cd.getNumero())
                .matches(c -> ((ContaBonus) c).getBonus() == bonus);
    }
}
