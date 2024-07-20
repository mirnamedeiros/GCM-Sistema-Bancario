/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package br.imd.sistemabancario.service;

import br.imd.sistemabancario.controller.dto.ContaDTO;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    // TESTES EM CADASTRAR CONTA
    @Test
    @DisplayName("Cadastrar Conta Padrão")
    void testCadastrarContaTipo1() {
        
        int numeroConta = 1;
        int tipoConta = 1; // Tipo padrão
        double saldoInicial = 100.0;

        contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial);
        
        ArgumentCaptor<Conta> contaCaptor = ArgumentCaptor.forClass(Conta.class);
        verify(contaRepository, times(1)).save(contaCaptor.capture());

        Conta contaSalva = contaCaptor.getValue();
        assertThat(contaSalva)
                .isNotNull()
                .isExactlyInstanceOf(Conta.class)
                .matches(c -> c.getNumero() == numeroConta)
                .matches(c -> c.getSaldo() == saldoInicial);
    }

    @Test
    @DisplayName("Cadastrar Conta Bonus")
    void testCadastrarContaTipo2() {
        
        int numeroConta = 2;
        int tipoConta = 2; // Tipo bônus
        double saldoInicial = 200.0;

        contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial);

        ArgumentCaptor<ContaBonus> contaCaptor = ArgumentCaptor.forClass(ContaBonus.class);
        verify(contaRepository, times(1)).save(contaCaptor.capture());

        ContaBonus contaSalva = contaCaptor.getValue();
        assertThat(contaSalva)
                .isNotNull()
                .isExactlyInstanceOf(ContaBonus.class)
                .matches(c -> c.getNumero() == numeroConta)
                .matches(c -> c.getSaldo() == saldoInicial)
                .matches(c -> c.getBonus() == 10);
    }

    @Test
    @DisplayName("Cadastrar Conta Poupança")
    void testCadastrarContaTipo3() {
        
        int numeroConta = 3;
        int tipoConta = 3; // Tipo poupança
        double saldoInicial = 300.0;
        
        contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial);
        
        ArgumentCaptor<ContaPoupanca> contaCaptor = ArgumentCaptor.forClass(ContaPoupanca.class);
        verify(contaRepository, times(1)).save(contaCaptor.capture());

        ContaPoupanca contaSalva = contaCaptor.getValue();
        assertThat(contaSalva)
                .isNotNull()
                .isExactlyInstanceOf(ContaPoupanca.class)
                .matches(c -> c.getNumero() == numeroConta)
                .matches(c -> c.getSaldo() == saldoInicial);
    }

    @Test
    @DisplayName("Cadastrar Conta com Saldo Zero")
    void testCadastrarContaComSaldoZero() {
        
        int numeroConta = 4;
        int tipoConta = 1; // Tipo padrão
        double saldoInicial = 0.0;

        contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial);

        ArgumentCaptor<Conta> contaCaptor = ArgumentCaptor.forClass(Conta.class);
        verify(contaRepository, times(1)).save(contaCaptor.capture());

        Conta contaSalva = contaCaptor.getValue();
        assertThat(contaSalva)
                .isNotNull()
                .isExactlyInstanceOf(Conta.class)
                .matches(c -> c.getNumero() == numeroConta)
                .matches(c -> c.getSaldo() == saldoInicial);
    }

    @Test
    @DisplayName("Cadastrar Conta com Saldo Negativo")
    void testCadastrarContaComSaldoNegativo() {
        
        int numeroConta = 5;
        int tipoConta = 2; // Tipo bônus
        double saldoInicial = -50.0;

        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial));

        assertThat(exception).hasMessage("O saldo da conta não pode ser negativo.");
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cadastrar Conta com Tipo Inválido")
    void testCadastrarContaComTipoInvalido() {
        
        int numeroConta = 6;
        int tipoConta = 4; // Tipo inválido
        double saldoInicial = 100.0;

        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial));

        assertThat(exception).hasMessage("Tipo de conta inválido. Os valores válidos são 1, 2 e 3.");
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cadastrar Conta com Número Negativo")
    void testCadastrarContaComNumeroNegativo() {
        
        int numeroConta = -1;
        int tipoConta = 1; // Tipo padrão
        double saldoInicial = 100.0;

        
        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial));

        assertThat(exception).hasMessage("O número da conta deve ser um valor positivo.");
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cadastrar Conta com Saldo Alto")
    void testCadastrarContaComSaldoAlto() {
        
        int numeroConta = 7;
        int tipoConta = 3; // Tipo poupança
        double saldoInicial = 1_000_000.0;

        contaService.cadastrarConta(numeroConta, tipoConta, saldoInicial);

        ArgumentCaptor<ContaPoupanca> contaCaptor = ArgumentCaptor.forClass(ContaPoupanca.class);
        verify(contaRepository, times(1)).save(contaCaptor.capture());

        ContaPoupanca contaSalva = contaCaptor.getValue();
        assertThat(contaSalva)
                .isNotNull()
                .isExactlyInstanceOf(ContaPoupanca.class)
                .matches(c -> c.getNumero() == numeroConta)
                .matches(c -> c.getSaldo() == saldoInicial);
    }

    // TESTES EM CONSULTAR SALDO
    @Test
    @DisplayName("Consultar saldo de conta normal")
    public void testConsultarSaldoContaNormal() {
        Conta conta = new Conta(1, 500.0);

        when(contaRepository.findByNumero(1)).thenReturn(Optional.of(conta));

        Optional<Double> saldo = contaService.consultarSaldo(1);

        assertTrue(saldo.isPresent());
        assertEquals(500.0, saldo.get());
    }

    @Test
    @DisplayName("Consultar saldo de conta bônus")
    public void testConsultarSaldoContaBonus() {
        ContaBonus contaBonus = new ContaBonus(2, 300.0);

        when(contaRepository.findByNumero(2)).thenReturn(Optional.of(contaBonus));

        Optional<Double> saldo = contaService.consultarSaldo(2);

        assertTrue(saldo.isPresent());
        assertEquals(310.0, saldo.get());
    }

    @Test
    @DisplayName("Consultar saldo de conta não existente")
    public void testConsultarSaldoContaNaoExistente() {
        when(contaRepository.findByNumero(3)).thenReturn(Optional.empty());

        Optional<Double> saldo = contaService.consultarSaldo(3);

        assertFalse(saldo.isPresent());
    }

    // TESTES EM CONSULTAR CONTA
    @Test
    @DisplayName("Consultar Dados Conta Normal")
    void testConsultarDadosContaNormal() {
        Conta conta = new Conta(1, 200.0);
        when(contaRepository.findByNumero(1)).thenReturn(Optional.of(conta));

        ContaDTO contaDTO = contaService.consultarDados(1);

        assertThat(contaDTO)
                .isNotNull()
                .matches(dto -> dto.numero() == 1)
                .matches(dto -> dto.tipo() == 1) // Tipo padrão
                .matches(dto -> dto.saldo() == 200.0);
    }

    @Test
    @DisplayName("Consultar Dados Conta Bonus")
    void testConsultarDadosContaBonus() {
        ContaBonus contaBonus = new ContaBonus(2, 300.0);
        when(contaRepository.findByNumero(2)).thenReturn(Optional.of(contaBonus));

        ContaDTO contaDTO = contaService.consultarDados(2);

        assertThat(contaDTO)
                .isNotNull()
                .matches(dto -> dto.numero() == 2)
                .matches(dto -> dto.tipo() == 2) // Tipo bônus
                .matches(dto -> dto.saldo() == 300.0);
    }

    @Test
    @DisplayName("Consultar Dados Conta Poupança")
    void testConsultarDadosContaPoupanca() {
        ContaPoupanca contaPoupanca = new ContaPoupanca(3, 400.0);
        when(contaRepository.findByNumero(3)).thenReturn(Optional.of(contaPoupanca));

        ContaDTO contaDTO = contaService.consultarDados(3);

        assertThat(contaDTO)
                .isNotNull()
                .matches(dto -> dto.numero() == 3)
                .matches(dto -> dto.tipo() == 3) // Tipo poupança
                .matches(dto -> dto.saldo() == 400.0);
    }

    @Test
    @DisplayName("Consultar Dados Conta Não Existente")
    void testConsultarDadosContaNaoExistente() {
        when(contaRepository.findByNumero(4)).thenReturn(Optional.empty());

        final var exception = assertThrows(NotFoundException.class,
                () -> contaService.consultarDados(4));

        assertThat(exception).hasMessage("Conta não encontrada");
    }

    // TESTES EM CREDITAR CONTA
    @Test
    @DisplayName("Creditar valor inválido")
    public void testCreditarValorInvalido() {
        double valor = -1;

        final var exception = assertThrows(BadRequestException.class,
                () -> contaService.creditarConta(1, valor));

        assertThat(exception).hasMessage("Valor não pode ser negativo");

        verify(contaRepository, never()).findByNumero(anyInt());
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Creditar em conta não existente")
    public void testCreditarContaInexistente() {
        double valor = 100;

        when(contaRepository.findByNumero(1)).thenReturn(Optional.empty());

        final var exception = assertThrows(NotFoundException.class,
                () -> contaService.creditarConta(1, valor));

        assertThat(exception).hasMessage("Conta de origem ou destino não encontrada");

        verify(contaRepository).findByNumero(1);
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Creditar valor em conta existente")
    public void testCreditarValorEmContaExistente() {
        final var contaCaptor = ArgumentCaptor.forClass(Conta.class);
        final var conta = new Conta(1, 200);
        final var valor = 100;
        final var novoSaldo = conta.getSaldo() + valor;

        when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        contaService.creditarConta(conta.getNumero(), valor);

        verify(contaRepository).save(contaCaptor.capture());
        assertThat(contaCaptor.getValue())
                .matches(c -> c.getSaldo() == novoSaldo)
                .matches(c -> c.getNumero() == conta.getNumero());
    }

    @Test
    @DisplayName("Creditar valor em conta bonus")
    public void testCreditarValorEmContaBonus() {
        final var contaCaptor = ArgumentCaptor.forClass(ContaBonus.class);
        final var conta = new ContaBonus(1, 200);
        final var valor = 100;
        final var novoSaldo = conta.getSaldo() + valor;

        when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        contaService.creditarConta(conta.getNumero(), valor);

        verify(contaRepository).save(contaCaptor.capture());
        assertThat(contaCaptor.getValue())
                .isExactlyInstanceOf(ContaBonus.class)
                .matches(c -> c.getSaldo() == novoSaldo)
                .matches(c -> c.getNumero() == conta.getNumero());
    }

    @Test
    @DisplayName("Creditar valor em conta poupança")
    public void testCreditarValorEmContaPoupanca() {
        final var contaCaptor = ArgumentCaptor.forClass(ContaPoupanca.class);
        final var conta = new ContaPoupanca(1, 200);
        final var valor = 100;
        final var novoSaldo = conta.getSaldo() + valor;

        when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        contaService.creditarConta(conta.getNumero(), valor);

        verify(contaRepository).save(contaCaptor.capture());
        assertThat(contaCaptor.getValue())
                .isExactlyInstanceOf(ContaPoupanca.class)
                .matches(c -> c.getSaldo() == novoSaldo)
                .matches(c -> c.getNumero() == conta.getNumero());
    }

}
