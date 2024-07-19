/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package br.imd.sistemabancario.service;

import br.imd.sistemabancario.model.ContaPoupanca;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import br.imd.sistemabancario.model.Conta;
import br.imd.sistemabancario.model.ContaBonus;
import br.imd.sistemabancario.repository.ContaRepository;
import br.imd.sistemabancario.service.ContaService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {
    
    @Spy
    @InjectMocks
    private ContaService contaService;
    
    @Mock
    private ContaRepository contaRepository;
     
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }
    
    // Para funcao debitar ---------------
    @Test
    @DisplayName("Valor negativo para debitar")
    public void testDebitarValorNegativo() {
        Mockito.lenient().when(contaService.valorInvalido(-1)).thenReturn(true);
        
        contaService.debitarConta(1, -100);
        Mockito.verify(contaRepository, Mockito.times(0)).findByNumero(1); 
    }
    
    @Test
    @DisplayName("Debito com saldo insuficiente")
    public void testDebitarSaldoInsuficiente() {
        Conta conta = new Conta(1, 95);
        double valor = 100;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        Mockito.when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));
        
        
        var novoSaldo = conta.getSaldo() - valor;
        Mockito.lenient().when(contaService.validaNovoSaldo(conta, novoSaldo)).thenReturn(true);
        
        contaService.debitarConta(conta.getNumero(), valor);
        Assertions.assertEquals("Saldo insuficiente", outputStreamCaptor.toString()
        .trim());
        
    }
    
    @Test
    @DisplayName("Debito concluido conta normal")
    public void testDebitarSuccessNormal() {
        Conta conta = new Conta(1, 250);
        double valor = 100;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        Mockito.when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));
        
        contaService.debitarConta(conta.getNumero(), valor);
        
        conta.setSaldo(conta.getSaldo() - valor);
        Mockito.verify(contaRepository, Mockito.times(1)).save(conta);
        
    }
    
    @Test
    @DisplayName("Debito concluido conta bonus")
    public void testDebitarSuccessBonus() {
        ContaBonus conta = new ContaBonus(1, 250);
        double valor = 249.99;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        Mockito.when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));
        
        contaService.debitarConta(conta.getNumero(), valor);
        
        conta.setSaldo((conta.getSaldo() - valor) + valor/100);
        Mockito.verify(contaRepository, Mockito.times(1)).save(conta);
        
    }
    
    // Para funcao transferir ---------------
    @Test
    @DisplayName("Transferir valor invalido")
    public void testTransferirValorInvalido(){
        double valor = -1;
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(true);
        
        contaService.transferir(1, 2, valor);
        Mockito.verify(contaRepository, Mockito.times(0)).findByNumero(Mockito.anyInt()); 
        
    }
    
    @Test
    @DisplayName("Transferir com conta origem nao existente")
    public void testTransferirContaOrigemInexistente() {
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2,90);
        double valor = 10;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        
        Mockito.when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.empty());
        Mockito.when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));
        contaService.transferir(co.getNumero(), cd.getNumero(), valor);
        Assertions.assertEquals("Conta de origem ou destino não encontrada", outputStreamCaptor.toString()
        .trim());
          
    }
    
    @Test
    @DisplayName("Transferir com conta destino nao existente")
    public void testTransferirContaDestinoInexistente() {
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2,90);
        double valor = 10;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        
        Mockito.when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co));
        Mockito.when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.empty());
        contaService.transferir(co.getNumero(), cd.getNumero(), valor);
        Assertions.assertEquals("Conta de origem ou destino não encontrada", outputStreamCaptor.toString()
        .trim());
          
    }
    
    @Test
    @DisplayName("Transferir com conta origem e destino nao existente")
    public void testTransferirContaOrigemDestinoInexistente() {
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2,90);
        double valor = 10;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        
        Mockito.when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.empty());
        Mockito.when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.empty());
        contaService.transferir(co.getNumero(), cd.getNumero(), valor);
        Assertions.assertEquals("Conta de origem ou destino não encontrada", outputStreamCaptor.toString()
        .trim());
          
    }
    
    @Test
    @DisplayName("Saldo da transferencia da origem insuficiente")
    public void testTransferirContaNovoSaldoInsuficiente() {
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2,90);
        double valor = 101;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        
        Mockito.when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co)); // validaNovoSaldo
        Mockito.when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));
        
        Mockito.lenient().when(contaService.validaNovoSaldo(co, co.getSaldo()- valor)).thenReturn(true);
        contaService.transferir(co.getNumero(), cd.getNumero(), valor);
        Assertions.assertEquals("Saldo insuficiente", outputStreamCaptor.toString()
        .trim());
          
    }
    
    @Test
    @DisplayName("Transferencia concluida")
    public void testTransferirSuccess() {
        Conta co = new Conta(1, 100);
        Conta cd = new Conta(2,90);
        double valor = 99.9;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        
        Mockito.when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co));
        Mockito.when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));
        Mockito.lenient().when(contaService.validaNovoSaldo(co, co.getSaldo()- valor)).thenReturn(false);
        contaService.transferir(co.getNumero(), cd.getNumero(), valor);
        
        co.setSaldo(co.getSaldo() - valor);
        Mockito.verify(contaRepository, Mockito.times(1)).save(co);
        
        cd.setSaldo(cd.getSaldo() - valor);
        Mockito.verify(contaRepository, Mockito.times(1)).save(cd);
          
    }
    
    @Test
    @DisplayName("Transferencia concluida em conta bonus")
    public void testTransferirSuccessContaBonus() {
        Conta co = new Conta(1, 100);
        ContaBonus cd = new ContaBonus(2,90);
        double valor = 99.9;
        
        Mockito.lenient().when(contaService.valorInvalido(valor)).thenReturn(false);
        
        Mockito.when(contaRepository.findByNumero(co.getNumero())).thenReturn(Optional.of(co));
        Mockito.when(contaRepository.findByNumero(cd.getNumero())).thenReturn(Optional.of(cd));
        Mockito.lenient().when(contaService.validaNovoSaldo(co, co.getSaldo()- valor)).thenReturn(false);
        contaService.transferir(co.getNumero(), cd.getNumero(), valor);
        
        co.setSaldo(co.getSaldo() - valor);
        Mockito.verify(contaRepository, Mockito.times(1)).save(co);
        
        int bonus = (int) (cd.getBonus() + (int) valor/150);
        cd.setBonus(bonus);
        cd.setSaldo(cd.getSaldo() - valor);
        Mockito.verify(contaRepository, Mockito.times(1)).save(cd);
          
    }

    // TESTES EM CADASTRAR CONTA
    @Test
    @DisplayName("Cadastrar Conta Padrão")
    void testCadastrarContaTipo1() {
        contaService.cadastrarConta(1, 1, 100.0);
        Mockito.verify(contaRepository, Mockito.times(1)).save(new Conta(1, 100.0));
    }

    @Test
    @DisplayName("Cadastrar Conta Bonus")
    void testCadastrarContaTipo2() {
        contaService.cadastrarConta(2, 2, 200.0);
        Mockito.verify(contaRepository, Mockito.times(1)).save(new ContaBonus(2, 200.0));
    }

    @Test
    @DisplayName("Cadastrar Conta Poupança")
    void testCadastrarContaTipo3() {
        contaService.cadastrarConta(3, 3, 300.0);
        Mockito.verify(contaRepository, Mockito.times(1)).save(new ContaPoupanca(3, 300.0));
    }

    @Test
    @DisplayName("Cadastrar Conta com Saldo Zero")
    void testCadastrarContaComSaldoZero() {
        contaService.cadastrarConta(4, 1, 0.0);
        Mockito.verify(contaRepository, Mockito.times(1)).save(new Conta(4, 0.0));
    }

    @Test
    @DisplayName("Cadastrar Conta com Saldo Negativo")
    void testCadastrarContaComSaldoNegativo() {
        // TODO acrescentar essa validação no service
        contaService.cadastrarConta(5, 2, -50.0);
        Mockito.verify(contaRepository, Mockito.times(1)).save(new ContaBonus(5, -50.0));
    }

    @Test
    @DisplayName("Cadastrar Conta com Tipo Inválido")
    void testCadastrarContaComTipoInvalido() {
        contaService.cadastrarConta(6, 4, 100.0);
        Mockito.verify(contaRepository, Mockito.never()).save(new Conta(6, 100.0));
    }

    @Test
    @DisplayName("Cadastrar Conta com Número Negativo")
    void testCadastrarContaComNumeroNegativo() {
        // TODO acrescentar essa validação no service
        contaService.cadastrarConta(-1, 1, 100.0);
        Mockito.verify(contaRepository, Mockito.times(1)).save(new Conta(-1, 100.0));
    }

    @Test
    @DisplayName("Cadastrar Conta com Saldo Alto")
    void testCadastrarContaComSaldoAlto() {
        contaService.cadastrarConta(7, 3, 1_000_000.0);
        Mockito.verify(contaRepository, Mockito.times(1)).save(new ContaPoupanca(7, 1_000_000.0));
    }

    // TESTES EM CONSULTAR CONTA
    // TODO

    // TESTES EM CONSULTAR SALDO
    @Test
    @DisplayName("Consultar Saldo Conta Bonus")
    void testConsultarSaldoContaBonus() {
        ContaBonus contaBonus = new ContaBonus(1, 100.0);
        contaBonus.setBonus(50);
        Mockito.when(contaRepository.findByNumero(1)).thenReturn(Optional.of(contaBonus));

        contaService.consultarSaldo(1);

        String expectedOutput = "O bônus da conta é: 50.0\nO saldo da conta é: 100.0";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    @DisplayName("Consultar Saldo Conta Padrão")
    void testConsultarSaldoConta() {
        Conta conta = new Conta(2, 200.0);
        Mockito.when(contaRepository.findByNumero(2)).thenReturn(Optional.of(conta));

        contaService.consultarSaldo(2);

        String expectedOutput = "O saldo da conta é: 200.0";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    @DisplayName("Consultar Saldo Conta Não Existente")
    void testConsultarSaldoContaNaoExistente() {
        Mockito.when(contaRepository.findByNumero(3)).thenReturn(Optional.empty());

        contaService.consultarSaldo(3);

        String expectedOutput = "Conta não existe!";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    // TESTES EM CREDITAR CONTA
    @Test
    @DisplayName("Creditar valor inválido")
    public void testCreditarValorInvalido() {
        double valor = -1;

        contaService.creditarConta(1, valor);
        Mockito.verify(contaRepository, Mockito.times(0)).findByNumero(Mockito.anyInt());

        String expectedOutput = "Valor não pode ser negativo";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    @DisplayName("Creditar em conta não existente")
    public void testCreditarContaInexistente() {
        // TODO acrescentar mensagem de retorno nessa validação no service
        double valor = 100;

        Mockito.when(contaRepository.findByNumero(1)).thenReturn(Optional.empty());

        contaService.creditarConta(1, valor);
        Mockito.verify(contaRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    @DisplayName("Creditar valor em conta existente")
    public void testCreditarPadrao() {
        Conta conta = new Conta(1, 200);
        double valor = 100;

        Mockito.when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        double saldoEsperado = conta.getSaldo() + valor;

        contaService.creditarConta(conta.getNumero(), valor);

        Mockito.verify(contaRepository, Mockito.times(1)).save(conta);
        assertEquals(saldoEsperado, conta.getSaldo());
    }

    @Test
    @DisplayName("Creditar valor em conta bônus")
    public void testCreditarBonus() {
        ContaBonus conta = new ContaBonus(1, 200);
        double valor = 150;

        Mockito.when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        double saldoEsperado = conta.getSaldo() + valor;
        double bonusEsperado = conta.getBonus();

        contaService.creditarConta(conta.getNumero(), valor);

        Mockito.verify(contaRepository, Mockito.times(1)).save(conta);
        assertEquals(saldoEsperado, conta.getSaldo());
        assertEquals(bonusEsperado, conta.getBonus());
    }

    @Test
    @DisplayName("Creditar valor em conta poupança")
    public void testCreditarPoupanca() {
        ContaPoupanca conta = new ContaPoupanca(1, 300);
        double valor = 200;

        Mockito.when(contaRepository.findByNumero(conta.getNumero())).thenReturn(Optional.of(conta));

        double saldoEsperado = conta.getSaldo() + valor;

        contaService.creditarConta(conta.getNumero(), valor);

        Mockito.verify(contaRepository, Mockito.times(1)).save(conta);
        assertEquals(saldoEsperado, conta.getSaldo());
    }

}
